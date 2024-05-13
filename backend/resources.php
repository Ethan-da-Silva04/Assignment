<?php
require_once "query.php";

expect_get();
$x = Database::select(DatabaseQuery::from_string("SELECT * FROM Resources;"), "", "");
echo Database::result_to_json($x);
?>
