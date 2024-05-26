<?php
require_once "error.inc.php";
require_once "entities.inc.php";

Account::require_login();
$json = get_json_from_post();
$id = $json["id"];

if ($id === null || !is_int($id)) {
	exit_with_status(message: "Expected page id", status_code: 400);
}

$query_result = Database::select(DBQuery::from_stored("select_user_from_page.sql"), "i", $id);
$row = $query_result->fetch_assoc();
if (!$row) {
	exit_with_status(message: "id not associated with any item", status_code: 400);
}

$result = array();
$result["user"] = $row;
$file_path = "user/" . $row["page_name"] . ".html";
$file = fopen($file_path, "r");

if ($file) {
	$length = filesize($file_path);
	$content = fread($file, $length);
	$result["content"] = $content;
	echo json_encode([$result]);
} else {
	exit_with_status("Server encountered internal error.", status_code: 500);
}
?>
