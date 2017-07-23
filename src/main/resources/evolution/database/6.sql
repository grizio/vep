CREATE TABLE play (
  id                 VARCHAR(255) NOT NULL,
  theater            VARCHAR(255) NOT NULL,
  date               TIMESTAMP NOT NULL,
  reservationEndDate TIMESTAMP NOT NULL,
  show               VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (theater) REFERENCES theater (id),
  FOREIGN KEY (show) REFERENCES show (id)
);

CREATE TABLE play_price (
  id        VARCHAR(255) NOT NULL,
  name      VARCHAR(255) NOT NULL,
  value     NUMERIC      NOT NULL,
  condition TEXT         NULL,
  play      VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (play) REFERENCES play (id)
)