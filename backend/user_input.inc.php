<?php
require_once "error.inc.php";

function get_json_from_post(): array
{
	expect_post();
	$raw_data = file_get_contents('php://input');
	$json_data = json_decode($raw_data, associative: true);

	if ($json_data === null) 
	{
		exit_with_status(message: "Invalid JSON.", status_code: 400);
	}

	return $json_data;
}

function contains_only(string $string, string $permitted) 
{
    $pattern = "/^[" . preg_quote($permitted, '/') . "]+$/";
    return preg_match($pattern, $string);
}

function contains_any(string $string, string $values)
{
    $pattern = '/[' . preg_quote($values, '/') . ']/';
    return preg_match($pattern, $string);	
}

function contains_char(string $string, string $char) 
{
    return strpos($string, $char) !== false;
}

function is_ascii(string $char) 
{
    $ord_value = ord($char);
    return ($ord_value >= 0 && $ord_value <= 127);
}

function string_all_of(string $string, $predicate)
{
	foreach (str_split($string) as $character)
	{
		if (!$predicate($character))
		{
			return false;
		}
	}

	return true;
}

function is_username_char(string $char) 
{
	return contains_char(string: "qwertyuiopasdfghjklzxcvbnm1234567890 ", char: strtolower($char));
}

function is_password_char(string $char)
{
	return is_ascii($char);
}

function is_phone_number_char(string $char)
{
	return ctype_digit($char);
}

function validate_username(mixed $username) 
{
	if ($username === null)
	{
		exit_with_status(message: "Missing username.", status_code: 400);
	}

	if (!is_string($username)) 
	{
		exit_with_status(message: "Username is presented in the wrong format.", status_code: 400);
	}

	if (strlen($username) < 3) 
	{
		exit_with_status(message: "Username must be atleast 3 characters.", status_code: 400);
	}

	if (strlen($username) > 64) 
	{
		exit_with_status(message: "Username is too long.", status_code: 400);
	}

	if (!string_all_of(string: $username, predicate: "is_username_char"))
	{
		exit_with_status(message: "Username contains unpermitted characters, username may only consist of alpha-numeric ASCII characters or spaces.", status_code: 400);
	}
}

function validate_password(mixed $password) 
{
	if ($password === null) 
	{
		exit_with_status(message: "Missing password.", status_code: 400);
	}

	if (!is_string($password)) 
	{
		exit_with_status(message: "Password is presented in the wrong format.", status_code: 400);
	}

	if (strlen($password) < 3)
		exit_with_status(message: "Password must be atleast 8 characters.", status_code: 400);
	{
	}

	if (strlen($password) > 64)
	{
		exit_with_status(message: "Password is too long.", status_code: 400);
	}

	if (!string_all_of(string: $password, predicate: "is_password_char"))
	{
		exit_with_status(message: "Password may only consist of ASCII characters.", status_code: 400);
	}
}

function validate_biography(mixed $biography)
{
	if ($biography === null)
	{
		exit_with_status(message: "Missing biography.", status_code: 400);
	}

	if (!is_string($biography))
	{
		exit_with_status(message: "Biography is presented in the wrong format.", status_code: 400);
	}

	if (strlen($biography) > 512)
	{
		exit_with_status(message: "Biography is too long.", status_code: 400);
	}

	if (!string_all_of(string: $biography, predicate: "is_ascii"))
	{
		exit_with_status(message: "Biography may only consist of ASCII characters.", status_code: 400);
	}
}

function validate_phone_number(mixed $phone_number)
{
	if ($phone_number === null)
	{
		exit_with_status(message: "Missing phone number.", status_code: 400);
	}

	if (!is_string($phone_number)) 
	{
		exit_with_status(message: "Phone number is presented in the wrong format.", status_code: 400);
	}

	if (strlen($phone_number) != 10) 
	{
		exit_with_status(message: "Phone number must be exactly 10 characters.", status_code: 400);
	}

	if (!string_all_of(string: $phone_number, predicate: "is_phone_number_char")) 
	{
		exit_with_status(message: "Phone number may only consist of digits.", status_code: 400);
	}
}

?>
