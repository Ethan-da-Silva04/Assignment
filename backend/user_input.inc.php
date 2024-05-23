<?php
require_once "error.inc.php";

header("Access-Control-Allow-Origin: *"); // Adjust as needed
header("Access-Control-Allow-Credentials: true");

function get_json_from_post(): array {
	expect_post();
	$raw_data = file_get_contents('php://input');
	$json_data = json_decode($raw_data, associative: true);

	if ($json_data === null){
		exit_with_status(message: "Invalid JSON.", status_code: 400);
	}

	return $json_data;
}

function contains_only(string $string, string $permitted) {
    $pattern = "/^[" . preg_quote($permitted, '/') . "]+$/";
    return preg_match($pattern, $string);
}

function contains_any(string $string, string $values) {
    $pattern = '/[' . preg_quote($values, '/') . ']/';
    return preg_match($pattern, $string);	
}

function contains_char(string $string, string $char)  {
    return strpos($string, $char) !== false;
}

function is_ascii(string $char) {
    $ord_value = ord($char);
    return ($ord_value >= 0 && $ord_value <= 127);
}

function string_all_of(string $string, $predicate) {
	foreach (str_split($string) as $character) {
		if (!$predicate($character)) {
			return false;
		}
	}

	return true;
}

function string_any_of(string $string, $predicate) {
	foreach (str_split($string) as $character) {
		if ($predicate($character)) {
			return true;
		}
	}

	return false;
}

function is_username_char(string $char) {
	return contains_char(string: "qwertyuiopasdfghjklzxcvbnm1234567890_ ", char: strtolower($char));
}

function is_password_char(string $char) {
	return is_ascii($char);
}

function is_phone_number_char(string $char) {
	return ctype_digit($char);
}

function is_page_name_char(string $char) {
	return ctype_alpha($char) || $char === " ";
}

function validate_username(mixed $username) {
	if ($username === null) {
		exit_with_status(message: "Missing username.", status_code: 400);
	}

	if (!is_string($username)) {
		exit_with_status(message: "Username is presented in the wrong format.", status_code: 400);
	}

	if (strlen($username) < 3) {
		exit_with_status(message: "Username must be atleast 3 characters.", status_code: 400);
	}

	if (strlen($username) > 64) {
		exit_with_status(message: "Username is too long.", status_code: 400);
	}

	if (!string_all_of(string: $username, predicate: "is_username_char")) {
		exit_with_status(message: "Username contains unpermitted characters, username may only consist of alpha-numeric ASCII characters or spaces.", status_code: 400);
	}

	if (!string_any_of(string: $username, predicate: "ctype_alpha")) {
		exit_with_status(message: "Username must contain atleast one alphebetical character.", status_code: 400);
	}
}

function validate_password(mixed $password) {
	if ($password === null) {
		exit_with_status(message: "Missing password.", status_code: 400);
	}

	if (!is_string($password)) {
		exit_with_status(message: "Password is presented in the wrong format.", status_code: 400);
	}

	if (strlen($password) < 8) {
		exit_with_status(message: "Password must be atleast 8 characters.", status_code: 400);
	}

	if (strlen($password) > 64) {
		exit_with_status(message: "Password is too long.", status_code: 400);
	}

	if (!string_all_of(string: $password, predicate: "is_password_char")) {
		exit_with_status(message: "Password may only consist of ASCII characters.", status_code: 400);
	}
}

function validate_biography(mixed $biography) {
	if ($biography === null) {
		exit_with_status(message: "Missing biography.", status_code: 400);
	}

	if (!is_string($biography)) {
		exit_with_status(message: "Biography is presented in the wrong format.", status_code: 400);
	}

	if (strlen($biography) > 512) {
		exit_with_status(message: "Biography is too long.", status_code: 400);
	}

	if (!string_all_of(string: $biography, predicate: "is_ascii")) {
		exit_with_status(message: "Biography may only consist of ASCII characters.", status_code: 400);
	}

	if (!string_any_of(string: $biography, predicate: "ctype_alpha")) {
		exit_with_status(message: "Biography must contain atleast one alphebetical character.", status_code: 400);
	}
}

function validate_phone_number(mixed $phone_number) {
	if ($phone_number === null) {
		exit_with_status(message: "Missing phone number.", status_code: 400);
	}

	if (!is_string($phone_number)) {
		exit_with_status(message: "Phone number is presented in the wrong format.", status_code: 400);
	}

	if (strlen($phone_number) != 10) {
		exit_with_status(message: "Phone number must be exactly 10 characters.", status_code: 400);
	}

	if (!string_all_of(string: $phone_number, predicate: "is_phone_number_char")) {
		exit_with_status(message: "Phone number may only consist of digits.", status_code: 400);
	}
}

function is_valid_attribute($tag_name, $attribute_name) {
    static $allowed_attributes = array('img' => array('src', 'alt'));

    return isset($allowed_attributes[$tag_name]) && in_array($attribute_name, $allowed_attributes[$tag_name]);
}

function validate_page_section(DOMNode|null &$page_section): bool {
	$allowed_tags = array('h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'img', 'title', 'meta');	

	if ($page_section === null) {
		exit_with_status("Failed parsing html.", status_code: 400);
	}


	foreach ($page_section->childNodes as $node) {
		if (!in_array(strtolower($node->nodeName), $allowed_tags)) {
			exit_with_status("Unpermitted tag.", status_code: 400);
			return false;
            	}

		foreach ($node->attributes as $attribute) {
			if (!is_valid_attribute($node->nodeName, $attribute->nodeName)) {
				exit_with_status("Unpermitted attribute.", status_code: 400);
				return false;
			}
                }
	}

	return true;
}

function validate_page_content(mixed &$page_content) {
	if ($page_content === null) {
		exit_with_status(message: "Missing page content.", status_code: 400);
	}

	if (!is_string($page_content)) {
		exit_with_status(message: "Page content is presented in the wrong format.", status_code: 400);
	}

	if (strlen($page_content) > 32768) {
		exit_with_status(message: "Page content is too long.", status_code: 400);
	}

	if (strlen($page_content) == 0) {
		exit_with_status(message: "Page content cannot be empty.", status_code: 400);
	}

	$dom = new DOMDocument();
	
	libxml_use_internal_errors(true);
	if (!$dom->loadHTML($page_content)) {
		exit_with_status(message: "Failed loading html.", status_code: 400);
	}
	libxml_use_internal_errors(false);

	$head = $dom->getElementsByTagName("head")->item(0);
	$body = $dom->getElementsByTagName("body")->item(0);
	
	if (!$head || !$body) {
		exit_with_status(message: "Missing head or body.", status_code: 400);
	}

	return validate_page_section($head) && validate_page_section($body);
}

function validate_page_name(mixed &$page_name) {
	if ($page_name === null) {
		exit_with_status(message: "Missing page name.",  status_code: 400);
	}

	if (!is_string($page_name)) {
		exit_with_status(message: "Page name is presented in the wrong format.", status_code: 400);
	}

	if (strlen($page_name) > 64) {
		exit_with_status("Page name too long.", status_code: 400);
	}

	if (strlen($page_name) == 0) {
		exit_with_status("Page name cannot be empty.", status_code: 400);
	}

	if (!string_all_of($page_name, "is_page_name_char")) {
		exit_with_status("Page name may only consist of ASCII alphabetical characters and spaces.\n", status_code: 400);
	}	
	
	if (!string_any_of($page_name, "ctype_alpha")) {
		exit_with_status("Page must contain atleast one alphabetical character.", status_code: 400);
	}
}
?>
