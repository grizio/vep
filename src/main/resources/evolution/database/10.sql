CREATE TABLE period_adhesion (
  id                VARCHAR(255) NOT NULL,
  startPeriod       TIMESTAMP    NOT NULL,
  endPeriod         TIMESTAMP    NOT NULL,
  startRegistration TIMESTAMP    NOT NULL,
  endRegistration   TIMESTAMP    NOT NULL,
  activities        TEXT         NOT NULL,
  PRIMARY KEY (id)
);