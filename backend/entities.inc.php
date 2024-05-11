<?php
/* It's probably convenient to represent the different entities as php objects */


class Account 
{
	public $id;
	public $username;
	public $biography;
	public $phone_number;
	public $created_at;

	public function __construct($id, $username, $biography, $phone_number, $created_at) 
	{
		$this->id = $id;
		$this->username = $username;
		$this->biography = $biography;
		$this->phone_number = $phone_number;
		$this->created_at = $created_at;
	}

	public static function register($username, $password, $biography, $phone_number)
	{
		// stuff goes here
	}

	public static function is_authenticated()
	{
		return false;
	}
}

class AccountReport 
{
	public $id;
	public $reported_id;
	public $reporter_id;
	public $description;
	public $created_at;

	public function __construct($id, $reported_id, $reporter_id, $description, $created_at) 
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
	public $id;
	public $resource_id;
	public $quantity;

	protected function __construct($id, $resource_id, $quantity) 
	{
		$this->id = $id;
		$this->resource_id = $resource_id;
		$this->quantity = $quantity;	
	}
}

class DonationPageEntry extends Entry 
{
	public $page_id;

	public function __construct($id, $resource_id, $quantity, $page_id) 
	{
		Entry::__construct($id, $resource_id, $quantity);
		$this->page_id = $page_id;
	}
}

class DonationPage 
{
	public $id;
	public $donatee_id;
	public $name;
	public $description;
	public $created_at;

	public function __construct($id, $donatee_id, $name, $description, $created_at)
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
	public $post_id;
	public function __construct($id, $resource_id, $quantity, $post_id) 
	{
		Entry::__construct($id, $resource_id, $quantity);
		$this->post_id = $post_id;
	}
}

class DonationPost 
{
	public $id;
	public $poster_id;
	public $recipient_id;
	public $created_at;

	public function __construct($id, $poster_id, $recipient_id, $created_at) 
	{
		$this->id = $id;
		$this->poster_id = $poster_id;
		$this->recipient_id = $recipient_id;
		$this->created_at = $created_at;
	}
}

class Resources 
{
	public $id;
	public $name;
	public $description;

	public function __construct($id, $name, $description) 
	{
		$this->id = $id;
		$this->name = $name;
		$this->description = $description;
	}
}

function serialize_object($object) 
{
	return json_encode((arrray) $object);
}

?>
