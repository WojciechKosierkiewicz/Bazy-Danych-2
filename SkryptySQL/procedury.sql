DELIMITER // //
CREATE PROCEDURE DodajFilm(
    IN TytulFilm VARCHAR(255),
    IN GatunekFilm VARCHAR(255),
    IN CenaDziennaFilm FLOAT(5,2),
    IN RezyserID INT
)
BEGIN
    DECLARE NowyID_Filmu INT;

    START TRANSACTION;

    -- Sprawdzenie, czy reżyser istnieje
    IF NOT EXISTS (SELECT 1 FROM Rezyser WHERE ID_Rezyser = RezyserID) THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Reżyser nie istnieje.';
    END IF;

    -- Sprawdzenie poprawności ceny dziennej
    IF CenaDziennaFilm <= 0 THEN
        SIGNAL SQLSTATE '45003'
            SET MESSAGE_TEXT = 'Cena dzienna musi być większa od zera.';
    END IF;

    -- Dodanie filmu
    IF NOT EXISTS (SELECT 1 FROM Filmy WHERE Tytul = TytulFilm) THEN
        INSERT INTO Filmy (Tytul, Gatunek, Cena_dzienna, ID_Rezyser)
        VALUES (TytulFilm, GatunekFilm, CenaDziennaFilm, RezyserID);
        SET NowyID_Filmu = LAST_INSERT_ID();
    ELSE
        SIGNAL SQLSTATE '45002'
            SET MESSAGE_TEXT = 'Film o podanym tytule już istnieje.';
    END IF;

    COMMIT;
END //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE DodajRezerwacje(
    IN ID_uzytkownika_var INT,
    IN ID_lokacji_var INT,
    IN ID_filmu_var INT,
    IN data_rozpoczecia_var DATE,
    IN data_zakonczenia_var DATE
)
BEGIN
    DECLARE dostepne_kopie INT;
    DECLARE zarezerwowane_kopie INT;

    IF NOT EXISTS(SELECT 1 FROM Uzytkownicy WHERE Uzytkownicy.ID_uzytkownika = ID_uzytkownika_var) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Użytkownik o podanym ID nie istnieje.';
    END IF;

    IF NOT EXISTS(SELECT 1 FROM Lokacje WHERE Lokacje.ID_Lokacji = ID_lokacji_var) THEN
        SIGNAL SQLSTATE '45001'
        SET MESSAGE_TEXT = 'Lokacja o podanym ID nie istnieje.';
    END IF;

    IF NOT EXISTS(SELECT 1 FROM Filmy WHERE Filmy.ID_Filmu = ID_filmu_var) THEN
        SIGNAL SQLSTATE '45002'
        SET MESSAGE_TEXT = 'Film o podanym ID nie istnieje.';
    END IF;

    SELECT count(*) INTO zarezerwowane_kopie FROM Rezerwacje WHERE ID_Filmu = ID_filmu_var AND ID_Lokacji = ID_lokacji_var AND Data_rozpoczecia <= data_zakonczenia_var AND Date_zakonczenia >= data_rozpoczecia_var;
    SELECT Ilosc INTO dostepne_kopie FROM DostepnoscFilmu WHERE ID_Filmu = ID_filmu_var AND ID_Lokacji = ID_lokacji_var;

    IF dostepne_kopie - zarezerwowane_kopie <= 0 THEN
        SIGNAL SQLSTATE '45003'
        SET MESSAGE_TEXT = 'Brak dostępnych kopii filmu w podanym terminie.';
    END IF;

    -- Dodanie rezerwacji
    INSERT INTO Rezerwacje (ID_uzytkownika, ID_filmu, data_rozpoczecia, date_zakonczenia, ID_lokacji)
    VALUES (ID_uzytkownika_var, ID_filmu_var, data_rozpoczecia_var, data_zakonczenia_var, ID_lokacji_var);

END //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE ZakonczRezerwacje(
    IN ID_rezerwacji_var INT
)
BEGIN
    -- Pobranie danych rezerwacji
    DECLARE ID_filmu_var INT;
    DECLARE ID_lokacji_Var INT;

    SELECT ID_Filmu, ID_Lokacji
    INTO ID_filmu_var, ID_lokacji_var
    FROM Rezerwacje
    WHERE ID_rezerwacji = ID_rezerwacji_var;

    -- Aktualizacja daty zakończenia
    UPDATE Rezerwacje
    SET Date_zakonczenia = sysdate()
    WHERE ID_rezerwacji = ID_rezerwacji_var;
END //

