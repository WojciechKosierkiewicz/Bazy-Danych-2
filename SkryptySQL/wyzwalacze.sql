delimiter //
CREATE TRIGGER dostepnosc_filmu_trg
    AFTER INSERT
    ON Elementy_zamowien FOR EACH ROW
    BEGIN 
	update DostepnoscFilmu set Ilosc = Ilosc - 1 where ID_Filmu = new.ID_Filmu and ID_Lokacji in (select ID_Lokacji from Zamowienia where ID_Zamowienia = new.ID_Zamowienia);
    END;
delimiter //

CREATE TRIGGER WalidacjaCen
BEFORE INSERT ON Filmy
FOR EACH ROW
BEGIN
    IF NEW.Cena_dzienna <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cena dzienna filmu musi być większa od zera.';
    END IF;
END;

delimiter //
CREATE TRIGGER WalidacjaCenPrzyAktualizacji
BEFORE UPDATE ON Filmy
FOR EACH ROW
BEGIN
    IF NEW.Cena_dzienna <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cena dzienna filmu musi być większa od zera.';
    END IF;
END;
