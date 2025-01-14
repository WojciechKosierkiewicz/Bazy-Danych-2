package pwr.bazydanych.bdanych;

public class Film {
    public String tytul;
    public String rezyserNazwisko;
    public String rezyserImie;
    public String gatunek;

    public String getGatunek() {
        return gatunek;
    }

    public String getRezyserImie() {
        return rezyserImie;
    }

    public String getRezyserNazwisko() {
        return rezyserNazwisko;
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
