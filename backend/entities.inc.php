<?php

require_once "user_input.inc.php";
require_once "error.inc.php";
require_once "query.php";

class Account {
	public int $id;
	public string $username;
	public string $biography;
	public string $phone_number;
	public DateTime $created_at;
	public int $account_rank;
	public int $accepted_contributions;

	private function __construct(
					int $id, 
					string $username, 
					string $biography, 
					string $phone_number, 
					DateTime $created_at,
					int $account_rank,
					int $accepted_contributions)
	{
		$this->id = $id;
		$this->username = $username;
		$this->biography = $biography;
		$this->phone_number = $phone_number;
		$this->created_at = $created_at;
		$this->account_rank = $account_rank;
		$this->accepted_contributions = $accepted_contributions;
	}

	public static function request_pending_contributions(): void {
		self::require_login();
		try {
			# TODO: this must also show the basket of the contribution even though it does not currently
			expect_get();
			$result = Database::result_to_json(Database::select(DBQuery::from_stored("get_pending_contributions.sql"), "i", self::get_session()->id));
			error_log($result);
			echo $result;
		} catch (Exception $e)  {
			error_log($e->getMessage());
			exit_with_status(message: "Server encountered error fetching pending contributions.", status_code: 500);
		}
	}

	private static function validate_user_registry_input(mixed $username, mixed $password, mixed $biography, mixed $phone_number): void {
		validate_username($username);
		validate_password($password);
		validate_biography($biography);
		validate_phone_number($phone_number);
	}

	private static function validate_user_login_input(mixed $username, mixed $password): void {
		validate_username($username);
		validate_password($password);
	}

	public static function register(mixed $username, mixed $password, mixed $biography, mixed $phone_number): self|false {
		if (self::is_logged_in()) {
			exit_with_status(message: "Already logged in.", status_code: 400);
		}

		self::validate_user_registry_input($username, $password, $biography, $phone_number);
		$hashed_password = password_hash(password: $password, algo: PASSWORD_DEFAULT);

		$created_at = new DateTime();
		$count_users = Database::select(DBQuery::from_stored("select_count_accounts.sql"), "", "")->fetch_assoc()["total"];
		$query = DBQuery::from_stored("register_user.sql");
		try  {
			$id = Database::insert(true, $query, "ssssi", $username, $hashed_password, $phone_number, $biography, $count_users);
		} catch (Exception $e) {
			error_log($e);
			exit_with_status(message: "Account with that username already exists.", status_code: 400);	
		}
		
		session_regenerate_id(true);		
		return $_SESSION["__user"] = new self($id, $username, $biography, $phone_number, $created_at, $count_users + 1, 0);
	}

	public static function is_logged_in(): bool {
		session_start();
		return $_SESSION["__user"] !== null;
	}

	public static function get_logged_in(): Account|false {
		session_start();
		if (!Account::is_logged_in()) {
			return false;
		}	

		return $_SESSION["__user"];
	}

	public static function logout(): void {
		$_SESSION["__user"] = null;
	}

	public static function require_login(): void {
		if (!self::is_logged_in()) {
			exit_with_status(message: "Must be logged in", status_code: 400);
		}
	}

	public static function &get_session(): Account {
		return $_SESSION["__user"];
	}

	public static function require_login_of_user(int $id): void {
		$instance = &Account::get_logged_in();
		if ($instance === false) {
			exit_with_status(message: "Not logged in.", status_code: 400);
		}

		if ($instance->id !== $id) {
			exit_with_status(message: "Not permitted.", status_code: 400);
		}
	}

