<?php
/* It's probably convenient to represent the different entities as php objects */


class Account 
{
	public int $id;
	public string $username;
	public string $biography;
	public string $phone_number;
	public DateTime $created_at;
	public int $account_rank;
	public int $accepted_donations;

	private function __construct(
					int $id, 
					string $username, 
					string $biography, 
					string $phone_number, 
					DateTime $created_at,
					int $account_rank,
					int $accepted_donations)
	{
		$this->id = $id;
		$this->username = $username;
		$this->biography = $biography;
		$this->phone_number = $phone_number;
		$this->created_at = $created_at;
		$this->account_rank = $account_rank;
		$this->accepted_donations = $accepted_donations;
	}

	public static function request_pending_donations(): void
	{
		self::require_login();
		try
		{
			echo Database::result_to_json(Database::select(DatabaseQuery::from_file("queries/get_pending_donations.sql"), "i", self::get_session()["id"]));
		}
		catch (Exception $e) 
		{
			error_log($e->getMessage());
			exit_with_status(message: "Server encountered error fetching pending donations.", status_code: 500);
		}
	}

	private static function validate_user_registry_input(mixed $username, mixed $password, mixed $biography, mixed $phone_number): void
	{
		require_once 'user_input.inc.php';
		require_once 'query.php';
		require_once "error.inc.php";
		validate_username($username);
		validate_password($password);
		validate_biography($biography);
		validate_phone_number($phone_number);

	}

	private static function validate_user_login_input(mixed $username, mixed $password): void
	{
		require_once 'user_input.inc.php';
		require_once 'query.php';
		require_once "error.inc.php";
		validate_username($username);
		validate_password($password);
	}

	public static function register(mixed $username, mixed $password, mixed $biography, mixed $phone_number): self|false
	{
		require_once "error.inc.php";
		if (self::is_logged_in())
		{
			exit_with_status(message: "Already logged in.", status_code: 400);
		}

		self::validate_user_registry_input($username, $password, $biography, $phone_number);
		$hashed_password = password_hash(password: $password, algo: PASSWORD_DEFAULT);

		$created_at = new DateTime();
		$query = DatabaseQuery::from_file("queries/register_user.sql");
		try 
		{
			$id = Database::insert(true, $query, "ssss", $username, $hashed_password, $phone_number, $biography);
		} 
		catch (Exception $e)
		{
			error_log($e);
			exit_with_status(message: "Account with that username already exists.", status_code: 400);	
		}
		
		return $_SESSION["__user"] = new self($id, $username, $biography, $phone_number, $created_at, $id, 0);
	}

	public static function is_logged_in(): bool
	{
		return isset($_SESSION["__user"]);
	}

	public static function get_logged_in(): Account|false
	{
		if (!Account::is_logged_in())
		{
			return false;
		}	

		return $_SESSION["__user"];
	}

	public static function require_login(): void 
	{
		if (!self::is_logged_in()) 
		{
			exit_with_status(message: "Must be logged in", status_code: 400);
		}
	}

	public static function &get_session(): Account
	{
		return $_SESSION["__user"];
	}

	public static function require_login_of_user(int $id): void
	{
		$instance = &Account::get_logged_in();
		if ($instance === false)
		{
			exit_with_status(message: "Not logged in.", status_code: 400);
		}

		if ($instance->id !== $id)
		{
			exit_with_status(message: "Not permitted.", status_code: 400);
		}
	}

	public static function accept_donation(DonationPost $post): void
	{
		self::require_login($post->recipient_id);
		$accepted_donation_delta = 0;

		foreach ($post->contents as $item)
		{
			$accepted_donation_delta += $item.quantity;
		}
			
		$id = $post->poster_id;
		$query = DatabaseQuery::from_file("queries/update_ranks.sql");
		try
		{
			Database::update
			(
				true, 
				$query,
				"iiiii",
				$id,
				$accepted_donation_delta,
				$id,
				$id,
				$id
			);
		}
		catch (Exception $e)
		{
			error_log($e->getMessage());
			exit_with_status(message: "Server is sad, server has failed in updating ranks :(", status_code: 500);
		}
	}

