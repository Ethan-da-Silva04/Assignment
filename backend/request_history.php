<?php
require_once "entities.inc.php";
require_once "error.inc.php";
require_once "user_input.inc.php";
Account::require_login();
$json_object = get_json_from_post();
$account_id = $json_object["id"];

if (!is_int($account_id)) {
	exit_with_status(message: "Expected integer", status_code: 400);
}

// if account_id does not exist within the Accounts table, $result will be a collection of empty sets
$result = array();
$page_entries_result_set = Database::select(
			DBQuery::from_stored("select_account_pages.sql"), 
			"i", 
			$account_id);
$contribution_entries_result_set = Database::select(
				DBQuery::from_stored("select_account_contributions.sql"), 
				"i", 
				$account_id);
$accepted_contributions = Database::select(DBQuery::from_stored("select_account_accepted.sql"), "i", $account_id)->fetch_all();
$result["pages"] = array();
$result["contributions"] = array();
# this should just be an integer array
$result["accepted_contributions"] = $accepted_contributions;

while (($row = $page_entries_result_set->fetch_assoc())) {
	$result["pages"][] = $row;
}

while (($row = $contribution_entries_result_set->fetch_assoc())) {
	$result["contributions"][] = $row;
}

error_log(json_encode($result));
echo json_encode([$result]);
?>
