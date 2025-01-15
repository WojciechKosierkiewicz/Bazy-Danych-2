package pwr.bazydanych.bdanych;

import java.util.Vector;

public class Zamowienie {
    public int ID_Zamowienia;
    public int ID_Klienta;
    public Vector<Film> Filmy;
    public double koszt;
    public String dataWypozyczenia;
    public String dataZakonczenia; //przewidywana data zakonczenia zamowienia
    public String dataZwrotu; //realna data zwrotu filmu przez uzytkownika

    public double getKoszt() {
        return koszt;
    }

    public void setKoszt(double koszt) {
        this.koszt = koszt;
    }

    public int getID_Klienta() {
        return ID_Klienta;
    }

    public int getID_Zamowienia() {
        return ID_Zamowienia;
    }

    public String getDataWypozyczenia() {
        return dataWypozyczenia;
    }

    public String getDataZakonczenia() {
        return dataZakonczenia;
    }

    public String getDataZwrotu() {
        return dataZwrotu;
    }

}
