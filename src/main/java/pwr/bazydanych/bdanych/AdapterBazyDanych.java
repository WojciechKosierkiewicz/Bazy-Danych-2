package pwr.bazydanych.bdanych;

import java.util.Vector;

public class AdapterBazyDanych {
    private static AdapterBazyDanych instance;
    public static AdapterBazyDanych getInstance() {
        if (instance == null) {
            instance = new AdapterBazyDanych();
        }
        return instance;
    }

    public User getUser(String id_user) {
        //TODO
        System.out.println("|" + id_user + "|");
        if (id_user.equals("2137")) {
            User user = new User();
            user.imie = "Jan";
            user.nazwisko = "Paweł";
            user.id = "2137";
            user.nrDowodu = "Potwor";
            return user;
        }
        else {
            System.out.println("err nie znaleziono użytkownika");
            return null;
        }
    }

    public Vector<WynajetyFilm> getFilmyWynajeteBy(String id_user) {
        //TODO
        Vector<WynajetyFilm> wynajete = new Vector<WynajetyFilm>();
        WynajetyFilm film = new WynajetyFilm();
        film.Tytul = "Harry Potter";
        film.aktualnykoszt = 10.0;
        film.dataWypozyczenia = "2021-01-01";
        wynajete.add(film);

        WynajetyFilm film2 = new WynajetyFilm();
        film2.Tytul = "Harry Potter 2";
        film2.aktualnykoszt = 20.0;
        film2.dataWypozyczenia = "2021-01-02";
        wynajete.add(film2);

        WynajetyFilm film3 = new WynajetyFilm();
        film3.Tytul = "Harry Potter 3";
        film3.aktualnykoszt = 30.0;
        film3.dataWypozyczenia = "2021-01-03";
        wynajete.add(film3);


        return wynajete;
    }
}
