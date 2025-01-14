create table Filmy(
ID_Filmu int not null auto_increment,
Tytul varchar(255),
Gatunek varchar (255),
Cena_dzienna FLOAT(5,2),
ID_Rezyser int,
PRIMARY KEY(ID_Filmu),
FOREIGN KEY (ID_Rezyser) REFERENCES Rezyser(ID_Rezyser));

CREATE INDEX Film_Tytul_idx ON Filmy(Tytul) USING BTREE;