	public static function accept_contribution(Contribution $contribution): void {
		self::require_login();
		$row = Database::select(DBQuery::from_stored("select_page_donatee.sql"), "i", $contribution->recipient_page_id)->fetch_assoc();
		self::require_login($row["donatee_id"]);
		$pending_status_query = DBQuery::from_stored("select_pending_contribution_by_id.sql");
		$row = Database::select($pending_status_query, "i", $contribution->id);
		if (!$row) {
			exit_with_status("Cannot accept non-pending contribution");
		}
		

		$accepted_contribution_delta = 0;

		foreach ($contribution->content as $item) {
			$accepted_contribution_delta += $item->quantity;
		}
			
		$rank_query = DBQuery::from_stored("update_account_ranks.sql");
		$page_entry_query = DBQuery::from_stored("update_page_entries.sql");
		$contribution_entry_query = DBQuery::from_stored("insert_accepted_entry.sql");
		try {
			$old_account_details = Database::select(DBQuery::from_stored("select_user_by_id.sql"), "i", $contribution->poster_id)->fetch_assoc();
			$old_accepted_contributions = $old_account_details["accepted_contributions"];
			$account_created_at = $old_account_details["created_at"];
			$new_accepted_contributions = $old_accepted_contributions + $accepted_contribution_delta;
			error_log($old_accepted_contributions);
			$row = Database::select(DBQuery::from_stored("select_account_rank.sql"), 
				"ii", 
				$old_accepted_contributions, 
				$new_accepted_contributions)->fetch_assoc();
			if (!$row) {
				exit_with_status("Server is sad :(", status_code: 500);
			}
			error_log(json_encode($row));
			$new_rank = $row["value"];
			

			Database::update(true, DBQuery::from_stored("update_account_contributions.sql"), "ii", $accepted_contribution_delta, $contribution->poster_id);

			Database::update(true, 
				$rank_query, 
				"iiisiiiii", 
				$old_accepted_contributions,
				$new_accepted_contributions,
				$new_accepted_contributions,
				$account_created_at,
				$contribution->poster_id,
				$contribution->poster_id,
				$new_rank,
				$old_accepted_contributions,
				$new_accepted_contributions
			);

			// TODO: Add adding accepted contribution entries.
			foreach ($contribution->content as $entry) {
				Database::insert(true, $contribution_entry_query, "i", $entry->id);
				Database::update(true, $page_entry_query, "ii", $entry->quantity, $contribution->recipient_page_id);
			}
			Database::delete(true, DBQuery::from_stored("delete_pending_contribution.sql"), "i", $contribution->id);
		} catch (Exception $e) {
			error_log($e->getMessage());
			exit_with_status(message: "Server is sad, server has failed in updating ranks :(", status_code: 500);
		}
	}

	public static function login(mixed $username, mixed $password): self|false {
		if (self::is_logged_in()) {
			exit_with_status(message: "Already logged in.", status_code: 400);
		}

		self::validate_user_login_input($username, $password);
		$hashed_password = password_hash(password: $password, algo: PASSWORD_DEFAULT);
		$query = DBQuery::from_stored("select_user_by_username.sql");

		try {
			$result_set = Database::select($query, "s", $username);
		} catch (Exception $e) {
			error_log($e);
			exit_with_status(message: "Failed executing query.", status_code: 500);
		}
		
		$fst_row = $result_set->fetch_assoc();

		if (!$fst_row || $fst_row["username"] != $username)  {
			exit_with_status("Account does not exist.", status_code: 400);
		}

		if (!password_verify(password: $password, hash: $fst_row["password_hash"])) {
			exit_with_status("Incorrect password.", status_code: 400);
		}

		session_regenerate_id(true);		
		return $_SESSION["__user"] = new self(
			$fst_row["id"], 
			$fst_row["username"], 
			$fst_row["biography"], 
			$fst_row["phone_number"], 
			DateTime::createFromFormat('Y-m-d H:i:s', $fst_row["created_at"]),
			$fst_row["account_rank"],
			$fst_row["accepted_contributions"]
		);
	}
}

class AccountReport {
	public int $id;
	public int $reported_id;
	public int $reporter_id;
	public string $description;
	public DateTime $created_at;

	public function __construct(int $id, int $reported_id, int $reporter_id, string $description, DateTime $created_at) {
		$this->id = $id;
		$this->reported_id = $reported_id;
		$this->reporter_id = $reporter_id;
		$this->description = $description;
		$this->created_at = $created_at;
	}

	public static function from_json(mixed $json_object): AccountReport {
		return null;
	}
}

class DonationPageEntry {
	public int $id;
	public int $page_id;
	public int $resource_id;
	public int $quantity_asked;
	public int $quantity_received;

	public function __construct(int $id, int $page_id, int $resource_id, int $quantity_asked, int $quantity_received)  {
		$this->id = $id;
		$this->page_id = $page_id;
		$this->resource_id = $resource_id;
		$this->quantity_asked = $quantity_asked;
		$this->quantity_received = $quantity_received;
	}
}

function validate_basket_content(&$basket_content): void {
	if ($basket_content === null) {
		exit_with_status(message: "Missing basket content.", status_code: 400);
	}

	if (!is_array($basket_content)) {
		exit_with_status(message: "Basket content is in the wrong format.", status_code: 400);
	}

	if (array_values($basket_content) !== $basket_content) {
		exit_with_status(message: "Basket must be a non-associative array.", status_code: 400);
	}

	if (sizeof($basket_content) == 0) {
		exit_with_status(message: "Basket may not be empty.", status_code: 400);
	}
	
	$resource_set = Resources::get_resource_set();

	$resources = array();
	while (($row = $resource_set->fetch_assoc())) {
		$resources[] = $row;
	}

	foreach ($basket_content as $item) {
		$quantity = $item["quantity"];
		if (!is_int($quantity)) {
			exit_with_status(message: "Basket content presented in the wrong format. Expected int.", status_code: 400);
		}

		if ($quantity <= 0 || $quantity > 1000000) {
			exit_with_status(message: "Basket quantity is too high or too low.", status_code: 400);
		}

		$found_match = false;	
		foreach ($resources as $resource) {
			if ($resource["id"] === $item["resource_id"]) {
				$found_match = true;
				break;
			}
		}

		if (!$found_match) {
			exit_with_status(message: "Invalid resource id.", status_code: 400);
		}

	}
}

