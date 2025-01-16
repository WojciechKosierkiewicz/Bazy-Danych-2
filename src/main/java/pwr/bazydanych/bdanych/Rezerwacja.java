package pwr.bazydanych.bdanych;

public class Rezerwacja {
    public int id;
    public double cena;
    public String data_rozpoczecia;
    public String data_zakonczenia;
    public String tytul;
    public String nazwaLokacji;

    public double getCena() {
        return cena;
    }

    public int getId() {
        return id;
    }

    public String getTytul() {
        return tytul;
    }

    public String getData_rozpoczecia() {
        return data_rozpoczecia;
    }

    public String getData_zakonczenia() {
        return data_zakonczenia;
    }

    public String getNazwaLokacji() {
        return nazwaLokacji;
    }
}
