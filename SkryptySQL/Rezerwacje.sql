CREATE TABLE Rezerwacje(
ID_rezerwacji int not null auto_increment,
data_rozpoczecia DATE,
date_zakonczenia Date,
ID_uzytkownika int,
ID_filmu int,
PRIMARY KEY(ID_rezerwacji),
foreign key (ID_uzytkownika) references Uzytkownicy(ID_uzytkownika),
Foreign key (ID_Filmu) references Filmy(Id_filmu) );