<?php
require "user_input.inc.php";
$data = get_json_from_post();

if (!isset($data["basket"])) {
	exit_with_status(message: "Missing basket.", status_code: 400);
}

echo json_encode($data["basket"]);
?>
