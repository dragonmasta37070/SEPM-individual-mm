-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE
FROM horse
where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE')
;
INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (-2, 'Tobisch', 'The famous one!', '2012-12-12', 'MALE')
;
INSERT INTO horse (id, name, description, date_of_birth, sex, father_id, mother_id)
VALUES (-3, 'Wendisch', 'child of Tobisch and Wendy', '2020-11-11', 'FEMALE', -2, -1)
;
INSERT INTO horse (id, name, description, date_of_birth, sex, father_id, mother_id)
VALUES (-4, 'Tick', 'second child of Tobisch and Wendy', '2020-12-12', 'FEMALE', -2, -1)
;
INSERT INTO horse (id, name, description, date_of_birth, sex, father_id, mother_id)
VALUES (-5, 'Trick', 'thierd child of Tobisch and Wendy', '2021-01-01', 'FEMALE', -2, -1)
;
INSERT INTO horse (id, name, description, date_of_birth, sex, father_id, mother_id)
VALUES (-6, 'Track', 'fourth child of Tobisch and Wendy', '2021-02-02', 'FEMALE', -2, -1)
;
INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id)
VALUES (-7, 'Peter', 'child of Wendisch', '2020-12-12', 'FEMALE', -3)
;

DELETE
FROM owner
where id < 0;

INSERT INTO owner (id, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-1, 'Eragon', 'Schattentöter', 'erscha@gmail.com' )
;
INSERT INTO owner (id, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-2, 'Enekin', 'Skywalker', 'lowground@imperium.com' )
;
INSERT INTO owner (id, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-3, 'Obiwan', 'Kenobus', 'highground@imperium.com' )
;