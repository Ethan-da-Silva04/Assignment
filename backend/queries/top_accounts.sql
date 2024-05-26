SELECT 	Accounts.id as id,
	Accounts.username as username,
	Accounts.phone_number as phone_number,
	Accounts.biography as biography,
	Accounts.created_at as created_at,
	Accounts.account_rank as account_rank,
	Accounts.accepted_contributions as accepted_contributions
FROM Accounts
ORDER BY Accounts.account_rank
LIMIT 100;
