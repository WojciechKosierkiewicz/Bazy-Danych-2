CREATE TABLE Elementy_zamowien(
ID_zamowienia int not null,
ID_filmu int,
ilosc int,
cena_czastkowa int,
PRIMARY KEY(ID_zamowienia, ID_filmu),
foreign key (ID_filmu) references Filmy(ID_filmu),
foreign key (ID_zamowienia) references Zamowienia(ID_zamowienia)
);

