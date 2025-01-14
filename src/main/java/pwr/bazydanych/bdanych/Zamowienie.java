package pwr.bazydanych.bdanych;

import java.util.Vector;

public class Zamowienie {
    int ID_Zamowienia;
    int ID_Klienta;
    Vector<Film> Filmy;
    double koszt;
    String dataWypozyczenia;
    String dataZakonczenia; //przewidywana data zakonczenia zamowienia
    String dataZwrotu; //realna data zwrotu filmu przez uzytkownika
}
