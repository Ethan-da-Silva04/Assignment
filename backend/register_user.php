<?php

require_once 'user_input.inc.php';

$data = get_json_from_post();
$username = $data["username"];
$password = $data["password"];
validate_username($username);
validate_password($password);
echo "Account creation succesful";

?>
