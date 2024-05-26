<?php
function exit_with_status(string $message, int $status_code = 400)  {
	http_response_code($status_code);
	exit($message);
}

function expect_request_method(string $request_method)  {
	if ($_SERVER["REQUEST_METHOD"] === $request_method) {
		return;
	}

	exit_with_status(message: "Method not allowed.", status_code: 405);
}

function expect_post() { expect_request_method("POST"); }
function expect_get() { expect_request_method("GET"); }
?>
