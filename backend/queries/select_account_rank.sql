SELECT MIN(account_rank) AS value
FROM Accounts
WHERE accepted_contributions BETWEEN ? AND ?
LIMIT 1;
