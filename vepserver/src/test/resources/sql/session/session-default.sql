INSERT INTO session (id, theater, canonical, date, name, reservationEndDate)
VALUES
  (1, 1, 'existing-session_1', '2100-01-01 20:30:00', 'Existing session', '2100-01-01 00:00:00'),
  (2, 1, 'existing-session_2', '2000-01-01 20:30:00', 'Existing session', '2000-01-01 00:00:00');

INSERT INTO session_price (id, session, name, price, cases)
VALUES
  (1, 1, 'First price', 10, NULL),
  (2, 1, 'Second price', 5, 'Children'),
  (3, 2, 'First price', 10, NULL);

INSERT INTO session_show (session, shows, num)
VALUES (1, 1, 1), (2, 1, 1);