SELECT * 
FROM ContributionEntries
INNER JOIN Contributions ON Contributions.id = ContributionEntries.contribution_id
INNER JOIN Accounts ON Accounts.id = Contributions.poster_id
WHERE Accounts.id = ?;
