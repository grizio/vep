INSERT INTO company(id, canonical, name, address, isvep, content)
VALUES (1, 'existing-company', 'Existing company', 'Paris, France', FALSE, 'This is a test.');

INSERT INTO company(id, canonical, name, address, isvep, content)
VALUES (2, 'existing-company-2', 'Existing company 2', null, FALSE, null);

INSERT INTO shows(canonical, title, author, director, company, duration, content)
VALUES('existing-show', 'Existing show', 'This author', 'This director', 1, 120, 'This is a content');