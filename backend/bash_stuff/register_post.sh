#! /bin/bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"xyz","password":"xyz","biography":"xyz","phone_number":"0834470919"}' \
  http://127.0.0.1:8000/register.php
