CREATE TABLE shows
(
  id        INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  canonical VARCHAR(255)    NOT NULL,
  title     VARCHAR(255)    NOT NULL,
  author    VARCHAR(255)    NOT NULL,
  director  VARCHAR(255)    NOT NULL,
  company   INT             NOT NULL,
  duration  INT,
  content   LONGTEXT
);
CREATE UNIQUE INDEX unique_canonical ON shows (canonical);