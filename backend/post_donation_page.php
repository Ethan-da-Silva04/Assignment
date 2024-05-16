<?php
require_once "error.inc.php";
require_once "entities.inc.php";

$json_object = get_json_from_post();
echo DonationPage::insert_from_json($json_object);
?>
