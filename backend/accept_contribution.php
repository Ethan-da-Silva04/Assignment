<?php
require_once "entities.inc.php";
require_once "error.inc.php";
$contribution = Contribution::from_json(get_json_from_post());
Account::accept_contribution($contribution);
echo json_encode($contribution);
?>
