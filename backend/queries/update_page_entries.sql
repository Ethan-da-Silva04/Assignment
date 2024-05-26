
UPDATE DonationPageEntries
SET quantity_received = quantity_received + ?
WHERE id = ?;
