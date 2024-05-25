SELECT MAX(account_rank) AS value
FROM Accounts
WHERE accepted_contributions BETWEEN ? + 1 AND ?
LIMIT 1;
