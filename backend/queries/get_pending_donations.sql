SELECT * 
FROM DonationPosts
INNER JOIN PendingDonationPosts ON PendingDonationPosts.id = DonationPosts.id
INNER JOIN DonationPages ON DonationPages.id = DonationPosts.recipient_page_id
WHERE DonationPages.donatee_id = ?;
