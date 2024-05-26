<?php
require_once "entities.inc.php";
require_once "error.inc.php";
Account::accept_contribution(Contribution::from_json(get_json_from_post()));
echo json_encode([]);
?>
