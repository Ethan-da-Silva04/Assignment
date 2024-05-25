SELECT 	DonationPages.donatee_id as id, 
	DonationPages.name as page_name,
	Accounts.username as username, 
	Accounts.phone_number as phone_number, 
	Accounts.biography as biography, 
	Accounts.account_rank as account_rank, 
	Accounts.accepted_contributions as accepted_contributions
FROM DonationPages
INNER JOIN Accounts ON DonationPages.donatee_id = Accounts.id
WHERE DonationPages.id = ?;
