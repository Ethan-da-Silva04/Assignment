CREATE TABLE IF NOT EXISTS Accounts (
	id INT AUTO_INCREMENT,
	username VARCHAR(64) UNIQUE NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	phone_number VARCHAR(10) NOT NULL,
	biography VARCHAR(512) NOT NULL,
	created_at DATETIME NOT NULL,

	account_rank INT UNIQUE NOT NULL,
	accepted_donations BIGINT UNSIGNED NOT NULL,

	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS Resources (
	id INT AUTO_INCREMENT,
	name VARCHAR(64) UNIQUE NOT NULL,
	description VARCHAR(255) NOT NULL,

	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS DonationPages (
	id INT AUTO_INCREMENT,
	donatee_id INT NOT NULL,
	name VARCHAR(128) UNIQUE NOT NULL,
	created_at DATETIME NOT NULL,

	PRIMARY KEY(id),
	FOREIGN KEY(donatee_id) REFERENCES Accounts(id)
);

CREATE TABLE IF NOT EXISTS DonationPageEntries (
	id INT AUTO_INCREMENT,
	page_id INT NOT NULL,
	resource_id INT NOT NULL,
	quantity_asked INT UNSIGNED NOT NULL,
	quantity_received INT UNSIGNED NOT NULL,
	
	PRIMARY KEY(id),
	FOREIGN KEY(page_id) REFERENCES DonationPages(id),
	FOREIGN KEY(resource_id) REFERENCES Resources(id)
);

CREATE TABLE IF NOT EXISTS DonationPosts (
	id INT AUTO_INCREMENT,
	poster_id INT NOT NULL,
	recipient_page_id INT NOT NULL,
	created_at DATETIME NOT NULL,

	PRIMARY KEY(id),
	FOREIGN KEY(poster_id) REFERENCES Accounts(id),
	FOREIGN KEY(recipient_page_id) REFERENCES DonationPages(id)
);

CREATE TABLE IF NOT EXISTS PendingDonationPosts (
	id INT,
	PRIMARY KEY(id),
	FOREIGN KEY(id) REFERENCES DonationPosts(id)
);

CREATE TABLE IF NOT EXISTS DonationPostEntries (
	id INT AUTO_INCREMENT,
	resource_id INT NOT NULL,
	post_id INT NOT NULL,
	quantity INT UNSIGNED NOT NULL,

	PRIMARY KEY(id),
	FOREIGN KEY(post_id) REFERENCES DonationPosts(id),
	FOREIGN KEY(resource_id) REFERENCES Resources(id)
);

CREATE TABLE IF NOT EXISTS AcceptedDonationPostEntries (
	id INT,
	accepted_at DATETIME NOT NULL,
	
	PRIMARY KEY(id),
	FOREIGN KEY(id) REFERENCES DonationPostEntries(id)
);

CREATE TABLE IF NOT EXISTS AccountReports (
	id INT,
	reported_id INT NOT NULL,
	reporter_id INT NOT NULL,
	description VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL,

	PRIMARY KEY(id),
	FOREIGN KEY(reported_id) REFERENCES Accounts(id),
	FOREIGN KEY(reporter_id) REFERENCES Accounts(id)
);
