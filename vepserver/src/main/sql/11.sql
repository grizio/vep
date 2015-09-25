CREATE TABLE reservation
(
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  session INT NOT NULL,
  firstname VARCHAR(255) NOT NULL,
  lastname VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  city VARCHAR(255),
  comment LONGTEXT,
  seats INT,
  pass VARCHAR(255) NOT NULL
);

CREATE TABLE reservation_seat
(
  reservation INT NOT NULL,
  seat VARCHAR(255) NOT NULL,
  PRIMARY KEY (reservation, seat)
);

ALTER TABLE reservation_seat ADD FOREIGN KEY fk_reservation_seat_reservation (reservation) REFERENCES reservation(id);

CREATE TABLE reservation_price
(
  reservation INT NOT NULL,
  price INT NOT NULL,
  number INT NOT NULL,
  PRIMARY KEY (reservation, price)
);

ALTER TABLE reservation_price ADD FOREIGN KEY fk_reservation_price_reservation (reservation) REFERENCES reservation(id);

ALTER TABLE reservation_price ADD FOREIGN KEY fk_reservation_price_price (price) REFERENCES session_price(id);