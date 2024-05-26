<?php
require_once "user_input.inc.php";
require_once "entities.inc.php";

$data = get_json_from_post();
$username = $data["username"];
$password = $data["password"];
session_start();

$account = Account::login($username, $password);
Account::require_login();
echo json_encode([$account]);
?>
