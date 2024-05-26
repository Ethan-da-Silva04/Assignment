SELECT DonationPageEntries.id as id,
	DonationPageEntries.page_id as page_id,
	DonationPageEntries.resource_id as resource_id,
	DonationPageEntries.quantity_asked as quantity_asked,
	DonationPageEntries.quantity_received as quantity_received,
	DonationPages.created_at as created_at,
	DonationPages.name as name,
	DonationPages.donatee_id as donatee_id
FROM DonationPageEntries
INNER JOIN DonationPages ON DonationPages.id = DonationPageEntries.page_id
INNER JOIN Accounts ON Accounts.id = DonationPages.donatee_id
WHERE Accounts.id = ?;
