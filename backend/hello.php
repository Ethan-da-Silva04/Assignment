<?php
require_once "query.php";

echo Database::result_to_json(Database::select(DatabaseQuery::from_string("SELECT * FROM Accounts;"), "", ""));
?>
