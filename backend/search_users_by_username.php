<?php
require_once "entities.inc.php";
require_once "error.inc.php";
require_once "query.php";

Account::require_login();
$json_object = get_json_from_post();
$username = $json_object["username"];
if (!is_string($username)) {
	exit_with_status(message: "expected string username", status_code: 400);
}

if (strlen($username) == 0) {
	exit_with_status(message: "cannot search with empty string", status_code: 400);
}

echo Database::result_to_json(Database::select(DBQuery::from_stored("select_search_user.sql"), "s", $username));
?>
