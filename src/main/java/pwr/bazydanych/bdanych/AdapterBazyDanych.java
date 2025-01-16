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

    public boolean changePrice(int id_filmu, double cena){
        String procedureCall = "CALL AktualizujCene(?, ?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, id_filmu);
            stmt.setDouble(2, cena);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean changeMovieAvailability(int id_filmu, int id_lokacji, int ilosc){
        String procedureCall = "CALL AktualizujDostepnosc(?, ?, ?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, id_filmu);
            stmt.setInt(2, id_lokacji);
            stmt.setInt(3, ilosc);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }


    public Vector<Zamowienie> getZamowienia(String id_user){
        Vector<Zamowienie> zamowienia = new Vector<Zamowienie>();
        String query = "SELECT z.ID_zamowienia, ID_uzytkownika, data_rozpoczecia, data_oczekiwanego_zakonczenia, SUM(e.cena_czastkowa)*DATEDIFF(SYSDATE(), data_rozpoczecia) as aktualnykoszt" +
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

    public Vector<Rezerwacja> getReservations(String user){
        Vector<Rezerwacja> rezerwacje = new Vector<Rezerwacja>();
        String query = "SELECT r.ID_rezerwacji, f.Tytul, f.Cena_dzienna, l.Nazwa, r.data_rozpoczecia, r.date_zakonczenia " +
                "FROM Rezerwacje r " +
                "JOIN Filmy f on r.ID_filmu = f.ID_filmu " +
                "JOIN Lokacje l on r.ID_lokacji = l.ID_lokacji " +
                "WHERE r.ID_uzytkownika = ? AND r.date_zakonczenia > SYSDATE()";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rezerwacja rezerwacja = new Rezerwacja();
                    rezerwacja.id = rs.getInt("ID_rezerwacji");
                    rezerwacja.tytul = rs.getString("Tytul");
                    rezerwacja.cena = rs.getDouble("Cena_dzienna");
                    rezerwacja.nazwaLokacji = rs.getString("Nazwa");
                    rezerwacja.data_rozpoczecia = rs.getString("data_rozpoczecia");
                    rezerwacja.data_zakonczenia = rs.getString("date_zakonczenia");
                    rezerwacje.add(rezerwacja);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reservations: " + e.getMessage());
        }
        return rezerwacje;
    }

    public boolean closeReservation(int id_rezerwacji){
        String procedureCall = "CALL ZakonczRezerwacje(?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, id_rezerwacji);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }


    public Vector<Film> getMoviesInLocation(int id_lokacji, String tytul, String rezyser){
        Vector<Film> movies = new Vector<Film>();
        StringBuilder queryBuilder = new StringBuilder("SELECT f.ID_filmu, f.Tytul, r.Imie, r.Nazwisko, f.Gatunek, d.Ilosc, f.Cena_dzienna FROM Filmy f " +
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
                    film.id = rs.getInt("ID_Filmu");
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
        Vector<Rezyser> rezyserzy = new Vector<Rezyser>();
        String query = "SELECT ID_Rezyser, Imie, Nazwisko FROM Rezyser WHERE Imie LIKE ? OR Nazwisko LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, '%' + name + '%');
            stmt.setString(2, '%' + name + '%');
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rezyser rezyser = new Rezyser();
                    rezyser.id = rs.getInt("ID_Rezyser");
                    rezyser.imie = rs.getString("Imie");
                    rezyser.nazwisko = rs.getString("Nazwisko");
                    rezyserzy.add(rezyser);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching directors: " + e.getMessage());
        }
        return getRezyserzy();
    }

    public boolean rentMovie(Vector<Film> films , int id_lokacji, String id_uzytkownika, String data_oczekiwanego_zakonczenia, Vector<Film> failedMovies) {
        String usersReservationstToClose = "SELECT ID_rezerwacji FROM Rezerwacje WHERE ID_uzytkownika = ? AND ID_Filmu = ? AND SYSDATE() BETWEEN data_rozpoczecia AND date_zakonczenia";
        String createOrderProcedure = "CALL DodajWypozyczenie(?, ?, ?, ?);";
        String addElementProcedure = "CALL DodajFilmDoWypozyczenia(?, ?, ?);";
        String deleteOrderQuery = "DELETE FROM Zamowienia WHERE ID_zamowienia = ?;";
        int orderId = -1;
        Vector<Integer> ReservationsToClose = new Vector<>();
        System.out.println("Renting movies");
        try (Connection conn = DriverManager.getConnection(connectionURL, user, password)) {
            conn.setAutoCommit(false);
            System.out.println("Transaction started");
            try (CallableStatement createOrderStmt = conn.prepareCall(createOrderProcedure)) {
                createOrderStmt.setString(1, id_uzytkownika);
                createOrderStmt.setInt(2, id_lokacji);
                createOrderStmt.setString(3, data_oczekiwanego_zakonczenia);
                createOrderStmt.registerOutParameter(4, Types.INTEGER);
                createOrderStmt.execute();
                orderId = createOrderStmt.getInt(4);

                if (orderId == -1) {
                    conn.rollback();
                    System.out.println("Error creating order, rollback");
                    conn.setAutoCommit(true);
                    return false;
                }
                System.out.println("Order created: " + orderId);
                boolean moviesAdded = true;
                try(PreparedStatement stmt = conn.prepareStatement(usersReservationstToClose)){
                    for (Film film : films) {
                        stmt.setString(1, id_uzytkownika);
                        stmt.setInt(2, film.id);
                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                ReservationsToClose.add(rs.getInt("ID_rezerwacji"));
                                System.out.println("Reservation to close: " + rs.getInt("ID_rezerwacji"));
                            }
                        }
                    }
                    if(!ReservationsToClose.isEmpty()){
                        for(Integer reservation : ReservationsToClose){
                            try (CallableStatement closeReservationstmt = conn.prepareCall("CALL ZakonczRezerwacje(?)")) {
                                closeReservationstmt.setInt(1, reservation);
                                closeReservationstmt.execute();
                                System.out.println("Reservation closed: " + reservation);
                            } catch (SQLException e) {
                                System.out.println("Error closing reservation: " + e.getMessage());
                                conn.rollback();
                                conn.setAutoCommit(true);
                                return false;
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error fetching reservations: " + e.getMessage());
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false;
                }

                for(Film film: films){
                    try (CallableStatement addElementStmt = conn.prepareCall(addElementProcedure)) {
                        addElementStmt.setInt(1, orderId);
                        addElementStmt.setInt(2, film.id);
                        addElementStmt.setInt(3, 1);
                        addElementStmt.execute();
                        System.out.println("Movie added to order: " + film.tytul);
                    } catch (SQLException e) {
                        System.out.println("Error adding movie to order: " + e.getMessage());
                        moviesAdded = false;
                        failedMovies.add(film);
                    }
                }

                if (!moviesAdded) {
                    try (PreparedStatement deleteOrderStmt = conn.prepareStatement(deleteOrderQuery)) {
                        deleteOrderStmt.setInt(1, orderId);
                        deleteOrderStmt.executeUpdate();
                    }
                    conn.rollback();
                    return false;
                }

                conn.commit();
                System.out.println("Transaction successful");
                conn.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed: " + e.getMessage());
                conn.setAutoCommit(true);
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public Vector<Film> getAvailableMovies(int ID_Lokacji){
        Vector<Film> movies = new Vector<>();
        String queryBuilder = "SELECT f.ID_Filmu, f.Tytul, f.Cena_dzienna FROM Filmy f " +
                "JOIN DostepnoscFilmu d ON f.ID_Filmu = d.ID_Filmu " +
                "WHERE d.ID_Lokacji = ? AND d.Ilosc > 0";

        try (PreparedStatement stmt = connection.prepareStatement(queryBuilder)) {
            stmt.setInt(1, ID_Lokacji);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Film film = new Film();
                    film.id = rs.getInt("ID_Filmu");
                    film.tytul = rs.getString("Tytul");
                    film.cena = rs.getDouble("Cena_dzienna");
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

    public boolean rejestruj(String imie, String nazwisko, String nrdowodu, String id_user) {
        if(imie == null || nazwisko == null || nrdowodu == null || id_user == null){
            return false;
        }

        if(imie.matches(".*\\d.*") || nazwisko.matches(".*\\d.*")){
            throw new IllegalArgumentException("Imie i nazwisko nie mogą zawierać cyfr");
        }else if(nrdowodu.length() != 9){
            throw new IllegalArgumentException("Numer dowodu musi mieć 9 znaków");
        }else if(imie.length() < 3 || nazwisko.length() < 3){
            throw new IllegalArgumentException("Imie i nazwisko musi mieć co najmniej 3 znaki");
        }else {
            String procedureCall = "CALL DodajUzytkownika(?, ?, ?, ?);";
            String id = null;
            try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
                stmt.setString(1, imie);
                stmt.setString(2, nazwisko);
                stmt.setString(3, nrdowodu);
                stmt.execute();
                id = stmt.getString(4);
            } catch (SQLException e) {
                System.err.println("Błąd procedury: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean returnOrder(int id_zamowienia, Double Kara){
        String procedureCall = "CALL ZakonczWypozyczenie(?, ?);";
        try (CallableStatement stmt = connection.prepareCall(procedureCall)) {
            stmt.setInt(1, id_zamowienia);
            stmt.execute();
            Kara = stmt.getDouble(2);
        } catch (SQLException e) {
            System.err.println("Błąd procedury: " + e.getMessage());
            return false;
        }
        return true;
    }
}

