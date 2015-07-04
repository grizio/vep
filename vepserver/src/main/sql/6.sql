CREATE TABLE theater
(
  id        INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  canonical VARCHAR(255)    NOT NULL,
  name      VARCHAR(255)    NOT NULL,
  address   LONGTEXT        NOT NULL,
  content   LONGTEXT,
  fixed     TINYINT         NOT NULL,
  plan      LONGTEXT,
  maxSeats  SMALLINT
);
CREATE UNIQUE INDEX unique_canonical ON theater (canonical);
