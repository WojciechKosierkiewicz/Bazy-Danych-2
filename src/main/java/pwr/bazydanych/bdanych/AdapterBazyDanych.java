package pwr.bazydanych.bdanych;

import java.util.Vector;
import java.sql.*;
import javax.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class AdapterBazyDanych {
    private static AdapterBazyDanych instance;
    private String connectionURL;
    private String user;
    private String password;

    private Connection connection = null;

    private AdapterBazyDanych() {
        Dotenv dotenv = Dotenv.load();
        connectionURL = Dotenv.load().get("CONNECTION_URL");
        user = Dotenv.load().get("USER");
        password = Dotenv.load().get("PASSWORD");
        this.connection = connect();
        instance = this;
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connectionURL, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static AdapterBazyDanych getInstance() {
        if (instance == null) {
            instance = new AdapterBazyDanych();
        }
        return instance;
    }

    public User getUser(String id_user) {
        //TODO
        User user = null;
        String query = "SELECT ID_uzytkownika, imie, nazwisko, nrdowodu FROM Uzytkownicy WHERE ID_uzytkownika = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id_user);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.imie = rs.getString("imie");
                    user.nazwisko = rs.getString("nazwisko");
                    user.id = rs.getString("ID_uzytkownika");
                    user.nrDowodu = rs.getString("nrdowodu");
                } else {
                    System.out.println("User not found");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
            return null;
        }

        return user;
    }

    public Vector<WynajetyFilm> getFilmyWynajeteBy(String id_user) {
        Vector<WynajetyFilm> wynajete = new Vector<WynajetyFilm>();
        String query = "SELECT f.Tytul, DATEDIFF(sysdate(), data_rozpoczecia) * f.Cena_dzienna as aktualnykoszt, z.data_rozpoczecia FROM Zamowienia z " +
                "JOIN Elementy_zamowien e on z.ID_zamowienia = e.ID_zamowienia " +
                "JOIN Filmy f on e.ID_filmu = f.ID_filmu " +
                "WHERE z.ID_uzytkownika = ? AND z.data_faktycznego_zakonczenia IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id_user);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WynajetyFilm film = new WynajetyFilm();
                    film.Tytul = rs.getString("Tytul");
                    film.aktualnykoszt = rs.getDouble("aktualnykoszt");
                    film.dataWypozyczenia = rs.getString("data_rozpoczecia");
                    wynajete.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
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

    public boolean isUserValidated(String id_user) {
        // TODO
        boolean validated = false;

        return validated;
    }

    public boolean validateUser(String id_user) {
        // TODO
        //dodatkowa kolumna w bazie
        return true;
    }

    public boolean addMovie(String title, String genre, String year, String director_id, String price) {
        // TODO

        return true;
    }

    public boolean addDirector(String name, String surname) {

        return true;
    }

    public Vector<Film> getMoviesMatching (String expression){
        Vector<Film> movies = new Vector<>();
        String query = "SELECT f.Tytul, r.Nazwisko, f.Gatunek FROM Filmy f JOIN Rezyser r on f.ID_Rezyser = r.ID_Rezyser WHERE f.Tytul LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + expression + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.tytul = rs.getString("Tytul");
                    film.rezyser = rs.getString("Nazwisko");
                    film.gatunek = rs.getString("Gatunek");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }

    public Vector<Film> getMoviesByDirector (String director){
        //TODO
        Vector<Film> movies = new Vector<>();
        String query = "SELECT f.Tytul, r.Nazwisko, f.Gatunek FROM Filmy f JOIN Rezyser r on f.ID_Rezyser = r.ID_Rezyser WHERE r.Nazwisko = ? OR r.Imie = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, director);
            stmt.setString(2, director);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.tytul = rs.getString("Tytul");
                    film.rezyser = rs.getString("Nazwisko");
                    film.gatunek = rs.getString("Gatunek");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
        return null;
    }

    public Vector<Film> getMoviesByArg(String Director, String Title, String Genre){
        Vector<Film> movies = new Vector<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT f.Tytul, r.Nazwisko, f.Gatunek FROM Filmy f JOIN Rezyser r ON f.ID_Rezyser = r.ID_Rezyser WHERE 1=1");

        if (Director != null && !Director.isEmpty()) {
            queryBuilder.append(" AND (r.Nazwisko = ? OR r.Imie = ?)");
        }
        if (Title != null && !Title.isEmpty()) {
            queryBuilder.append(" AND f.Tytul = ?");
        }
        if (Genre != null && !Genre.isEmpty()) {
            queryBuilder.append(" AND f.Gatunek = ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
            int index = 1;
            if (Director != null && !Director.isEmpty()) {
                stmt.setString(index++, '%' + Director + '%');
            }
            if (Title != null && !Title.isEmpty()) {
                stmt.setString(index++, '%' + Title + '%');
            }
            if (Genre != null && !Genre.isEmpty()) {
                stmt.setString(index++, '%' + Genre + '%');
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.tytul = rs.getString("Tytul");
                    film.rezyser = rs.getString("Nazwisko");
                    film.gatunek = rs.getString("Gatunek");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
        return movies;
    }

    public Vector<String> getGenres(){
        Vector<String> genres = new Vector<>();
        String query = "SELECT DISTINCT Gatunek FROM Filmy";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(rs.getString("Gatunek"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching genres: " + e.getMessage());
        }
        return genres;
    }
}

