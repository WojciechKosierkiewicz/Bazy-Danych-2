package pwr.bazydanych.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pwr.bazydanych.SharedState;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.WynajetyFilm;

import java.util.Vector;

public class UserViewController {
    @FXML
    private TableView<WynajetyFilm> HistoriaWynajmu;
    @FXML
    private TableColumn<WynajetyFilm, String> tytulColumn;
    @FXML
    private TableColumn<WynajetyFilm, String> dataWynajmuColumn;
    @FXML
    private TableColumn<WynajetyFilm, Double> kosztColumn;
    @FXML
    public void initialize() {
        AdapterBazyDanych adapter = AdapterBazyDanych.getInstance();
        System.out.println("SharedState.username: " + SharedState.username);
        Vector<WynajetyFilm> wynajete = adapter.getFilmyWynajeteBy(SharedState.username);
        System.out.println("Number of rented movies: " + wynajete.size());
        for (WynajetyFilm film : wynajete) {
            System.out.println("Movie: " + film.Tytul + ", Cost: " + film.aktualnykoszt + ", Date: " + film.dataWypozyczenia);
        }
        ObservableList<WynajetyFilm> wynajetelist = FXCollections.observableArrayList(wynajete);
        tytulColumn.setCellValueFactory(new PropertyValueFactory<>("Tytul"));
        dataWynajmuColumn.setCellValueFactory(new PropertyValueFactory<>("dataWypozyczenia"));
        kosztColumn.setCellValueFactory(new PropertyValueFactory<>("aktualnykoszt"));

        HistoriaWynajmu.setItems(wynajetelist);

    }

    @FXML
    public void browsemoviescliked() {
        util.switch_scene("MovieBrowser.fxml");
    }
}
