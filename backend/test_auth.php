<?php
require_once "entities.inc.php";
session_start();
echo "Hello world! [" . $_SESSION["__user"] . "]";
#Account::require_login();
?>
