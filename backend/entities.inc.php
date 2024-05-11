<?php
/* It's probably convenient to represent the different entities as php objects */


class Account 
{
	public int $id;
	public string $username;
	public string $biography;
	public string $phone_number;
	public DateTime $created_at;

	public function __construct(int $id, string $username, string $biography, string $phone_number, DateTime $created_at) 
	{
		$this->id = $id;
		$this->username = $username;
		$this->biography = $biography;
		$this->phone_number = $phone_number;
		$this->created_at = $created_at;
	}

	public static function register(string $username, string $password, string $biography, string $phone_number): void
	{
		// stuff goes here, this should handle the data validation stuff
	}

	public static function is_authenticated(): bool
	{
		return false;
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

function serialize_object(object $object)  
{
	return json_encode((array) $object);
}

?>
