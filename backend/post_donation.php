<?php
require_once "entities.inc.php";
require_once "error.inc.php";

$json_object = get_json_from_post();
echo json_encode(Contribution::insert_from_json($json_object));
?>
