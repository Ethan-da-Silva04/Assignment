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
		$query_string = "INSERT INTO Accounts VALUES(NULL, ?, ?, ?, ?, NOW(), LAST_INSERT_ID(), 0)";

		try 
		{
			$id = Database::insert(true, DatabaseQuery::from_string($query_string), "ssss", $username, $hashed_password, $phone_number, $biography);
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

	public static function login(mixed $username, mixed $password): self|false
	{
		require_once "error.inc.php";
		if (self::is_logged_in())
		{
			exit_with_status(message: "Already logged in.", status_code: 400);
		}

		self::validate_user_login_input($username, $password);
		$hashed_password = password_hash(password: $password, algo: PASSWORD_DEFAULT);

		try
		{
			$result_set = Database::select(DatabaseQuery::from_string("SELECT * FROM Accounts WHERE username = ?;"), "s", $username);
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
	public string $description;
	public DateTime $created_at;

	public function __construct(int $id, int $donatee_id, string $name, string $description, DateTime $created_at)
	{
		$this->id = $id;
		$this->donatee_id = $donatee_id;
		$this->name = $name;
		$this->description = $description;
		$this->created_at = $created_at;
		
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
			if (is_int($quantity) || $quantity < 0 || $quantity > 10000)
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
		$recipient_id = basket_details["recipient_id"];

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
			$query_string = "INSERT INTO DonationPosts VALUES(NULL, ?, ?, NOW())";
			$id = Database::insert(true, DatabaseQuery::from_string($query_string), "iii", $poster_id, $recipient_id);
			$result_entries = array();

			foreach ($basket_content as $item)
			{
				$query_string = "INSERT INTO DonationPostEntries VALUES(NULL, ?, ?, ?);";
				$item_id = Database::insert(true, DatabaseQuery::from_string($query_string), "iii", $item["resource_id"], $id, $item["quantity"]);
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
		return Database::select(DatabaseQuery::from_string("SELECT * FORM Resources;"), "", "");
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
