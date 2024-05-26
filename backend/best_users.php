<?php
require_once "user_input.inc.php";
require_once "error.inc.php";
require_once "query.php";
session_start();
Account::require_login();
expect_get();
echo Database::result_to_json(Database::select(DBQuery::from_stored("top_accounts.sql"), "", ""));
?>
