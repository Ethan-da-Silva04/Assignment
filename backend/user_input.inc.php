<?php
require_once "error.inc.php";

function get_json_from_post() 
{
	expect_request_method("POST");
	$raw_data = file_get_contents('php://input');
	$json_data = json_decode($raw_data, associative: true);

	if ($json_data === null) 
	{
		exit_with_status(message: "Invalid JSON.", status_code: 400);
	}

	return $json_data;
}

function contains_only($string, $permitted) 
{
    $pattern = "/^[" . preg_quote($permitted, '/') . "]+$/";
    return preg_match($pattern, $string);
}

function contains_any($string, $values)
{
    $pattern = '/[' . preg_quote($values, '/') . ']/';
    return preg_match($pattern, $string);	
}

function contains_char($string, $char) 
{
    return strpos($string, $char) !== false;
}

function is_ascii($char) 
{
    $ord_value = ord($char);
    return ($ord_value >= 0 && $ord_value <= 127);
}

function string_all_of($string, $predicate)
{
	foreach ($string as $character)
	{
		if (!$predicate($character))
		{
			return false;
		}
	}

	return true;
}

function is_username_char($char) 
{
	return contains_char(string: "qwertyuiopasdfghjklzxcvbnm1234567890 ", char: strtolower($char));
}

function is_password_char($char)
{
	return is_ascii($char);
}

function validate_username($username) 
{
	if ($username === null)
	{
		exit_with_status(message: "Missing username.", status_code: 400);
	}

	if (strlen($username) > 64) 
	{
		exit_with_status(message: "Username is too long.", status_code: 400);
	}

	if (!string_all_of(string: $username, predicate: "is_username_char"))
	{
		exit_with_status(message: "Username contains unpermitted characters, username may only consist of alpha-numeric ASCII characters or spaces", status_code: 400);
	}
	
		
}

function validate_password($password) 
{
	if ($password === null) 
	{
		exit_with_status(message: "Missing password.", status_code: 400);
	}

	if (strlen($password) > 64)
	{
		exit_with_status(message: "Password is too long.", status_code: 400);
	}

	if (!string_all_of(string: $password, predicate: "is_password_char"))
	{
		exit_with_status(message: "Password may only consist of ascii characters", status_code: 400);
	}
}
?>