	public static function login(mixed $username, mixed $password): self|false
	{
		require_once "error.inc.php";
		if (self::is_logged_in())
		{
			exit_with_status(message: "Already logged in.", status_code: 400);
		}

		self::validate_user_login_input($username, $password);
		$hashed_password = password_hash(password: $password, algo: PASSWORD_DEFAULT);
		$query = DatabaseQuery::from_file("queries/select_user_by_username");

		try
		{
			$result_set = Database::select($query, "s", $username);
		}
		catch (Exception $e)
		{
			error_log($e);
			exit_with_status(message: "Failed executing query.", status_code: 500);
		}
		
		$fst_row = $result_set->fetch_assoc();

		if (!$fst_row || $fst_row["username"] != $username) 
		{
			exit_with_status("Account does not exist.", status_code: 400);
		}

		if (!password_verify(password: $password, hash: $fst_row["password_hash"]))
		{
			exit_with_status("Incorrect password.", status_code: 400);
		}

		return $_SESSION["__user"] = new self(
			$fst_row["id"], 
			$fst_row["username"], 
			$fst_row["biography"], 
			$fst_row["phone_number"], 
			DateTime::createFromFormat('Y-m-d H:i:s', $fst_row["created_at"]),
			$fst_row["account_rank"],
			$fst_row["accepted_donations"]
		);
	}
}

class AccountReport 
{
	public int $id;
	public int $reported_id;
	public int $reporter_id;
	public string $description;
	public DateTime $created_at;

	public function __construct(int $id, int $reported_id, int $reporter_id, string $description, DateTime $created_at) 
	{
		$this->id = $id;
		$this->reported_id = $reported_id;
		$this->reporter_id = $reporter_id;
		$this->description = $description;
		$this->created_at = $created_at;
	}

	public static function from_json(mixed $json_array): AccountReport
	{
		return null;
	}
}

class DonationPageEntry 
{
	public int $id;
	public int $page_id;
	public int $resource_id;
	public int $quantity_asked;
	public int $quantity_received;

	public function __construct(int $id, int $page_id, int $resource_id, int $quantity_asked, int $quantity_received) 
	{
		$this->id = $id;
		$this->page_id = $page_id;
		$this->resource_id = $resource_id;
		$this->quantity_asked = $quantity_asked;
		$this->quantity_received = $quantity_received;
	}
}

class DonationPage 
{
	public int $id;
	public int $donatee_id;
	public string $name;
	public DateTime $created_at;

	public function __construct(int $id, int $donatee_id, string $name, DateTime $created_at)
	{
		$this->id = $id;
		$this->donatee_id = $donatee_id;
		$this->name = $name;
		$this->created_at = $created_at;
		
	}

	private static function validate_basket_content(&$basket_content): void
	{
		if ($basket_content === null)
		{
			exit_with_status(message: "Missing basket content.", status_code: 400);
		}

		if (!is_array($basket_content) || array_values($basket_content) !== $basket_content)
		{
			exit_with_status(message: "Basket content is in the wrong format.", status_code: 400);
		}
		
		$resources = Database::result_to_json(Resources::get_resource_set());

		foreach ($basket_content as $item)
		{
			$quantity_asked = $basket_content["quantity_asked"];
			if (!is_int($quantity_asked))
			{
				exit_with_status(message: "Basket content presented in the wrong format.", status_code: 400);
			}

			if ($quantity_asked <= 0 || $quantity_asked > 1000000)
			{
				exit_with_status(message: "Basket quantity is too high or too low.", status_code: 400);
			}

			$found_match = false;	
			foreach ($resources as $resource)
			{
				if ($resources["id"] === $item["resource_id"])
				{
					$found_match = true;
					break;
				}
			}

			if (!$found_match)
			{
				exit_with_status(message: "Invalid resource id.", status_code: 400);
			}

		}
	}


