/* Theaters */

/* Fixed theater */
INSERT INTO theater (id, canonical, name, address, content, fixed, plan, maxSeats)
VALUES (1, 'existing-theater-fixed', 'Existing theater fixed', 'Paris, France', null, 1, '[{"c":"A1","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"A2","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"A3","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"A4","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"A5","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"B1","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"B2","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"B3","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"B4","t":"normal","x":0,"y":0,"w":0,"h":0},{"c":"B5","t":"normal","x":0,"y":0,"w":0,"h":0}]', null);

/* Dynamic theater */
INSERT INTO theater (id, canonical, name, address, content, fixed, plan, maxSeats)
VALUES (2, 'existing-theater-dynamic', 'Existing theater dynamic', 'Paris, France', null, 0, null, 10);


/* Companies */
INSERT INTO company (id, canonical, name, address, isvep, content)
VALUES (1, 'existing-company', 'Existing company', 'Paris, France', 1, null);


/* Shows */
INSERT INTO shows (id, canonical, title, author, director, company, duration, content)
VALUES (1, 'existing-show', 'Existing show', 'An author', 'A director', 1, null, null);


/* Sessions */

/* session fixed theater */
INSERT INTO session (id, theater, canonical, date, name, reservationEndDate)
VALUES (1, 1, 'existing-session_1', '2100-01-01 20:30:00', 'Existing session', '2100-01-01 20:00:00');

INSERT INTO session_price (id, session, name, price, cases) VALUES (1, 1, 'Full price', 10, null);
INSERT INTO session_price (id, session, name, price, cases) VALUES (2, 1, 'Half price', 5, 'Children');

INSERT INTO session_show (session, shows, num) VALUES (1, 1, 1);


/* session dynamic theater */
INSERT INTO session (id, theater, canonical, date, name, reservationEndDate)
VALUES (2, 2, 'existing-session_2', '2100-01-02 20:30:00', 'Existing session', '2100-01-02 20:00:00');

INSERT INTO session_price (id, session, name, price, cases) VALUES (3, 2, 'Full price', 12, null);
INSERT INTO session_price (id, session, name, price, cases) VALUES (4, 2, 'Half price', 6, 'Children');

INSERT INTO session_show (session, shows, num) VALUES (2, 1, 1);


/* Reservations */

/* Fixed theater */
/* Reservation 1 */
INSERT INTO reservation (id, session, firstname, lastname, email, city, comment, seats, pass)
VALUES (1, 1, 'John', 'Smith', 'abc@def.com', null, null, null, '123456789');

INSERT INTO reservation_seat (reservation, seat) VALUES (1, 'A1');
INSERT INTO reservation_seat (reservation, seat) VALUES (1, 'A2');
INSERT INTO reservation_seat (reservation, seat) VALUES (1, 'A3');
INSERT INTO reservation_seat (reservation, seat) VALUES (1, 'A4');

INSERT INTO reservation_price (reservation, price, number) VALUES (1, 1, 1);
INSERT INTO reservation_price (reservation, price, number) VALUES (1, 2, 3);

/* Reservation 2 */
INSERT INTO reservation (id, session, firstname, lastname, email, city, comment, seats, pass)
VALUES (3, 1, 'Al', 'Zheimer', 'al@zheimer.undefined.com', null, null, null, '246813579');

INSERT INTO reservation_seat (reservation, seat) VALUES (3, 'A5');

INSERT INTO reservation_price (reservation, price, number) VALUES (3, 1, 1);

/* Dynamic theater */
/* Reservation 1 */
INSERT INTO reservation (id, session, firstname, lastname, email, city, comment, seats, pass)
VALUES (2, 2, 'Albert', 'Einstein', 'ghi@jkl.com', null, null, 5, '987654321');

INSERT INTO reservation_price (reservation, price, number) VALUES (2, 3, 2);
INSERT INTO reservation_price (reservation, price, number) VALUES (2, 4, 3);

/* Reservation 2 */
INSERT INTO reservation (id, session, firstname, lastname, email, city, comment, seats, pass)
VALUES (4, 2, 'Miss', 'Tyc', 'miss@tyc.undefined.com', null, null, 1, '987654321');

INSERT INTO reservation_price (reservation, price, number) VALUES (4, 3, 1);
