SELECT * 
FROM Accounts
WHERE username LIKE CONCAT("%", ?, "%");
