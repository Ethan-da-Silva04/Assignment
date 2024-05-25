SELECT * FROM DonationPageEntries
INNER JOIN DonationPages ON DonationPages.id = DonationPageEntries.page_id
INNER JOIN Accounts ON Accounts.id = DonationPages.donatee_id
WHERE Accounts.id = ?;