class DonationPage {
	public int $id;
	public int $donatee_id;
	public string $name;
	public DateTime $created_at;

	public function __construct(int $id, int $donatee_id, string $name, DateTime $created_at) {
		$this->id = $id;
		$this->donatee_id = $donatee_id;
		$this->name = $name;
		$this->created_at = $created_at;
		
	}

	public static function insert_from_json(mixed $json_object): DonationPage {
		Account::require_login();

		if (!isset($json_object["basket"])) {
			exit_with_status(message: "Missing basket.", status_code: 400);	
		}

		validate_page_name($json_object["name"]);
		validate_page_content($json_object["page_content"]);
		
		$donatee_id = Account::get_session()->id;
		
		$name = $json_object["name"];
		$created_at = new DateTime();
		$basket_content = $json_object["basket"]["content"];	
		validate_basket_content($basket_content);

		try {
			$query = DBQuery::from_stored("insert_donation_page.sql");
			$page_id = Database::insert(true, $query, "is", $donatee_id, $name);

			foreach ($basket_content as $item) {
				$quantity_asked = $item["quantity"];
				$resource_id = $item["resource_id"];
				$query = &DBQuery::from_stored("insert_donation_page_entry.sql");
				$entry_id = Database::insert(true, $query, "iii", $page_id, $resource_id, $quantity_asked);
			}
		} catch (Exception $e) {
			error_log($e);
			exit_with_status("Page name already exists.", status_code: 400);
		}	
		
		$file = fopen("user/" . $name . ".html", "a");
		
		if ($file) {
			fwrite($file, $json_object["page_content"]);
			fclose($file);
		}

		return new DonationPage($page_id, $donatee_id, $name, $created_at);
	}
}

class ContributionEntry {
	public int $id;
	public int $resource_id;
	public int $contribution_id;
	public int $quantity;

	public function __construct(int $id, int $resource_id, int $contribution_id, int $quantity) {
		$this->id = $id;
		$this->resource_id = $resource_id;
		$this->contribution_id = $contribution_id;
		$this->quantity = $quantity;
	}
}

class Contribution {
	public int $id;
	public int $poster_id;
	public int $recipient_page_id;
	public DateTime $created_at;
	public array $content;

	public function __construct(int $id, int $poster_id, int $recipient_page_id, DateTime $created_at, array $content) {
		$this->id = $id;
		$this->poster_id = $poster_id;
		$this->recipient_page_id = $recipient_page_id;
		$this->created_at = $created_at;
		$this->content = $content;
	}

	public static function from_json(array $json_object): Contribution {
		Account::require_login();
		if ($json_object === null) {
			exit_with_status(message: "Missing json", status_code: 400);
		}

		if ($json_object["basket"] === null) {
			exit_with_status(message: "Missing basket.", status_code: 400);	
		}

		$contribution_id = $json_object["id"];	
		$poster_id = $json_object["poster_id"];
		$recipient_page_id = $json_object["recipient_page_id"];
		$raw_items = $json_object["basket"]["content"];
		$items = array();
		$created_at = null;

		error_log(json_encode($json_object));

		if (!is_int($contribution_id) || !is_int($poster_id) || !is_int($recipient_page_id) || $raw_items === null) {
			exit_with_status("Atleast one item is presented in the wrong format.", status_code: 400);
		}

		if (!is_array($raw_items) || sizeof($raw_items) == 0)  {
			exit_with_status("Items are presented in the wrong format.", status_code: 400);
		}

		$row = Database::select(DBQuery::from_stored("select_page_donatee.sql"), "i", $recipient_page_id)->fetch_assoc();
		if (!$row) {
			exit_with_status(message: "Cannot donate to a page that does not exist.", status_code: 400);
		}
		
		$recipient_account_id = $row["donatee_id"];

		Account::require_login($recipient_account_id);

		if ($recipient_account_id === $poster_id) {
			exit_with_status(message: "Cannot donate to oneself.", status_code: 400);
		}

		try {
			$query_string = "SELECT * FROM Contributions WHERE Contributions.id = ?";
			$query = DBQuery::from_string($query_string);
			$result = Database::select($query, "i", $contribution_id);
			$row = $result->fetch_assoc();
			if (!$row) {
				exit_with_status("Post does not exist.", status_code: 400);
			}

			if ($row["poster_id"] !== $poster_id || $row["recipient_page_id"] !== $recipient_page_id) {
				exit_with_status("Mismatch in given id with respect to given poster and recipient id.", status_code: 400);
			}

			$created_at = DateTime::createFromFormat('Y-m-d H:i:s', $row["created_at"]);
			$query_string = "SELECT * FROM ContributionEntries WHERE ContributionEntries.id = ?";
			$query = DBQuery::from_string($query_string);

			foreach ($raw_items as $raw_item) {
				$id = $raw_item["id"];
				if (!is_int($id)) {
					exit_with_status("Item it presented in the wrong format.", status_code: 400);
				}

				$result = Database::select($query, "i", $id);

				while (($row = $result->fetch_assoc())) {
					if (!$row) {
						exit_with_status("Item does not exist.", status_code: 400);
					}
					
					if ($row["contribution_id"] !== $id) {
						exit_with_status("Entry not linked with account.", status_code: 400);
					}
					
					$items[] = new ContributionEntry($id, $row["resource_id"], $row["contribution_id"], $row["quantity"]);
				}
			}	
		} catch (Exception $e) {
			error_log($e->getMessage());
			exit_with_status("Mismatch in given id with respect to given poster and recipient id.", status_code: 400);
		}
		
		return new self($id, $poster_id, $recipient_page_id, $created_at, $items);
		
	}

