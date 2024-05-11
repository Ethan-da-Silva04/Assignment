<?php

require_once 'entities.inc.php';
require_once "error.inc.php";

class DatabaseQuery
{
	public string $data;

	private function __construct(string $data)
	{
		$this->data = $data;	
	}

	public static function from_file(string $file_path): self
	{
		return new self($file_path);
	}

	public static function from_string(string $query_as_string): self
	{
		return new self($query_as_string);
	}
}

class Database
{
	private static ?mysqli $connection = null;

	public static function get_connection(): mysqli 
	{
		if (self::$connection === null) 
		{
			mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
			self::$connection = new mysqli("localhost", "standard_user", "password", "Donations");
		}	

		if (self::$connection->connect_error)
		{
			exit_with_status(message: "Failed connecting to database", status_code: 500);			
		}

		return self::$connection;
	}

	private static function execute_from_query(DatabaseQuery &$query, string $types, mixed ...$bind_params): mysqli_stmt
	{
		$connection = self::get_connection();
		$stmt = $connection->prepare($query->data);
		if (!$stmt) 
		{
			exit_with_status(message: "Failed preparing statement.", status_code: 500);
		}

		if (($bind_params !== null && strlen($types) != 0) && !$stmt->bind_param($types, ...$bind_params))
		{
			exit_with_status(message: "Could not bind parameters.", status_code: 500);
		}

		if (!$stmt->execute())
		{
			$connection->rollback();
			exit_with_status(message: "Failed executing statement: " . $stmt->error, status_code: 500);
		}

		return $stmt;
	}
	
	/*
	 * @return: the id of the inserted row
	 */
	public static function insert(DatabaseQuery &$query, string $types, mixed ...$bind_params): int|false
	{
		$connection = self::get_connection();
		$connection->begin_transaction();

		$connection->begin_transaction();

		$stmt = self::execute_from_query($query, $types, ...$bind_params);

		if ($stmt->affected_rows > 0)
		{
			$connection->commit();
			$result = $stmt->insert_id;
			$stmt->close();
			return $result;
		}

		$connection->rollback();
		
		return false;
	}

	public static function select(DatabaseQuery &$query, string $types, mixed ...$bind_params): mysqli_result
	{
		$connection = self::get_connection();
		$stmt = self::execute_from_query($query, $types, ...$bind_params);
		
		$result = $stmt->get_result();
		if (!$result)
		{
			exit_with_status(message: "Failed getting result from query.", status_code: 500);
		}
		
		return $result;
	}

	public static function update(DatabaseQuery &$query, string $types, mixed ...$bind_params): mysqli_result
	{
		$connection = self::get_connection();

		$connection->begin_transaction();

		$stmt = self::execute_from_query($query, $types, ...$bind_params);
		$result = $stmt->get_result();
		if (!$result)
		{
			exit_with_status(message: "Failed getting result from query.", status_code: 500);
		}
		
		return $result;
	}	

	public static function delete(DatabaseQuery &$query, string $types, mixed &...$bind_params): mysqli_result
	{
		$connection = self::get_connection();

		$connection->begin_transaction();

		$stmt = self::execute_from_query($query, $types, ...$bind_params);
		$result = $stmt->get_result();
		if (!$result)
		{
			exit_with_status(message: "Failed getting result from query.", status_code: 500);
		}
		
		return $result;
	}

	public static function result_to_json(mysqli_result $result): string|false
	{
		$data = array();
		while ($row = $result->fetch_assoc()) 
		{
			$data[] = $row;	
		}
		
		return json_encode($data);
	}

}

?>
