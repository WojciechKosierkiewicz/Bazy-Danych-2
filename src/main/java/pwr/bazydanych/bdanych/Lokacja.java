package pwr.bazydanych.bdanych;

public class Lokacja {
    public int id;
    public String nazwa;
    public String adres;
    public String nr_telefonu;

    public int getId() {
        return id;
    }

    public String getAdres() {
        return adres;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getNr_telefonu() {
        return nr_telefonu;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public void setNr_telefonu(String nr_telefonu) {
        this.nr_telefonu = nr_telefonu;
    }

    public String toString() {
        return nazwa;
    }
}