	public static function insert_from_json(mixed $json_array): void
	{
		Account::require_login();
		require_once "user_input.inc.php";
		validate_page_name($json_array["name"]);
		validate_page_content($json_array["page_content"]);
		
		$donatee_id = Account::get_session()["id"];
		
		$name = $json_array["name"];
		$created_at = new DateTime();
		
		try
		{
			$query = DatabaseQuery::from_file("queries/insert_donation_page.sql");
			$page_id = Database::insert(true, $query, "is", $donatee_id, $name);
			$basket_content = $json_array["basket_content"];	
			self::validate_basket_content($basket_content);

			foreach ($basket_content as $item)
			{
				$quantity_asked = $item["quantity_asked"];
				$resource_id = $item["resource_id"];
				$query = &DatabaseQuery::from_file("queries/insert_donation_page_entry.sql");
				$page_id = Database::insert(true, $query, "iii", $page_id, $resource_id, $quantity_asked);
			}
		}
		catch (Exception $e)
		{
			error_log($e);
			exit_with_status("Page name already exists.", status_code: 400);
		}	
		
		$file = fopen("users/" . $name . ".html", "w");
		fwrite($file, $json_array["page_content"]);
	}
}

class DonationPostEntry 
{
	public int $id;
	public int $resource_id;
	public int $post_id;
	public int $quantity;

	public function __construct(int $id, int $resource_id, int $post_id, int $quantity) 
	{
		$this->id = $id;
		$this->resource_id = $resource_id;
		$this->post_id = $post_id;
		$this->quantity = $quantity;
	}

	public static function insert_from_json(mixed $json_array): void
	{
		Account::require_login();
	}
}

class DonationPost 
{
	public int $id;
	public int $poster_id;
	public int $recipient_id;
	public DateTime $created_at;
	public array $contents;

	public function __construct(int $id, int $poster_id, int $recipient_id, DateTime $created_at, array $contents) 
	{
		$this->id = $id;
		$this->poster_id = $poster_id;
		$this->recipient_id = $recipient_id;
		$this->created_at = $created_at;
		$this->contents = $contents;
	}

	private static function validate_basket_content(&$basket_content): void
	{
		if ($basket_content === null)
		{
			exit_with_status(message: "Missing basket content.", status_code: 400);
		}

		if (!is_array($basket_content) || array_values($basket_content) !== $basket_content)
		{
			exit_with_status(message: "Basket content is in the wrong format.", status_code: 400);
		}
		
		$resources = Database::result_to_json(Resources::get_resource_set());

		foreach ($basket_content as $item)
		{
			$quantity = $item["quantity"];
			if (is_int($quantity) || $quantity <= 0 || $quantity > 10000)
			{
				exit_with_status(message: "Invalid quantity.", status_code: 400);
			}
			
			$found_match = false;	
			foreach ($resources as $resource)
			{
				if ($resources["id"] === $item["resource_id"])
				{
					$found_match = true;
					break;
				}
			}

			if (!$found_match)
			{
				exit_with_status(message: "Invalid resource id.", status_code: 400);
			}

		}
	}

