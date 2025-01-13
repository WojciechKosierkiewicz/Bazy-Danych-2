CREATE TABLE Lokacje (
ID_Lokacji int NOT NULL auto_increment,
NAZWA varchar(100),
adres varchar(255),
nr_telefonu varchar(13),
primary key (ID_Lokacji)
);

CREATE INDEX lokacja_adres_idx ON Lokacje(adres) using btree;


