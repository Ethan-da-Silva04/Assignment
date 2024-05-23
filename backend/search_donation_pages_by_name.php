<?php
require_once "entities.inc.php";
require_once "error.inc.php";
require_once "query.php";

Account::require_login();
$json_object = get_json_from_post();
$name = $json_object["name"];
if (!is_string($name)) {
	exit_with_status(message: "expected string name.", status_code: 400);
}

if (strlen($name) === 0) {
	exit_with_status(message: "cannot search with an empty query.", status_code: 400);
}


$query = DatabaseQuery::from_file("queries/select_search_page.sql");
$result =  Database::result_to_json(Database::select($query, "s", $name));
echo $result;
?>
