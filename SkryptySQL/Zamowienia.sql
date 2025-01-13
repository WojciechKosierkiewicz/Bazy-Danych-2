CREATE TABLE Zamowienia(
ID_zamowienia int NOT NULL auto_increment,
ID_uzytkownika int,
data_rozpoczecia DATE not null,
data_oczekiwanego_zakonczenia DATE not null,
data_faktycznego_zakonczenia DATE,
ID_Lokacji int,
PRIMARY KEY (ID_zamowienia),
foreign key (ID_uzytkownika) references Uzytkownicy(ID_uzytkownika),
foreign key (ID_Lokacji) references Lokacje(ID_Lokacji)
);