DELIMITER ;
DELIMITER //
DROP PROCEDURE IF EXISTS DodajUzytkownika;
CREATE PROCEDURE DodajUzytkownika(
    IN Imie_var VARCHAR(100),
    IN Nazwisko_var VARCHAR(100),
    IN Nr_dowodu_var VARCHAR(9),
    OUT ID_uzytkownika INT
)
BEGIN
    INSERT INTO Uzytkownicy (Imie, Nazwisko, nrdowodu, zatwierdzony)
    VALUES (Imie_var, Nazwisko_var, Nr_dowodu_var, FALSE);
    SELECT LAST_INSERT_ID() INTO ID_uzytkownika;
END //

DELIMITER ;
DELIMITER //
DROP PROCEDURE IF EXISTS DodajWypozyczenie;
CREATE PROCEDURE DodajWypozyczenie(
    IN ID_uzytkownika_var INT,
    IN ID_lokacji_var INT,
    IN data_przewidywana_zakonczenia_var DATE,
    OUT ID_zamowienia INT
)
BEGIN
    -- Validate input parameters
    IF ID_uzytkownika_var IS NULL OR ID_uzytkownika_var <= 0 THEN
        SIGNAL SQLSTATE '45001'
            SET MESSAGE_TEXT = 'Invalid user ID provided.', MYSQL_ERRNO = 1001;
    END IF;

    IF ID_lokacji_var IS NULL OR ID_lokacji_var <= 0 THEN
        SIGNAL SQLSTATE '45002'
            SET MESSAGE_TEXT = 'Invalid location ID provided.', MYSQL_ERRNO = 1002;
    END IF;

    IF data_przewidywana_zakonczenia_var IS NULL OR data_przewidywana_zakonczenia_var < CURDATE() THEN
        SIGNAL SQLSTATE '45003'
            SET MESSAGE_TEXT = 'Invalid end date provided. Must be today or in the future.', MYSQL_ERRNO = 1003;
    END IF;

    -- Try inserting the order
    BEGIN
        DECLARE EXIT HANDLER FOR SQLEXCEPTION
            BEGIN
                -- Signal a general SQL exception
                SIGNAL SQLSTATE '45004'
                    SET MESSAGE_TEXT = 'An error occurred while inserting the order.', MYSQL_ERRNO = 1004;
            END;

        -- Start a transaction
        START TRANSACTION;

        -- Insert the order
        INSERT INTO Zamowienia (ID_uzytkownika, data_rozpoczecia, data_oczekiwanego_zakonczenia, ID_lokacji)
        VALUES (ID_uzytkownika_var, SYSDATE(), data_przewidywana_zakonczenia_var, ID_lokacji_var);

        -- Get the ID of the inserted order
        SELECT LAST_INSERT_ID() INTO ID_zamowienia;
    END;
END;

DELIMITER ;
DELIMITER //

CREATE PROCEDURE DodajFilmDoWypozyczenia(
    IN ID_wypozyczenia_var INT,
    IN ID_filmu_var INT,
    IN Ilosc_var INT
)
BEGIN
    DECLARE ID_lokacji_var INT;
    DECLARE Cena_dzienna_var FLOAT;

    SELECT Cena_dzienna FROM Filmy WHERE ID_filmu = ID_filmu_var INTO Cena_dzienna_var;

    IF Cena_dzienna_var IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Film o podanym ID nie istnieje.';
    END IF;

    SELECT ID_Lokacji INTO ID_lokacji_var FROM Zamowienia WHERE ID_zamowienia = ID_wypozyczenia_var;
    IF ID_lokacji_var IS NULL THEN
        SIGNAL SQLSTATE '45001'
        SET MESSAGE_TEXT = 'W podanej lokalizacji nie zlozono zamowienia o podanym ID.';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM DostepnoscFilmu WHERE ID_Filmu = ID_filmu_var AND ID_Lokacji = ID_lokacji_var AND Ilosc > 0) THEN
        SIGNAL SQLSTATE '45002'
        SET MESSAGE_TEXT = 'Film nie jest dostępny w podanej lokalizacji.';
    END IF;

    INSERT INTO Elementy_zamowien (ID_zamowienia, ID_filmu, Ilosc, cena_czastkowa)
    VALUES (ID_wypozyczenia_var, ID_filmu_var, Ilosc_var, Cena_dzienna_var);
    COMMIT;
END //

