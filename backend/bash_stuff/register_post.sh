#! /bin/bash

curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"username":"Test_user","password":"password","biography":"Since I was very young, Ive been a test user.","phone_number":"0834470919"}' \
  http://10.197.160.37:8000/register.php
