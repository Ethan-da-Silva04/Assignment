<?php
require_once "user_input.inc.php";
require_once "error.inc.php";
require_once "query.php";

Account::require_login();
expect_get();
echo Database::result_to_json(Database::select(DatabaseQuery::from_file("queries/top_accounts.sql"), "", ""));
?>
