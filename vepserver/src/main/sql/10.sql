CREATE TABLE session
(
  id                 INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  theater            INT             NOT NULL,
  canonical          VARCHAR(255)    NOT NULL,
  date               DATETIME        NOT NULL,
  name               VARCHAR(255)    NOT NULL,
  reservationEndDate DATETIME        NOT NULL
);

ALTER TABLE session ADD FOREIGN KEY fk_theater(theater) REFERENCES theater (id);

ALTER TABLE session ADD UNIQUE u_theater_canonical(theater, canonical);

ALTER TABLE session ADD UNIQUE u_theater_date(theater, date);

CREATE TABLE session_price
(
  id      INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  session INT             NOT NULL,
  name    VARCHAR(255)    NOT NULL,
  price   INT             NOT NULL,
  cases   VARCHAR(255)
);

ALTER TABLE session_price ADD FOREIGN KEY fk_session(session) REFERENCES session (id);

ALTER TABLE session_price ADD UNIQUE u_session_name(session, name);

ALTER TABLE session_price ADD UNIQUE u_session_price(session, price);

CREATE TABLE session_show
(
  session INT NOT NULL,
  shows   INT NOT NULL,
  num     INT NOT NULL,
  PRIMARY KEY (session, shows)
);

ALTER TABLE session_show ADD FOREIGN KEY fk_session(session) REFERENCES session (id);

ALTER TABLE session_show ADD FOREIGN KEY fk_shows(shows) REFERENCES shows (id);

ALTER TABLE session_show ADD UNIQUE u_session_num(session, num);