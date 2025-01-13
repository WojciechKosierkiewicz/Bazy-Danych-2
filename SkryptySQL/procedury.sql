DELIMITER //

CREATE PROCEDURE DodajFilm(
    IN TytulFilm VARCHAR(255),
    IN GatunekFilm VARCHAR(255),
    IN CenaDziennaFilm FLOAT(3,2),
    IN RezyserID INT,
    IN LokacjaID INT,
    IN IloscFilmow INT
)
BEGIN
	DECLARE NowyID_Filmu INT;
    -- Rozpoczynamy transakcję
    START TRANSACTION;
    
    

    -- Sprawdzenie, czy reżyser istnieje
    IF NOT EXISTS (SELECT 1 FROM Rezyser WHERE ID_Rezyser = RezyserID) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Reżyser nie istnieje.';
    END IF;

    -- Sprawdzenie, czy lokacja istnieje
    IF NOT EXISTS (SELECT 1 FROM Lokacje WHERE ID_Lokacji = LokacjaID) THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Lokacja nie istnieje.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM Filmy WHERE Tytul = TytulFilm) THEN
		INSERT INTO Filmy (Tytul, Gatunek, Cena_dzienna, ID_Rezyser)
		VALUES (TytulFilm, GatunekFilm, CenaDziennaFilm, RezyserID);
		-- Pobranie ID dodanego filmu jeżeli dodano
		SET NowyID_Filmu = LAST_INSERT_ID();
	ELSE
		-- W przeciwnym wypadku bierzemy ID z tabeli Filmy
		SELECT ID_Filmu INTO NowyID_Filmu
		FROM Filmy
		WHERE Tytul = TytulFilm;
	END IF;

    -- Dodanie informacji o dostępności filmu do tabeli DostepnoscFilmu
    INSERT INTO DostepnoscFilmu (ID_Filmu, ID_Lokacji, Ilosc)
    VALUES (NowyID_Filmu, LokacjaID, IloscFilmow);

    -- Zatwierdzenie transakcji
    COMMIT;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE DodajRezerwację(
    IN ID_uzytkownika INT,
    IN ID_filmu INT,
    IN ID_lokacji INT,
    IN data_rozpoczecia DATE,
    IN data_zakonczenia DATE
)
BEGIN
    -- Sprawdzenie, czy użytkownik istnieje
    IF NOT EXISTS (SELECT 1 FROM Uzytkownicy WHERE ID_uzytkownika = ID_uzytkownika) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Użytkownik nie istnieje.';
    END IF;

    -- Sprawdzenie dostępności filmu
    IF NOT EXISTS (SELECT 1 FROM DostepnoscFilmu WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji AND Ilosc > 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Film nie jest dostępny w wybranej lokalizacji.';
    END IF;

    -- Dodanie rezerwacji
    INSERT INTO Rezerwacje (ID_uzytkownika, FilmyID_Filmu, Data_rozpoczecia, Data_zakonczenia)
    VALUES (ID_uzytkownika, ID_filmu, data_rozpoczecia, data_zakonczenia);

    -- Zmniejszenie liczby kopii filmu
    UPDATE DostepnoscFilmu
    SET Ilosc = Ilosc - 1
    WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE ZakończRezerwację(
    IN ID_rezerwacji INT
)
BEGIN
    -- Pobranie danych rezerwacji
    DECLARE ID_filmu INT;
    DECLARE ID_lokacji INT;

    SELECT FilmyID_Filmu, LokacjeID_Lokacji
    INTO ID_filmu, ID_lokacji
    FROM Rezerwacje
    WHERE ID_rezerwacji = ID_rezerwacji;

    -- Aktualizacja daty zakończenia
    UPDATE Rezerwacje
    SET Data_zakonczenia = CURRENT_DATE()
    WHERE ID_rezerwacji = ID_rezerwacji;

    -- Zwiększenie liczby kopii filmu
    UPDATE DostepnoscFilmu
    SET Ilosc = Ilosc + 1
    WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE DodajUżytkownika(
    IN Imie VARCHAR(100),
    IN Nazwisko VARCHAR(100),
    IN Nr_dowodu VARCHAR(9)
)
BEGIN
    INSERT INTO Uzytkownicy (Imie, Nazwisko, Nr_dowodu)
    VALUES (Imie, Nazwisko, Nr_dowodu);
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE DodajWypożyczenie(
    IN ID_uzytkownika INT,
    IN ID_filmu INT,
    IN ID_lokacji INT,
    IN data_rozpoczecia DATE,
    IN data_przewidywana_zakonczenia DATE
)
BEGIN
    -- Sprawdzenie dostępności filmu
    IF NOT EXISTS (SELECT 1 FROM DostepnoscFilmu WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji AND Ilosc > 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Film nie jest dostępny w wybranej lokalizacji.';
    END IF;

    -- Dodanie wypożyczenia
    INSERT INTO Zamowienia (ID_uzytkownika, FilmyID_Filmu, LokacjeID_Lokacji, Data_rozpoczecia, Data_przewidywana_zakonczenia)
    VALUES (ID_uzytkownika, ID_filmu, ID_lokacji, data_rozpoczecia, data_przewidywana_zakonczenia);

    -- Zmniejszenie liczby kopii filmu
    UPDATE DostepnoscFilmu
    SET Ilosc = Ilosc - 1
    WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE ZakończWypożyczenie(
    IN ID_wypozyczenia INT,
    IN data_zwrotu DATE
)
BEGIN
    -- Pobranie danych wypożyczenia
    DECLARE ID_filmu INT;
    DECLARE ID_lokacji INT;
    DECLARE Cena_dzienna FLOAT;

    SELECT FilmyID_Filmu, LokacjeID_Lokacji, Cena_dzienna
    INTO ID_filmu, ID_lokacji, Cena_dzienna
    FROM Zamowienia
    JOIN Filmy ON Zamowienia.FilmyID_Filmu = Filmy.ID_Filmu
    WHERE ID_wypozyczenia = ID_wypozyczenia;

    -- Aktualizacja daty zwrotu
    UPDATE Zamowienia
    SET Data_faktycznego_zakonczenia = data_zwrotu,
        Kwota_zamowienia = DATEDIFF(data_zwrotu, Data_rozpoczecia) * Cena_dzienna
    WHERE ID_wypozyczenia = ID_wypozyczenia;

    -- Zwiększenie liczby kopii filmu
    UPDATE DostepnoscFilmu
    SET Ilosc = Ilosc + 1
    WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji;
END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE AktualizujDostępność(
    IN ID_filmu INT,
    IN ID_lokacji INT,
    IN nowa_ilosc INT
)
BEGIN
    UPDATE DostepnoscFilmu
    SET Ilosc = nowa_ilosc
    WHERE FilmyID_Filmu = ID_filmu AND LokacjeID_Lokacji = ID_lokacji;
END //

DELIMITER ;

