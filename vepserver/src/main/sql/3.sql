ALTER TABLE users ADD roles TEXT NOT NULL;
UPDATE users SET roles = 'user';