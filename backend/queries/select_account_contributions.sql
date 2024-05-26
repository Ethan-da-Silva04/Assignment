SELECT 	ContributionEntries.id as id,
	ContributionEntries.resource_id as resource_id,
	ContributionEntries.quantity as quantity,
	ContributionEntries.contribution_id as contribution_id,
	Contributions.poster_id as poster_id,
	Contributions.created_at as created_at,
	Contributions.recipient_page_id as recipient_page_id,
	DonationPages.name as page_name,
	DonationPages.donatee_id as donatee_id
FROM ContributionEntries
INNER JOIN Contributions ON Contributions.id = ContributionEntries.contribution_id
INNER JOIN DonationPages ON DonationPages.id = Contributions.recipient_page_id
INNER JOIN Accounts ON Accounts.id = Contributions.poster_id
WHERE Accounts.id = ?;
