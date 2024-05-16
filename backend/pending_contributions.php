<?php
require_once "entities.inc.php";
Account::require_login();
Account::request_pending_contributions();
?>
