CREATE TABLE adhesion (
  id       VARCHAR(255) NOT NULL,
  period   VARCHAR(255) NOT NULL,
  user_id  VARCHAR(255) NOT NULL,
  accepted BOOLEAN      NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (period) REFERENCES period_adhesion (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE adhesion_member (
  id        VARCHAR(255) NOT NULL,
  adhesion  VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  birthday  TIMESTAMP    NOT NULL,
  activity  VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (adhesion) REFERENCES adhesion (id)
);