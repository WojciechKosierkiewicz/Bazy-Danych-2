package pwr.bazydanych.bdanych;

public class Film {
    public int id;
    public String tytul;
    public String rezyserNazwisko;
    public String rezyserImie;
    public String gatunek;
    public int Ilosc;
    public double cena;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIlosc() {
        return Ilosc;
    }

    public void setIlosc(int ilosc) {
        Ilosc = ilosc;
    }

    public String getGatunek() {
        return gatunek;
    }

    public String getRezyserImie() {
        return rezyserImie;
    }

    public String getRezyserNazwisko() {
        return rezyserNazwisko;
    }

    public double getCena() {
        return cena;
    }

    public String getRezyser() {
        return rezyserImie + " " + rezyserNazwisko;
    }



    public String getTytul() {
        return tytul;
    }

    public void setGatunek(String gatunek) {
        this.gatunek = gatunek;
    }

    public void setRezyserImie(String rezyserImie) {
        this.rezyserImie = rezyserImie;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }
}
