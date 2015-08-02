CREATE TABLE company
(
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  canonical VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255),
  isvep TINYINT NOT NULL,
  content LONGTEXT
);
CREATE UNIQUE INDEX unique_canonical ON company (canonical);