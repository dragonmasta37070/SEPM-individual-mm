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
VALUES (-3, 'Wendisch', 'the first child of many', '2020-12-12', 'FEMALE', -2, -1)
;
