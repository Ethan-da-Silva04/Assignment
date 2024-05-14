SET @old_accepted_donations := (
	SELECT accepted_donations as value
	FROM Accounts
	WHERE id = ?
);

UPDATE Accounts
SET accepted_donations = accepted_donations + ?
WHERE id = ?;

SET @new_account_details := (
	SELECT accepted_donations, created_at
	FROM Accounts
	WHERE id = ?
);

SET @new_account_rank := (
	SELECT MAX(account_rank) AS value
	FROM Accounts 
	WHERE accepted_donations BETWEEN @old_accepted_donations.value + 1 AND @new_accepted_donations.accepted_donations
	LIMIT 1
);

UPDATE Accounts
SET 	account_rank = CASE
		WHEN 	(accepted_donations BETWEEN @old_accepted_donations.value + 1 AND @new_account_details.accepted_donations - 1) 
			OR (created_at <= @new_account_details.created_at)
				THEN account_rank - 1
		WHEN 	id = ? 
				THEN @new_account_rank.value
		ELSE 
				account_rank
	END
WHERE accepted_donations BETWEEN @old_accepted_donations.value + 1 AND @new_account_details.accepted_donations;
