CREATE TABLE page (
  canonical       VARCHAR(255) NOT NULL,
  title           VARCHAR(255) NOT NULL,
  navigationOrder INTEGER      NOT NULL,
  content         TEXT         NOT NULL,
  PRIMARY KEY (canonical)
);