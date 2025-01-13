CREATE TABLE Uzytkownicy (
ID_uzytkownika int NOT NULL auto_increment,
imie varchar(30),
nazwisko varchar(30),
nrdowodu varchar(9),
PRIMARY KEY (ID_uzytkownika));

CREATE INDEX dowod_klienta_idx ON Uzytkownicy(nrdowodu) USING BTREE;