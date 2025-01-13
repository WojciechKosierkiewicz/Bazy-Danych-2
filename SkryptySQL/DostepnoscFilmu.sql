CREATE TABLE DostepnoscFilmu(
ID_Filmu int,
ID_Lokacji int,
Ilosc int,
FOREIGN KEY (ID_Filmu) REFERENCES Filmy(ID_Filmu),
FOREIGN KEY (ID_Lokacji) REFERENCES Lokacje(ID_Lokacji));
