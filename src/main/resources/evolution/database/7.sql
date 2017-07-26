CREATE TABLE play_theater_seat (
  play_id VARCHAR(255) NOT NULL,
  c       VARCHAR(255) NOT NULL,
  x       INTEGER      NOT NULL,
  y       INTEGER      NOT NULL,
  w       INTEGER      NOT NULL,
  h       INTEGER      NOT NULL,
  t       VARCHAR(255),
  PRIMARY KEY (play_id, c),
  FOREIGN KEY (play_id) REFERENCES play (id)
);

CREATE TABLE reservation (
  id         VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  email      VARCHAR(255) NOT NULL,
  city       VARCHAR(255) NULL,
  comment    TEXT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE reservation_seat (
  reservation_id VARCHAR(255) NOT NULL,
  play_id        VARCHAR(255) NOT NULL,
  play_c         VARCHAR(255) NOT NULL,
  PRIMARY KEY (reservation_id, play_id, play_c),
  FOREIGN KEY (reservation_id) REFERENCES reservation(id),
  FOREIGN KEY (play_id, play_c) REFERENCES play_theater_seat(play_id, c)
);