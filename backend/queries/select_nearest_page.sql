SELECT DonationPages.id, DonationPages.donatee_id, DonationPages.name, DonationPages.created_at
FROM DonationPages
INNER JOIN DonationPageEntries ON DonationPages.id = DonationPageEntries.page_id
WHERE DonationPageEntries.resource_id IN (?)
GROUP BY DonationPages.id
ORDER BY COUNT(*);
