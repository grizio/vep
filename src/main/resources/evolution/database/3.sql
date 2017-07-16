CREATE TABLE theater (
  id      VARCHAR(255) NOT NULL,
  name    VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  content TEXT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE theater_seat (
  theater_id VARCHAR(255) NOT NULL,
  c          VARCHAR(255) NOT NULL,
  x          INTEGER      NOT NULL,
  y          INTEGER      NOT NULL,
  w          INTEGER      NOT NULL,
  h          INTEGER      NOT NULL,
  t          VARCHAR(255),
  PRIMARY KEY (theater_id, c),
  FOREIGN KEY (theater_id) REFERENCES theater (id)
);