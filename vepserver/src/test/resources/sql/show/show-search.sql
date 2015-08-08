INSERT INTO company (id, canonical, name, address, isvep, content)
VALUES
  (1, 'scene-et-loire', 'Scène et loire', 'La Possonnière, France', TRUE, ''),
  (2, 'la-companie-du-coin', 'La companie du coin', 'La Possonnière, France', TRUE, ''),
  (3, 'la-companie-de-l-ourson-blanc', 'La Compagnie de l''Ourson Blanc', 'Segré, France', FALSE, ''),
  (4, 'uatl', 'UATL', 'Angers, France', FALSE, ''),
  (5, 'replik', 'Replik', 'Angers, France', FALSE, ''),
  (6, 'l-atelier-de-la-comedie', 'L''atelier de la comédie', 'Nantes, France', FALSE, ''),
  (7, 'le-theater-de-la-queue-du-chat', 'Le théâtre de la queue du chat', 'Chalonne-sur-Loire, France', FALSE, ''),
  (8, 'atelier-theatre', 'Ateliers théâtre', 'La Possonnière, France', TRUE, ''),
  (9, 'la-cie-des-anjoues', 'La companie des anjoués', 'Angers, France', FALSE, '');

INSERT INTO shows (canonical, title, author, director, company, duration, content)
VALUES
  ('amour-passion-et-cx-diesel', 'Amour Passion et CX Diesel', 'Celui-là même', 'Un autre', 9, NULL, NULL),
  ('le-mystere-de-la-baguette', 'Le mystère de la baguette', 'Interne A', 'Olivier', 8, NULL, NULL),
  ('ubu-roi', 'Ubu roi', 'Interne B', 'Olivier', 8, NULL, NULL),
  ('voyage-en-absurdie', 'Voyage en absurdie', 'Interne C', 'Leslie Quévet', 8, NULL, NULL),
  ('scenes-de-vie', 'Scènes de vie', 'Interne D', 'Leslie Quévet', 8, NULL, NULL),
  ('dissonances', 'Dissonances', 'Editions Théâtrales', 'Nicolas Berthoux', 7, NULL, NULL),
  ('donc', 'Donc', 'Jean-Yves PICQ', 'Jean-Yves PICQ', 6, NULL, NULL),
  ('les-justes', 'Les Justes', 'Albert Camus', 'Bernard Clément', 3, NULL, NULL),
  ('poulard-et-fils', 'Poulard et fils', 'Bruno Chapelle', 'Philippe Chauveau', 5, NULL, NULL),
  ('t-emballe-pas', 'T''emballe pas', 'Christian Rossignol', 'Mathieu Lancelot', 1, NULL, NULL),
  ('le-mot-de-cambronne', 'Le mot de Cambronne', 'Sacha Guitry', 'Sacha Guitry', 4, NULL, NULL),
  ('un-grand-cri-d-amour', 'Un grand cri d''amour', 'Josiane Balasko', 'Bernard Clement', 3, NULL, NULL),

  ('amour-passion-et-cx-diesel-2', 'Amour Passion et CX Diesel 2', 'Celui-là même', 'Un autre', 9, NULL, NULL),
  ('le-mystere-de-la-baguette-2', 'Le mystère de la baguette 2', 'Interne E', 'Olivier', 8, NULL, NULL),
  ('ubu-roi-2', 'Ubu roi 2', 'Interne F', 'Olivier', 8, NULL, NULL),
  ('voyage-en-absurdie-2', 'Voyage en absurdie 2', 'Interne G', 'Leslie Quévet', 8, NULL, NULL),
  ('scenes-de-vie-2', 'Scènes de vie 2', 'Interne H', 'Leslie Quévet', 8, NULL, NULL),
  ('dissonances-2', 'Dissonances 2', 'Editions Théâtrales', 'Nicolas Berthoux', 7, NULL, NULL),
  ('donc-2', 'Donc 2', 'Jean-Yves PICQ', 'Jean-Yves PICQ', 6, NULL, NULL),
  ('les-justes-2', 'Les Justes 2', 'Albert Camus', 'Bernard Clément', 3, NULL, NULL),
  ('poulard-et-fils-2', 'Poulard et fils 2', 'Bruno Chapelle', 'Philippe Chauveau', 5, NULL, NULL),
  ('t-emballe-pas-2', 'T''emballe pas 2', 'Christian Rossignol', 'Mathieu Lancelot', 1, NULL, NULL),
  ('le-mot-de-cambronne-2', 'Le mot de Cambronne 2', 'Sacha Guitry', 'Sacha Guitry', 4, NULL, NULL),
  ('un-grand-cri-d-amour-2', 'Un grand cri d''amour 2', 'Josiane Balasko', 'Bernard Clement', 3, NULL, NULL);