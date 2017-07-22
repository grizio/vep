CREATE TABLE company (
  id      VARCHAR(255) NOT NULL,
  name    VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  isVep   BOOLEAN      NOT NULL,
  content TEXT         NOT NULL,
  PRIMARY KEY (id)
);