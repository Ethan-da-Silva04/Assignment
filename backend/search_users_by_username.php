<?php
require_once "entities.inc.php";
require_once "errors.inc.php";
require_once "query.php";

Account::require_login();
$json_object = get_json_from_post();
$username = $json_object["username"];
if (!is_string($username)) 
{
	exit_with_status(message: "expected string username", status_code: 400);
}

echo Database::result_to_json(Database::select(DatabaseQuery::from_string("SELECT * FROM Accounts WHERE Accounts.username = ?"), "s", $username));
?>
