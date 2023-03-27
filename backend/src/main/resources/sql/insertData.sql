-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data
DELETE
FROM owner
where id < 0;

INSERT INTO owner (id, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-1, 'Eragon', 'Schattentöter', 'erscha@gmail.com'),
       (-2, 'Enekin', 'Skywalker', 'lowground@imperium.com'),
       (-3, 'Obiwan', 'Kenobus', 'highground@imperium.com'),
       (-4, 'Naruto', 'Uzumaki', 'naruto.uzumaki@leafvillage.com'),
       (-5, 'Sasuke', 'Uchiha', 'sasuke.uchiha@snakevillage.com'),
       (-6, 'Sakura', 'Haruno', 'sakura.haruno@leafvillage.com'),
       (-7, 'Kakashi', 'Hatake', 'kakashi.hatake@leafvillage.com'),
       (-8, 'Hinata', 'Hyuga', 'hinata.hyuga@leafvillage.com'),
       (-9, 'Neji', 'Hyuga', 'neji.hyuga@leafvillage.com'),
       (-10, 'Shikamaru', 'Nara', 'shikamaru.nara@leafvillage.com'),
       (-11, 'Ino', 'Yamanaka', 'ino.yamanaka@leafvillage.com'),
       (-12, 'Choji', 'Akimichi', 'choji.akimichi@leafvillage.com'),
       (-13, 'Gaara', 'Sabaku', 'gaara.sabaku@sandvillage.com'),
       (-14, 'Temari', 'Sabaku', 'temari.sabaku@sandvillage.com'),
       (-15, 'Kankuro', 'Sabaku', 'kankuro.sabaku@sandvillage.com'),
       (-16, 'Rock', 'Lee', 'rock.lee@leafvillage.com'),
       (-17, 'Tenten', '', 'tenten@leafvillage.com'),
       (-18, 'Might', 'Guy', 'might.guy@leafvillage.com'),
       (-19, 'Jiraiya', '', 'jiraiya@sannin.com'),
       (-20, 'Tsunade', '', 'tsunade@sannin.com'),
       (-21, 'Orochimaru', '', 'orochimaru@snakevillage.com'),
       (-22, 'Kabuto', 'Yakushi', 'kabuto.yakushi@snakevillage.com'),
       (-23, 'Itachi', 'Uchiha', 'itachi.uchiha@snakevillage.com')
;


DELETE
FROM horse
where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, father_id, mother_id)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', -12, NULL, NULL),
       (-2, 'Tobisch', 'The famous one!', '2012-12-12', 'MALE', NULL, NULL, NULL),
       (-3, 'Wendisch', 'child of Tobisch and Wendy', '2020-11-11', 'FEMALE', -8, -2, -1),
       (-4, 'Tick', 'second child of Tobisch and Wendy', '2020-12-12', 'FEMALE', NULL, -2, -1),
       (-5, 'Trick', 'thierd child of Tobisch and Wendy', '2021-01-01', 'FEMALE', NULL, -2, -1),
       (-6, 'Track', 'fourth child of Tobisch and Wendy', '2021-02-02', 'FEMALE', NULL, -2, -1),
       (-7, 'Peter', 'child of Wendisch', '2020-12-12', 'FEMALE', NULL, -2, -3),
       (-8, 'Jedi', 'powerful force user', '1990-05-25', 'MALE', NULL, NULL, NULL),
       (-9, 'Sith', 'dark side force user', '1990-05-25', 'FEMALE', -4, NULL, NULL),
       (-10, 'Padmé Amidala', 'Queen of Naboo and mother of Luke and Leia', '2020-04-06', 'FEMALE', NULL, NULL, NULL),
       (-11, 'Jango Fett', 'Mandalorian bounty hunter and father of Boba Fett', '2020-04-06', 'MALE', NULL, -2, -1),
       (-12, 'Boba Fett', 'Mandalorian bounty hunter', '2020-04-06', 'MALE', -4, -11, -10),
       (-13, 'Anakin Skywalker', 'Jedi Knight who became Darth Vader', '2020-04-010', 'MALE', NULL, -12, NULL),
       (-14, 'Luke', 'son of Anakin Skywalker and Padmé Amidala', '2000-07-16', 'MALE', -4, -13, -10),
       (-15, 'Leia', 'daughter of Anakin Skywalker and Padmé Amidala', '2000-07-16', 'FEMALE', NULL, -13, -10),
       (-16, 'Han', 'smuggler and pilot', '1990-05-25', 'MALE', -14, NULL, NULL),
       (-17, 'Chewbacca', 'Wookiee co-pilot of the Millennium Falcon', '1990-05-25', 'MALE', NULL, NULL, NULL),
       (-18, 'Darth Vader', 'Sith Lord and father of Luke and Leia', '1977-05-25', 'MALE', -2, NULL, NULL),
       (-19, 'Yoda', 'Jedi Master and Grand Master of the Jedi Order', '896-05-25', 'MALE', -1, NULL, NULL),
       (-20, 'Obi-Wan Kenobi', 'Jedi Master and mentor to Luke Skywalker', '2020-04-07', 'MALE', NULL, NULL, NULL),
       (-21, 'Mace Windu', 'Jedi Master and member of the Jedi High Council', '2020-04-06', 'MALE', NULL, NULL, NULL),
       (-22, 'Qui-Gon Jinn', 'Jedi Master and mentor to Obi-Wan Kenobi', '2020-05-06', 'MALE', -5, NULL, NULL),
       (-23, 'Emperor Palpatine', 'Sith Lord and Galactic Emperor', '2020-05-013', 'MALE', -6, NULL, NULL),
       (-24, 'Darth Maul', 'Sith Lord and apprentice to Darth Sidious', '2020-06-06', 'MALE', NULL, NULL, NULL),
       (-25, 'Kylo Ren', 'leader of the Knights of Ren and grandson of Darth Vader', '2020-06-020', 'MALE', NULL, -18, -15),
       (-26, 'Rey', 'Jedi and scavenger from Jakku', '2021-03-04', 'FEMALE', -18, NULL, NULL);
