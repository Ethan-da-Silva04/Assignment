<?php

require_once 'user_input.inc.php';
require_once 'entities.inc.php';

$data = get_json_from_post();
$username = $data["username"];
$password = $data["password"];
$biography = $data["biography"];
$phone_number = $data["phone_number"];

session_start();
echo json_encode([Account::register($username, $password, $biography, $phone_number)]);
?>