	public static function insert_from_json(array $json_object): Contribution {
		Account::require_login();
		$basket_content = $json_object["basket"]["content"];
		$poster_id = $json_object["poster_id"];
		$recipient_page_id = $json_object["recipient_page_id"];

		// TODO: Check if the poster_id and recipient_page_id even exist
		// Check if each contribution entry is associated with a DonationPageEntry

		if (!isset($json_object["basket"])) {
			exit_with_status(message: "Missing basket.", status_code: 400);	
		}


		if ($poster_id === null || $recipient_page_id === null) {
			exit_with_status(message: "Missing recipient or poster id.", status_code: 400);
		}
		
		if (!is_int($poster_id) || !is_int($recipient_page_id)) {
			exit_with_status(message: "Poster or recipient id is in the wrong format.", status_code: 400);
		}
		
		Account::require_login_of_user($poster_id);

		$row = Database::select(DBQuery::from_stored("select_page_donatee.sql"), "i", $recipient_page_id)->fetch_assoc();
		if (!$row) {
			exit_with_status(message: "Cannot donate to a page that does not exist.", status_code: 400);
		}

		if ($row["donatee_id"] === $poster_id) {
			exit_with_status(message: "Cannot donate to oneself.", status_code: 400);
		}

		validate_basket_content($basket_content);

		try {
			$query = DBQuery::from_stored("check_contribution_entry.sql");
			foreach ($basket_content as $item) {
				$row = Database::select($query, "ii", $recipient_page_id, $item["resource_id"])->fetch_assoc();
				if (!$row) {
					exit_with_status("Mismatch in asked for items to given items.", status_code: 400);
				}
			}

			$created_at = new DateTime();
			$query = DBQuery::from_stored("insert_contribution.sql");
			$id = Database::insert(true, $query, "ii", $poster_id, $recipient_page_id);
			$query = DBQuery::from_stored("insert_pending_contribution.sql");
			Database::insert(true, $query, "i", $id);
			$result_entries = array();

			$query = DBQuery::from_stored("insert_contribution_entry.sql");
			foreach ($basket_content as $item) {
				$item_id = Database::insert(true, $query, "iii", $item["resource_id"], $id, $item["quantity"]);
				$entry = new ContributionEntry($item_id, $item["resource_id"], $id, $item["quantity"]);
				$result_entries[] = $entry;
			}
		} catch (Exception $e) {
			error_log($e);
			exit_with_status(message: "Invalid identifiers.", status_code: 400);
		}

		return new self($id, $poster_id, $recipient_page_id, $created_at, $result_entries);
	}
}

class Resources {
	public int $id;
	public string $name;
	public string $description;
	
	public static ?mysqli_result $resource_set = null;

	public function __construct(int $id, string $name, string $description) {
		$this->id = $id;
		$this->name = $name;
		$this->description = $description;
	}

	public static function get_resource_set(): mysqli_result {
		if (self::$resource_set !== null) {
			return self::$resource_set;
		}

		$query = DBQuery::from_stored("select_resources.sql");
		return self::$resource_set = Database::select($query, "", "");
	}

	public static function request_resources(): void {
		expect_get();
		echo Database::result_to_json(self::get_resource_set());
	}	
}
?>
