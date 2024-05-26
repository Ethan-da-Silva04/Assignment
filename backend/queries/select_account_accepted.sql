
SELECT 	AcceptedContributionEntries.id
FROM AcceptedContributionEntries
INNER JOIN ContributionEntries ON ContributionEntries.id = AcceptedContributionEntries.id
INNER JOIN Contributions ON ContributionEntries.contribution_id = Contributions.id
WHERE Contributions.poster_id = ?;
