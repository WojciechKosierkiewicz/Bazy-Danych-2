package pwr.bazydanych.bdanych;

public class WynajetyFilm {
    public double aktualnykoszt;
    public String dataWypozyczenia;
    public String Tytul;
    public int ID_Zamowienia;

    public String getTytul() {
        return Tytul;
    }

    public void setTytul(String tytul) {
        this.Tytul = tytul;
    }

    public String getDataWypozyczenia() {
        return dataWypozyczenia;
    }

    public void setDataWypozyczenia(String dataWypozyczenia) {
        this.dataWypozyczenia = dataWypozyczenia;
    }

    public double getAktualnykoszt() {
        return aktualnykoszt;
    }

    public void setAktualnykoszt(double aktualnykoszt) {
        this.aktualnykoszt = aktualnykoszt;
    }

    public int getID_Zamowienia() {
        return ID_Zamowienia;
    }
}
