CREATE TABLE vep.page
(
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  canonical VARCHAR(255) NOT NULL,
  menu INT,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL
);
ALTER TABLE vep.page ADD CONSTRAINT unique_canonical UNIQUE (canonical);