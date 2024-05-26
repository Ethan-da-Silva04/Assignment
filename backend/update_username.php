<?php
require_once "entities.inc.php";
$json_object = get_json_from_post();
$id = $json_object["id"];
$new_username = $json_object["new_username"];
if (!is_int($id)) {
	exit_with_status(message: "Missing id.", status_code: 400);
}

validate_username($new_username);

Account::require_login_of_user($id);
$result_set = Database::select(DBQuery::from_stored("select_user_by_username.sql"), "s",  $new_username);
$row = $result_set->fetch_row();

if ($row !== false && $row !== null) {
	exit_with_status(message: "Username already taken", status_code: 400);
	return;
}

Database::update(true, DBQuery::from_stored("update_account_username.sql"), "si", $new_username, $id);
echo json_encode([]);
?>
