<?php
require_once "user_input.inc.php";
require_once "entities.inc.php";

$data = get_json_from_post();
$username = $data["username"];
$password = $data["password"];

echo json_encode([Account::login($username, $password)]);
?>
