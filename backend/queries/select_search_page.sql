SELECT *
FROM DonationPageEntries
INNER JOIN DonationPages
ON DonationPages.id = page_id
WHERE DonationPages.name = ?;
