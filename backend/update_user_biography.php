<?php
require_once "entities.inc.php";
$json_object = get_json_from_post();
$id = $json_object["id"];
$new_biography = $json_object["new_biography"];

if (!is_int($id)) {
	exit_with_status(message: "Missing id.", status_code: 400);
}

validate_biography($new_biography);
Database::update(true, DatabaseQuery::from_file("queries/update_account_biography.sql"), "si", $new_biography, $id);
echo json_encode([]);
?>
