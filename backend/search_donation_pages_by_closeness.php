<?php
require_once "user_input.inc.php";
require_once "entities.inc.php";
Account::require_login();
$json_object = get_json_from_post();
$basket_content = $json_object["content"];
validate_basket_content($basket_content);

$query_to_embed = "SELECT ";

for ($i = 0; $i < sizeof($basket_content) - 1; $i++) {
	$entry = &$basket_content[$i];
	$query_to_embed = $query_to_embed . strval($entry["resource_id"]) . ", ";
}

$query_to_embed = $query_to_embed . strval($basket_content[sizeof($basket_content) - 1]["resource_id"]);
$test = Database::result_to_json(Database::select(DBQuery::from_stored("select_nearest_page.sql"), "s", $query_to_embed));
error_log($test);
echo $test;
?>
