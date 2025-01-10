package pwr.bazydanych.bdanych;

import java.util.Vector;

public class AdapterBazyDanych {
    private static AdapterBazyDanych instance;

    private AdapterBazyDanych() {
        instance = this;
    }

    public static AdapterBazyDanych getInstance() {
        if (instance == null) {
            instance = new AdapterBazyDanych();
        }
        return instance;
    }

    public User getUser(String id_user) {
        //TODO
        System.out.println("|" + id_user + "|");
        if (id_user.equals("123")) {
            User user = new User();
            user.imie = "Kocham";
            user.nazwisko = "Pwr";
            user.id = "123";
            user.nrDowodu = "<3";
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
        film3.Tytul = "Harry Potter 2137";
        film3.aktualnykoszt = 30.0;
        film3.dataWypozyczenia = "2021-01-03";
        wynajete.add(film3);


        return wynajete;
    }

    public boolean returnMovie(String id_user, String title) {
        // TODO
        return true;
    }

    public boolean rentMovie(String id_user, String title) {
        // TODO
        return true;
    }

    public boolean validateUser(String id_user) {
        // TODO
        return true;
    }

    public boolean addMovie(String title, String genre, String year, String director_id, String price) {
        // TODO
        // wez dodaj błędy do wyłapania typu film już istnieje w bazie danych albo że jeszcze nie istnieje taki reżyser
        return true;
    }

    public boolean addDirector(String name, String surname) {
        // TODO
        return true;
    }

    public Vector<Film> getMoviesMatching (String expression){
        //TODO
        // Zwracaj liste filmmow pasujacych do wyrazenia np Har znajdzie Harrego  POttera
        return null;
    }

    public Vector<Film> getMoviesByDirector (String director){
        //TODO
        // Zwracaj liste filmow danego rezysera po czesci nazwiskak i imienia np Stev znajdzie Stevena Spielberga ale tez Spiel znajdzide Stevena Spielberga
        return null;
    }
}
