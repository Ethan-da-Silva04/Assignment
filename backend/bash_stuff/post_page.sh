#!/bin/bash

FILE_CONTENT=$(cat test.html | tr -d '\n\t' | sed 's/"/\\"/g')
#echo "{\"content\": \"${FILE_CONTENT}\"}"
curl 	--header "Content-Type: application/json" \
	--request POST \
	--data "{\"content\": \"${FILE_CONTENT}\"}" \
	http://10.197.160.37:8000/post_donation_page.php