	public static function from_json($json_array): DonationPost
	{
		Account::require_login();
		if ($json_array === null)
		{
			exit_with_status(message: "Missing json", status_code: 400);
		}

		$id = $json_array["id"];	
		$poster_id = $json_array["poster_id"];
		$recipient_id = $json_array["recipient_id"];
		$raw_items = $json_array["contents"];
		$items = array();
		$created_at = null;

		if (!is_int($id) || !is_int($poster_id) || !is_int($recipient_id) || true || $raw_items === null)
		{
			exit_with_status("Atleast one item is presented in the wrong format.", status_code: 400);
		}

		try 
		{
			$query_string = "SELECT * FROM DonationPosts WHERE DonationPosts.id = ?";
			$query = DatabaseQuery::from_string($query_string);
			$result = Database::select($query, "i", $id);
			$row = $result->fetch_assoc();
			if (!$row)
			{
				exit_with_status("Post does not exist.", status_code: 400);
			}

			if ($row["poster_id"] !== $poster_id || $row["recipient_id"] !== $recipient_id)
			{
				exit_with_status("Mismatch in given id with respect to given poster and recipient id.", status_code: 400);
			}

			$created_at = $row["created_at"];
			$query_string = "SELECT * FROM DonationPostEntries WHERE DonationPostEntries.id = ?";
			$query = DatabaseQuery::from_string($query_string);

			foreach ($raw_items as $raw_item)
			{
				$id = $raw_item["id"];
				if (!is_int($id))
				{
					exit_with_status("Item it presented in the wrong format.", status_code: 400);
				}

				$result = Database::select($query, "i", $id);
				$row = $result->fetch_assoc();
				
				if (!$row)
				{
					exit_with_status("Item does not exist.", status_code: 400);
				}

				if ($row["post_id"] !== $id)
				{
					exit_with_status("Entry not linked with account.", status_code: 400);
				}
				
				$items[] = new DonationPostEntry($id, $row["resource_id"], $row["post_id"], $row["quantity"]);
				
			}	
		}
		catch (Exception $e)
		{
			error_log($e->getMessage());
			exit_with_status("Mismatch in given id with respect to given poster and recipient id.", status_code: 400);
		}
		
		return new self($id, $poster_id, $recipient_id, $created_at, $items);
		
	}

	public static function insert_from_json($json_array): DonationPost
	{
		require_once 'user_input.inc.php';
		require_once 'query.php';
		require_once "error.inc.php";
		Account::require_login();

		if ($json_array["basket_details"] === null)
		{
			exit_with_status(message: "Missing basket details.", status_code: 400);
		}

		$basket_details = $json_array["basket_details"];	
		$basket_content = $json_array["basket_content"];
		$poster_id = basket_details["poster_id"];
		$recipient_id = basket_details["recipient_page_id"];

		if ($poster_id === null || $recipient_id === null)
		{
			exit_with_status(message: "Missing recipient or poster id.", status_code: 400);
		}
		
		if (!is_int($poster_id) || !is_int($recipient_id))
		{
			exit_with_status(message: "Poster or recipient id is in the wrong format.", status_code: 400);
		}

		if ($poster_id === $recipient_id)
		{
			exit_with_status(message: "Cannot donate to oneself.", status_code: 400);
		}

		// be more specific and ask for poster_id now that it is a known integer
		Account::require_login_of_user($poster_id);

		self::validate_basket_content($basket_content);

		try 
		{
			$created_at = new DateTime();
			$query = DatabaseQuery::from_file("queries/insert_donation_post.sql")	;
			$id = Database::insert(true, $query, "iii", $poster_id, $recipient_id);
			$result_entries = array();

			foreach ($basket_content as $item)
			{
				$query = DatabaseQuery::from_file("queries/insert_donation_post_entry.sql");
				$item_id = Database::insert(true, $query, "iii", $item["resource_id"], $id, $item["quantity"]);
				$entry = new DonationPostEntry($item_id, $item["resource_id"], $id, $item["quantity"]);
				$result_entries[] = $entry;
			}
		}
		catch (Exception $e)
		{
			error_log($e);
			exit_with_status(message: "Invalid identifiers.", status_code: 400);
		}

		return new self($id, $poster_id, $recipient_id, $created_at, $result_entries);
	}

	public static function accept_donation_post(DonationPost $post): void
	{
		Account::require_login_of_user($post->recipient_id);
		
	}
}

class Resources 
{
	public int $id;
	public string $name;
	public string $description;

	public function __construct(int $id, string $name, string $description) 
	{
		$this->id = $id;
		$this->name = $name;
		$this->description = $description;
	}

	public static function get_resource_set(): mysqli_result
	{
		$query = DatabaseQuery::from_file("queries/select_resources.sql");
		return Database::select($query, "", "");
	}

	public static function request_resources(): void
	{
		expect_get();
		echo Database::result_to_json(self::get_resource_set());
	}	
}

function encode_to_json(object $object)  
{
	return json_encode((array) $object);
}

?>
