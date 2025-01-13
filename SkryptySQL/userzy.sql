-- Tworzenie użytkowników
CREATE USER 'Pracownik'@'localhost' IDENTIFIED BY 'haslo_pracownik';
CREATE USER 'Klient'@'localhost' IDENTIFIED BY 'haslo_klient';
CREATE USER 'Prezes'@'localhost' IDENTIFIED BY 'haslo_prezes';

-- Nadawanie uprawnień dla Pracownika
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Rezerwacje TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Uzytkownicy TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Filmy TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Rezyser TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Zamowienia TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Elementy_zamowien TO 'Pracownik'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.DostepnoscFilmu TO 'Pracownik'@'localhost';
GRANT SELECT ON wypozyczalnia.Lokacje TO 'Pracownik'@'localhost';

-- Nadawanie uprawnień dla Klienta
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Rezerwacje TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.Filmy TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.Rezyser TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.Zamowienia TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.Elementy_zamowien TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.DostepnoscFilmu TO 'Klient'@'localhost';
GRANT SELECT ON wypozyczalnia.Lokacje TO 'Klient'@'localhost';

-- Nadawanie uprawnień dla Prezesa
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Rezerwacje TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Uzytkownicy TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Filmy TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Rezyser TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Zamowienia TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Elementy_zamowien TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.DostepnoscFilmu TO 'Prezes'@'localhost';
GRANT SELECT, UPDATE, INSERT, DELETE ON wypozyczalnia.Lokacje TO 'Prezes'@'localhost';

-- Zastosowanie zmian
FLUSH PRIVILEGES;
