<?php
require_once "user_input.inc.php";
require_once "error.inc.php";

$content = get_json_from_post()["content"];
validate_page_content($content);
?>
