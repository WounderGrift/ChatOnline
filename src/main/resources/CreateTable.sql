CREATE TABLE MESSAGE(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  MSG VARCHAR(255));

CREATE TABLE USER(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(50),
  PASS VARCHAR(50));

CREATE TABLE USER_MESSAGE(
  USER_ID INT,
  MSG_ID INT,
  CONSTRAINT USER_MESSAGE_USER_ID_fkey FOREIGN KEY (USER_ID)
  REFERENCES public.USER (id),
  CONSTRAINT USER_MESSAGE_MSG_ID_fkey FOREIGN KEY (MSG_ID)
  REFERENCES public.MESSAGE (ID));

SELECT NAME FROM USER;


INSERT INTO TESTDB.PUBLIC.USER (ID, NAME, PASS) VALUES (1, 'User', '111')