#! /bin/bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"xyz","password":"17153143"}' \
  http://127.0.0.1:8000/login.php
