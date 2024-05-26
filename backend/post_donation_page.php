<?php
require_once "error.inc.php";
require_once "entities.inc.php";
require_once "user_input.inc.php";
$json_object = get_json_from_post();
echo json_encode([DonationPage::insert_from_json($json_object)]);
?>
