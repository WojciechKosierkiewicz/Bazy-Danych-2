package pwr.bazydanych.bdanych;

public class Film {
    public String tytul;
    public String rezyser;
    public String gatunek;

    public String getGatunek() {
        return gatunek;
    }

    public String getRezyser() {
        return rezyser;
    }

    public String getTytul() {
        return tytul;
    }

    public void setGatunek(String gatunek) {
        this.gatunek = gatunek;
    }

    public void setRezyser(String rezyser) {
        this.rezyser = rezyser;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }
}
