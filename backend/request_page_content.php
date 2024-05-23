<?php
require_once "error.inc.php";
require_once "entities.inc.php";

Account::require_login();
$json = get_json_from_post();
$id = $json["id"];

if ($id === null || !is_int($id)) 
{
	exit_with_status(message: "Expected id", status_code: 400);
}

$result = Database::select(DatabaseQuery::from_string("SELECT * FROM DonationPages WHERE id = ?"), "i", $id);
$row = $result->fetch_assoc();
if (!$row)
{
	exit_with_status(message: "id not associated with any item", status_code: 400);
}

$file_path = "user/" . $row["name"] . ".html";
$file = fopen($file_path, "r");

if ($file)
{
	$length = filesize($file_path);
	$content = fread($file, $length);
	echo json_encode([["content" => $content]]);
}
else
{
	exit_with_status("Server encountered internal error.", status_code: 500);
}

?>
