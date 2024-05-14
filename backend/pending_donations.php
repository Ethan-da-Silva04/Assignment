<?php
require_once "entities.inc.php";
Account::require_login();
echo Account::request_pending_donations();
?>
