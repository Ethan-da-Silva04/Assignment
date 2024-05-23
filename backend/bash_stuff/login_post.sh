#! /bin/bash

curl --cookie-jar \
  --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"orhil","password":"password"}' \
  http://192.168.1.105:8000/login.php
