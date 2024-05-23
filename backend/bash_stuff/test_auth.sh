#!/bin/bash

curl 	--cookie-jar \
        --header "Content-Type: application/json" \
	--request GET \
	http://192.168.1.105:8000/test_auth.php
