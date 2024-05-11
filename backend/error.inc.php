<?php
function exit_with_status($message, $status_code = 400) 
{
	http_response_code($status_code);
	exit($message);
}

function expect_request_method($request_method) 
{
	if ($_SERVER["REQUEST_METHOD"] === $request_method) 
	{
		return;
	}

	exit_with_status(message: "Method not allowed.", status_code: 405);
}
?>
