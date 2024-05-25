<?php

require_once 'entities.inc.php';
require_once "error.inc.php";

class DatabaseQuery {
	private static array $cached_queries = array();

	public string $data;

	private function __construct(string $data) {
		$this->data = $data;	
	}

	public static function from_file(string $file_path): self {
		/*
		if (isset(self::$cached_queries, $file_path)) 
		{
			error_log(message: "QIUHFQOWIUHOWIUHFOQWIUGHW");
			return self::$cached_queries[$file_path];
		}
		*/

		$file = fopen($file_path, "r");

		if ($file === null) {
			exit_with_status("Failed finding query.\n", status_code: 500);		
		}
		
		$length = filesize($file_path);
		$contents = fread($file, $length);

		if ($contents === null) {
			exit_with_status("Failed reading query.\n", status_code: 500);
		}

		self::$cached_queries[$file_path] = $contents;
		return new self($contents);
	}

	public static function from_string(string $query_as_string): self {
		return new self($query_as_string);
	}
}

class Database {
	private static ?mysqli $connection = null;

	private static function enable_reporting(bool $yes): void {
		if ($yes) {
			mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);
		}
	}

	public static function get_connection(): mysqli {
		if (self::$connection === null)  {
			self::enable_reporting(true);
			self::$connection = new mysqli("localhost", "standard_user", "password", "Donations");
		}	

		if (self::$connection->connect_error) {
			exit_with_status(message: "Failed connecting to database", status_code: 500);			
		}

		return self::$connection;
	}

	private static function execute_from_query(DatabaseQuery &$query, string $types, mixed ...$bind_params): mysqli_stmt {
		$connection = &self::get_connection();
		$stmt = $connection->prepare($query->data);
		if (!$stmt) {
			exit_with_status(message: "Failed preparing statement.", status_code: 500);
		}

		if (($bind_params !== null && strlen($types) != 0) && !$stmt->bind_param($types, ...$bind_params)) {
			exit_with_status(message: "Could not bind parameters.", status_code: 500);
		}

		try {
			if (!$stmt->execute()) {
				$connection->rollback();
				exit_with_status(message: "Failed executing statement: " . $stmt->error, status_code: 500);
			}
		} catch (Exception $e) {
			throw new Exception($e);
		}

		return $stmt;
	}
	
	/*
	 * @return: the id of the inserted row
	 */
	public static function insert(bool $commit_transaction, DatabaseQuery &$query, string $types, mixed ...$bind_params): int|false {
		try {
			$connection = &self::get_connection();
			$connection->begin_transaction();

			$stmt = self::execute_from_query($query, $types, ...$bind_params);

			if ($stmt->affected_rows <= 0) {
				$connection->rollback();
				return false;
			}

			if ($commit_transaction) {
				$connection->commit();
			}

			$result = $stmt->insert_id;
			$stmt->close();
			return $result;
		} catch (Exception $e) {
			throw new Exception($e);
		}
		
		return false;
	}

	public static function select(DatabaseQuery &$query, string $types, mixed ...$bind_params): mysqli_result {
		try {
			$connection = &self::get_connection();
			$stmt = self::execute_from_query($query, $types, ...$bind_params);
			
			$result = $stmt->get_result();
			if (!$result) {
				exit_with_status(message: "Failed getting result from query.", status_code: 500);
			}
			
			return $result;
		} catch (Exception $e) {
			throw new Exception($e);
		}
	}

	public static function update(bool $commit_transaction, DatabaseQuery &$query, string $types, mixed ...$bind_params): void {
		try {
			$connection = &self::get_connection();
			$connection->begin_transaction();

			$stmt = self::execute_from_query($query, $types, ...$bind_params);

			if ($commit_transaction) {
				$connection->commit();
			}
			
		} catch (Exception $e) {
			throw new Exception($e);
		}
	}	

	public static function delete(bool $commit_transaction, DatabaseQuery &$query, string $types, mixed &...$bind_params): void{
		try {
			$connection = &self::get_connection();
			$connection->begin_transaction();

			$stmt = self::execute_from_query($query, $types, ...$bind_params);
			if ($commit_transaction) {
				$connection->commit();
			}
		} catch (Exception $e) {
			throw new Exception($e);
		}
	}

	public static function commit(): void {
		self::get_connection()->commit();
	}

	public static function rollback(): void {
		self::get_connection()->rollback();
	}

	public static function result_to_json(mysqli_result $result): string|false {
		$data = array();
		while ($row = $result->fetch_assoc()) {
			$data[] = $row;	
		}
		
		return json_encode($data);
	}

}
?>
