CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.USER(
id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50),
 pass VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.MESSAGE(
id INT AUTO_INCREMENT PRIMARY KEY,
msg VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS TESTDB.PUBLIC.USERS_MESSAGE (
USER_ID INT NOT NULL,
 MSG_ID INT NOT NULL,
 CONSTRAINT USERS_MESSAGE_USER_ID_fkey FOREIGN KEY (USER_ID)
REFERENCES public.USER (id),
  CONSTRAINT USERS_MESSAGE_MSG_ID_fkey FOREIGN KEY (MSG_ID)
REFERENCES public.MESSAGE (ID)
);


INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (1, 'Dima', 'MTEx');
INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (2, 'Dasha', 'MTEx');
INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (3, 'Masha', 'MTEx');
INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (4, 'Sasha', 'MTEx');