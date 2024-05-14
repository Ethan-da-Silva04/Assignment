#! /bin/bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"test_user","password":"password"}' \
  http://127.0.0.1:8000/login.php
