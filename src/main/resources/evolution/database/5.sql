CREATE TABLE show (
  id       VARCHAR(255) NOT NULL,
  title    VARCHAR(255) NOT NULL,
  author   VARCHAR(255) NOT NULL,
  director VARCHAR(255) NOT NULL,
  content  TEXT         NOT NULL,
  company  VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (company) REFERENCES company (id)
);