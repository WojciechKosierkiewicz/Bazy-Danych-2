package pwr.bazydanych.bdanych;

import java.util.Vector;
import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class AdapterBazyDanych {
    private static AdapterBazyDanych instance;
    private String connectionURL;
    private String user;
    private String password;

    private Connection connection = null;

    private AdapterBazyDanych() {
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

    public Vector<Zamowienie> getZamowienia(String id_user){
        Vector<Zamowienie> zamowienia = new Vector<Zamowienie>();
        String query = "SELECT z.ID_zamowienia, ID_uzytkownika, data_rozpoczecia, data_oczekiwanego_zakonczenia, SUM(cena_czastkowa)*DATEDIFF(SYSDATE(), data_rozpoczecia) as aktualnykoszt" +
        " FROM Zamowienia z JOIN Elementy_zamowien e on e.ID_zamowienia = z.ID_zamowienia" +
        " WHERE ID_uzytkownika = ? AND data_faktycznego_zakonczenia IS NULL" +
        " GROUP BY z.ID_zamowienia";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id_user);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Zamowienie zamowienie = new Zamowienie();
                    zamowienie.ID_Zamowienia = rs.getInt("ID_zamowienia");
                    zamowienie.ID_Klienta = rs.getInt("ID_uzytkownika");
                    zamowienie.dataWypozyczenia = rs.getString("data_rozpoczecia");
                    zamowienie.dataZakonczenia = rs.getString("data_oczekiwanego_zakonczenia");
                    zamowienie.koszt = rs.getDouble("aktualnykoszt");
                    zamowienia.add(zamowienie);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
        return zamowienia;
    }


    public Vector<WynajetyFilm> getWynajeteFilmy(int id_zamowienia){
        Vector<WynajetyFilm> wynajete = new Vector<WynajetyFilm>();

        String query = "SELECT Tytul, Cena_dzienna FROM Filmy f " +
                "JOIN Elementy_zamowien e on f.ID_Filmu = e.ID_filmu " +
                "JOIN Zamowienia z on e.ID_zamowienia = z.ID_zamowienia " +
                "WHERE z.ID_zamowienia = ? ";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id_zamowienia);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WynajetyFilm film = new WynajetyFilm();
                    film.Tytul = rs.getString("Tytul");
                    film.aktualnykoszt = rs.getDouble("Cena_dzienna");
                    wynajete.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
        return wynajete;
    }

    public Vector<Film> getMoviesInLocation(int id_lokacji, String tytul, String rezyser){
        Vector<Film> movies = new Vector<Film>();
        StringBuilder queryBuilder = new StringBuilder("SELECT f.Tytul, r.Imie, r.Nazwisko, f.Gatunek, d.Ilosc, f.Cena_dzienna FROM Filmy f " +
                "JOIN Rezyser r on f.ID_Rezyser = r.ID_Rezyser " +
                "JOIN DostepnoscFilmu d on f.ID_filmu = d.ID_filmu " +
                "WHERE d.ID_Lokacji = ? AND d.Ilosc > 0");

        if (rezyser != null && !rezyser.isEmpty()) {
            queryBuilder.append(" AND (r.Nazwisko LIKE ? OR r.Imie LIKE ?)");
        }
        if (tytul != null && !tytul.isEmpty()) {
            queryBuilder.append(" AND f.Tytul LIKE ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
            int index = 1;

            stmt.setInt(index++, id_lokacji);
            if (rezyser != null && !rezyser.isEmpty()) {
                stmt.setString(index++, "%" + rezyser + "%");
                stmt.setString(index++, "%" + rezyser + "%");
            }
            if (tytul != null && !tytul.trim().isEmpty()) {
                stmt.setString(index++, "%" + tytul + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.tytul = rs.getString("Tytul");
                    film.rezyserNazwisko = rs.getString("Nazwisko");
                    film.rezyserImie = rs.getString("Imie");
                    film.gatunek = rs.getString("Gatunek");
                    film.Ilosc = rs.getInt("Ilosc");
                    film.cena = rs.getDouble("Cena_dzienna");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
        return movies;
    }

    public boolean addReservation(Vector<Film> filmy, String lokalizacja, String id_uzytkownika, String data_rozpoczecia, String data_oczekiwanego_zakonczenia){
        String query = "SELECT COUNT(*) as count FROM Uzytkownicy WHERE ID_uzytkownika = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id_uzytkownika);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt("count") == 0) {
                        System.out.println("User not found");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
            return false;
        }
        for(Film film : filmy){
            String procedureCall = "CALL DodajRezerwacje(?, ?, ?, ?, ?);";
            try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
                stmt.setString(1, id_uzytkownika);
                stmt.setString(2, lokalizacja);
                stmt.setInt(3, film.id);
                stmt.setString(4, data_rozpoczecia);
                stmt.setString(5, data_oczekiwanego_zakonczenia);
                System.out.println("Zapytanie SQL: " + procedureCall);
                System.out.println("ID_filmu: " + film.id + " ID_lokacji: " + lokalizacja + " ID_uzytkownika: " + id_uzytkownika + " data_rozpoczecia: " + data_rozpoczecia + " data_oczekiwanego_zakonczenia: " + data_oczekiwanego_zakonczenia);
                stmt.execute();
            } catch (SQLException e) {
                System.err.println("Błąd procedury: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean isUserValidated(String id_user) {
        String query = "SELECT zatwierdzony FROM Uzytkownicy WHERE ID_uzytkownika = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id_user);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("zatwierdzony");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user: " + e.getMessage());
        }
        return false;
    }

    public boolean validateUser(String id_user) {
        String procedureCall = "CALL ZatwierdzUzytkownika(?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, id_user);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean invalidateUser(String id_user) {
        String procedureCall = "CALL DezatwierdzUzytkownika(?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, id_user);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean addMovie(String title, String genre, Double CenaDziennaFilm, int director_id) {
        String procedureCall = "CALL DodajFilm(?, ?, ?, ?);";

        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, title);
            stmt.setString(2, genre);
            stmt.setDouble(3, CenaDziennaFilm);
            stmt.setInt(4, director_id);

            stmt.execute();
            System.out.println("Film dodany pomyślnie.");
            return true;
        } catch (SQLException e) {
            if ("45001".equals(e.getSQLState())) {
                System.err.println("Błąd procedury: Reżyser nie istnieje.");
            } else if ("45002".equals(e.getSQLState())) {
                System.err.println("Błąd procedury: Film już istnieje.");
            } else if ("45003".equals(e.getSQLState())) {
                System.err.println("Błąd procedury: Cena dzienna musi być większa od zera.");
            } else {
                System.err.println("Nieznany błąd SQL: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Nieznany błąd: " + e.getMessage());
        }
        return false;
    }

    public boolean addDirector(String name, String surname) {
        String procedureCall = "CALL DodajRezysera(?, ?);";
        try(CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.execute();
            System.out.println("Reżyser dodany pomyślnie.");
            return true;
        } catch (SQLException e) {
            if ("45000".equals(e.getSQLState())) {
                System.err.println("Błąd procedury: Reżyser już istnieje.");
            } else {
                System.err.println("Nieznany błąd SQL: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Nieznany błąd: " + e.getMessage());
        }
        return true;
    }


    public Vector<Film> getMoviesByArg(String Director, String Title, String Genre){
        Vector<Film> movies = new Vector<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT f.Tytul, r.Imie, r.Nazwisko, f.Gatunek " +
                "FROM Filmy f " +
                "JOIN Rezyser r ON f.ID_Rezyser = r.ID_Rezyser WHERE 1=1");

        if (Director != null && !Director.trim().isEmpty()) {
            queryBuilder.append(" AND (r.Nazwisko LIKE ? OR r.Imie LIKE ?)");
        }
        if (Title != null && !Title.trim().isEmpty()) {
            queryBuilder.append(" AND f.Tytul LIKE ?");
        }
        if (Genre != null && !Genre.trim().isEmpty()) {
            queryBuilder.append(" AND f.Gatunek LIKE ?");
        }


        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
            int index = 1;


            if (Director != null && !Director.isEmpty()) {
                stmt.setString(index++, "%" + Director + "%");
                stmt.setString(index++, "%" + Director + "%");
            }
            if (Title != null && !Title.trim().isEmpty()) {
                stmt.setString(index++, "%" + Title + "%");
            }
            if (Genre != null && !Genre.trim().isEmpty()) {
                stmt.setString(index++, "%" + Genre + "%");
            }

            System.out.println("Zapytanie SQL: " + queryBuilder.toString());

            // Wykonanie zapytania
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    Rezyser rezyser = new Rezyser();
                    film.tytul = rs.getString("Tytul");
                    film.rezyserNazwisko = rs.getString("Nazwisko");
                    film.rezyserImie = rs.getString("Imie");
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

    public Vector<Lokacja> getLokacje(){
        Lokacja lokacja = null;
        String query = "SELECT ID_lokacji, nazwa, adres, nr_telefonu FROM Lokacje";

        Vector<Lokacja> lokacje = new Vector<Lokacja>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lokacja = new Lokacja();
                    lokacja.id = rs.getInt("ID_lokacji");
                    lokacja.nazwa = rs.getString("nazwa");
                    lokacja.adres = rs.getString("adres");
                    lokacja.nr_telefonu = rs.getString("nr_telefonu");
                    lokacje.add(lokacja);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching locations: " + e.getMessage());
        }
        return lokacje;
    }

    public Vector<Rezyser> getRezyserzy(){
        Rezyser rezyser = null;
        String query = "SELECT ID_Rezyser, Imie, Nazwisko FROM Rezyser";

        Vector<Rezyser> rezyserzy = new Vector<Rezyser>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rezyser = new Rezyser();
                    rezyser.id = rs.getInt("ID_Rezyser");
                    rezyser.imie = rs.getString("Imie");
                    rezyser.nazwisko = rs.getString("Nazwisko");
                    rezyserzy.add(rezyser);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching directors: " + e.getMessage());
        }
        return rezyserzy;
    }

    public Vector<Rezyser> getRezyserzyByName(String name){
        return getRezyserzy();
    }

    public Vector<Film> getAvailableMovies(String Title, String Director, int ID_Lokacji){
        Vector<Film> movies = new Vector<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT f.Tytul, r.Imie, r.Nazwisko, f.Gatunek, d.Ilosc " +
                "FROM Filmy f " +
                "JOIN Rezyser r ON f.ID_Rezyser = r.ID_Rezyser" +
                "JOIN DostepnoscFilmu d on f.ID_filmu = d.ID_filmu" +
                "JOIN Lokacje l on l.ID_Lokacji = d.ID_Lokacji" +
                "WHERE Lokacje.ID_Lokacji = ?");

        if (Director != null && !Director.trim().isEmpty()) {
            queryBuilder.append(" AND (r.Nazwisko LIKE ? OR r.Imie LIKE ?)");
        }
        if (Title != null && !Title.trim().isEmpty()) {
            queryBuilder.append(" AND f.Tytul LIKE ?");
        }

        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder.toString())) {
            int index = 1;

            stmt.setInt(index++, ID_Lokacji);

            if (Director != null && !Director.isEmpty()) {
                stmt.setString(index++, "%" + Director + "%");
                stmt.setString(index++, "%" + Director + "%");
            }
            if (Title != null && !Title.trim().isEmpty()) {
                stmt.setString(index++, "%" + Title + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.tytul = rs.getString("Tytul");
                    film.rezyserNazwisko = rs.getString("Nazwisko");
                    film.rezyserImie = rs.getString("Imie");
                    film.gatunek = rs.getString("Gatunek");
                    film.Ilosc = rs.getInt("Ilosc");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }
        return movies;
    }

    public Vector<Film> getLocationsMovies(int idLokacji) {
        Vector<Film> movies = new Vector<>();
        String query = "SELECT f.ID_Filmu, f.Tytul, df.Ilosc, f.Cena_dzienna " +
                "FROM Filmy f " +
                "LEFT JOIN DostepnoscFilmu df ON f.ID_Filmu = df.ID_Filmu " +
                "WHERE ID_Lokacji = ? " +
                "UNION ALL " +
                "SELECT f.ID_Filmu, f.Tytul, 0, f.Cena_dzienna " +
                "FROM Filmy f " +
                "JOIN DostepnoscFilmu df ON f.ID_Filmu = df.ID_Filmu " +
                "WHERE ID_Lokacji != ? AND f.ID_Filmu NOT IN (SELECT ID_Filmu FROM DostepnoscFilmu WHERE ID_Lokacji = ?) " +
                "GROUP BY f.ID_Filmu";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idLokacji);
            stmt.setInt(2, idLokacji);
            stmt.setInt(3, idLokacji);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.id = rs.getInt("ID_Filmu");
                    film.tytul = rs.getString("Tytul");
                    film.Ilosc = rs.getInt("Ilosc");
                    film.cena = rs.getDouble("Cena_dzienna");
                    movies.add(film);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }
}

