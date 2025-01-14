package pwr.bazydanych.bdanych;

public class Film {
    public String tytul;
    public Rezyser rezyser;
    public String gatunek;

    public String getGatunek() {
        return gatunek;
    }

    public Rezyser getRezyser() {
        return rezyser;
    }

    public String getTytul() {
        return tytul;
    }

    public void setGatunek(String gatunek) {
        this.gatunek = gatunek;
    }

    public void setRezyser(Rezyser rezyser) {
        this.rezyser = rezyser;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }
}
