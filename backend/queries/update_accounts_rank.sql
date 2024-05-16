SET @old_accepted_contributions := (
	SELECT accepted_contributions as value
	FROM Accounts
	WHERE id = ?
);

UPDATE Accounts
SET accepted_contributions = accepted_contributions + ?
WHERE id = ?;

SET @new_account_details := (
	SELECT accepted_contributions, created_at
	FROM Accounts
	WHERE id = ?
);

SET @new_account_rank := (
	SELECT MAX(account_rank) AS value
	FROM Accounts 
	WHERE accepted_contributions BETWEEN @old_accepted_contributions.value + 1 AND @new_accepted_contributions.accepted_contributions
	LIMIT 1
);

UPDATE Accounts
SET 	account_rank = CASE
		WHEN 	(accepted_contributions BETWEEN @old_accepted_contributions.value + 1 AND @new_account_details.accepted_contributions - 1) 
			OR (created_at <= @new_account_details.created_at)
				THEN account_rank - 1
		WHEN 	id = ? 
				THEN @new_account_rank.value
		ELSE 
				account_rank
	END
WHERE accepted_contributions BETWEEN @old_accepted_contributions.value + 1 AND @new_account_details.accepted_contributions;
