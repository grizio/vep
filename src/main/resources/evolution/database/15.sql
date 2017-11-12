CREATE TABLE reservation_price (
  reservation_id   VARCHAR(255) NOT NULL,
  price            VARCHAR(255) NOT NULL,
  seatsCount       INTEGER NOT NULL,
  PRIMARY KEY (reservation_id, price)
);