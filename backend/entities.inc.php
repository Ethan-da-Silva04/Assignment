<?php
/* It's probably convenient to represent the different entities as php objects */


class Account 
{
	public int $id;
	public string $username;
	public string $biography;
	public string $phone_number;
	public DateTime $created_at;

	private function __construct(int $id, string $username, string $biography, string $phone_number, DateTime $created_at) 
	{
		$this->id = $id;
		$this->username = $username;
		$this->biography = $biography;
		$this->phone_number = $phone_number;
		$this->created_at = $created_at;
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
		$query_string = "INSERT INTO Accounts VALUES(NULL, ?, ?, ?, ?, NOW())";
		$id = Database::insert(DatabaseQuery::from_string($query_string), "ssss", $username, $hashed_password, $phone_number, $biography);
		
		if (!$id)
		{
			exit_with_status(message: "Username already exists.", status_code: 400);	
		}
		
		return $_SESSION["__user"] = new self($id, $username, $biography, $phone_number, $created_at);
	}

	public static function is_logged_in(): bool
	{
		return isset($_SESSION["__user"]);
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
		
		$result_set = Database::select(DatabaseQuery::from_string("SELECT * FROM Accounts WHERE username = ?;"), "s", $username);
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
			DateTime::createFromFormat('Y-m-d H:i:s', $fst_row["created_at"])
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

class Entry 
{
	public int $id;
	public int $resource_id;
	public int $quantity;

	protected function __construct(int $id, int $resource_id, int $quantity) 
	{
		$this->id = $id;
		$this->resource_id = $resource_id;
		$this->quantity = $quantity;	
	}
}

class DonationPageEntry extends Entry 
{
	public int $page_id;

	public function __construct(int $id, int $resource_id, int $quantity, int $page_id) 
	{
		Entry::__construct($id, $resource_id, $quantity);
		$this->page_id = $page_id;
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

class DonationPostEntry extends Entry 
{
	public int $post_id;
	public function __construct(int $id, int $resource_id, int $quantity, int $post_id) 
	{
		Entry::__construct($id, $resource_id, $quantity);
		$this->post_id = $post_id;
	}
}

class DonationPost 
{
	public int $id;
	public int $poster_id;
	public int $recipient_id;
	public DateTime $created_at;

	public function __construct(int $id, int $poster_id, int $recipient_id, DateTime $created_at) 
	{
		$this->id = $id;
		$this->poster_id = $poster_id;
		$this->recipient_id = $recipient_id;
		$this->created_at = $created_at;
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
}

function encode_to_json(object $object)  
{
	return json_encode((array) $object);
}

?>
