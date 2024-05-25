UPDATE Accounts
SET 	account_rank = CASE
		-- old_accepted_contributions.value, new_account_details.accepted_contributions
		WHEN 	(accepted_contributions BETWEEN ? + 1 AND ? - 1) 
			-- new_account_details.accepted_contributions, new_account_details.created_at
			OR (accepted_contributions == ? AND created_at <= ? AND id != ?)
				THEN account_rank - 1
		WHEN 	id = ? 
			 -- @new_account_rank.value
				THEN ?
		ELSE 
				account_rank
	END
-- old_accepted_contributions.value, new_account_details.accepted_contributions
WHERE accepted_contributions BETWEEN ? + 1 AND ?;
