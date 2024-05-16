SELECT * 
FROM Contributions
INNER JOIN PendingContributions ON PendingContributions.id = DonationContributions.id
INNER JOIN DonationPages ON DonationPages.id = Contributions.recipient_page_id
WHERE DonationPages.donatee_id = ?;