DELIMITER ;
DELIMITER //
DROP PROCEDURE IF EXISTS ZakonczWypozyczenie;
CREATE PROCEDURE ZakonczWypozyczenie(
    IN ID_wypozyczenia_var INT,
    OUT Kara_out FLOAT
)
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE ID_filmu_var INT;
    DECLARE ID_lokacji_var INT;
    DECLARE Cena_dzienna_var FLOAT;
    DECLARE Data_zakonczenia_var DATE;
    DECLARE roznica_dat_var INT;
    DECLARE total_kara FLOAT DEFAULT 0;

    -- Cursor for iterating through movies in the order
    DECLARE cur CURSOR FOR
        SELECT
            Elementy_zamowien.ID_Filmu,
            Lokacje.ID_Lokacji,
            Elementy_zamowien.cena_czastkowa,
            Zamowienia.data_oczekiwanego_zakonczenia
        FROM Zamowienia
                 JOIN Elementy_zamowien ON Zamowienia.ID_zamowienia = Elementy_zamowien.ID_zamowienia
                 JOIN Lokacje ON Zamowienia.ID_Lokacji = Lokacje.ID_Lokacji
        WHERE Zamowienia.ID_zamowienia = ID_wypozyczenia_var;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    -- Update the actual end date for the order
    UPDATE Zamowienia
    SET Data_faktycznego_zakonczenia = SYSDATE()
    WHERE ID_zamowienia = ID_wypozyczenia_var;

    -- Open the cursor
    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO ID_filmu_var, ID_lokacji_var, Cena_dzienna_var, Data_zakonczenia_var;

        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Update the number of available copies for the movie
        UPDATE DostepnoscFilmu
        SET Ilosc = Ilosc + 1
        WHERE ID_Filmu = ID_filmu_var AND ID_Lokacji = ID_lokacji_var;

        -- Calculate the penalty for the movie
        SET roznica_dat_var = DATEDIFF(SYSDATE(), Data_zakonczenia_var);

        IF roznica_dat_var > 0 THEN
            SET total_kara = total_kara + (roznica_dat_var * Cena_dzienna_var * 2);
        END IF;
    END LOOP;

    CLOSE cur;

    -- Set the total penalty to the OUT parameter
    SET Kara_out = total_kara;
END;


DELIMITER ;
DELIMITER //
drop procedure if exists AktualizujDostępnosc;
CREATE PROCEDURE AktualizujDostepnosc(
    IN ID_filmu_var INT,
    IN ID_lokacji_var INT,
    IN nowa_ilosc_var INT
)
BEGIN
    UPDATE DostepnoscFilmu
    SET Ilosc = nowa_ilosc_var
    WHERE ID_Filmu = ID_filmu_var AND ID_Lokacji = ID_lokacji_var;
END //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE AktualizujCene(
    IN ID_filmu_var INT,
    IN nowa_cena_var FLOAT
)
BEGIN
    IF NOT EXISTS(SELECT 1 FROM Filmy WHERE ID_filmu = ID_filmu_var) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Film o podanym ID nie istnieje.';
    ELSE
        UPDATE Filmy
        SET Cena_dzienna = nowa_cena_var
        WHERE ID_filmu = ID_filmu_var;
    END IF;
end //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE ZatwierdzUzytkownika(
    IN ID_uzytkownika_var INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Uzytkownicy WHERE Uzytkownicy.ID_uzytkownika = ID_uzytkownika_var) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Użytkownik nie istnieje.';
    ELSE
        UPDATE Uzytkownicy SET Zatwierdzony = 1 WHERE ID_uzytkownika = ID_uzytkownika_var;
    END IF;
end //
DELIMITER ;
DELIMITER //

CREATE PROCEDURE DezatwierdzUzytkownika(
    IN ID_uzytkownika_var INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Uzytkownicy WHERE Uzytkownicy.ID_uzytkownika = ID_uzytkownika_var) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Użytkownik nie istnieje.';
    ELSE
        UPDATE Uzytkownicy SET Zatwierdzony = 0 WHERE ID_uzytkownika = ID_uzytkownika_var;
    END IF;
end //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE dodajRezysera(
    IN Imie VARCHAR(100),
    IN Nazwisko VARCHAR(100)
)
BEGIN
    IF EXISTS (SELECT 1 FROM Rezyser WHERE Rezyser.Imie = Imie AND Rezyser.Nazwisko = Nazwisko) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Reżyser już istnieje.';
    ELSE
        INSERT INTO Rezyser (Imie, Nazwisko)
        VALUES (Imie, Nazwisko);
    END IF;
END //

DELIMITER ;
DELIMITER //
CREATE PROCEDURE usunFilm(
    IN ID_filmu_var INT
)
BEGIN
    IF NOT EXISTS(SELECT 1 FROM Filmy WHERE Filmy.ID_filmu = ID_filmu_var) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Film o podanym ID nie istnieje.';
    ELSE
        UPDATE DostepnoscFilmu
        SET Ilosc = 0
        WHERE ID_Filmu = ID_filmu_var;
    END IF;
END //

