package pwr.bazydanych.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.w3c.dom.events.Event;
import pwr.bazydanych.bdanych.Film;
import pwr.bazydanych.bdanych.Lokacja;

import java.util.Vector;

public class MovieReservationController
{
    @javafx.fxml.FXML
    private TextField rezyser;
    @javafx.fxml.FXML
    private DatePicker enddate;
    @javafx.fxml.FXML
    private TableView tabelafilmow;
    @javafx.fxml.FXML
    private DatePicker startdate;
    @javafx.fxml.FXML
    private ChoiceBox lokacja;
    @javafx.fxml.FXML
    private TextField movietitle;
    private Vector<Film> dostepneFilmy = new Vector<Film>();

    private Vector<Film> filmywzamowieniu = new Vector<Film>();
    @javafx.fxml.FXML
    private TableColumn TutulRezerw;
    @javafx.fxml.FXML
    private TableColumn cenaColumn;
    @javafx.fxml.FXML
    private TableColumn CenaRezerw;
    @javafx.fxml.FXML
    private TableColumn tytulColumn;
    @javafx.fxml.FXML
    private TableColumn rezyserColumn;
    @javafx.fxml.FXML
    private TableColumn gatunekColumn;
    @javafx.fxml.FXML
    private TableView AktualneZamowi;

    @javafx.fxml.FXML
    public void initialize() {
        lokacja.getItems().addAll(pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getLokacje());
        lokacja.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChoiceBoxSelected((Lokacja) newValue));

        tytulColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, String>("tytul"));
        rezyserColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, String>("rezyser"));
        gatunekColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, String>("gatunek"));
        cenaColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, Double>("cena"));
        TutulRezerw.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, String>("tytul"));
        CenaRezerw.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, Double>("cena"));

        ObservableList<Film> dostepnefilmyobs = javafx.collections.FXCollections.observableArrayList(dostepneFilmy);
        tabelafilmow.setItems(dostepnefilmyobs);
        ObservableList<Film> filmywzamowieniuobs = javafx.collections.FXCollections.observableArrayList(filmywzamowieniu);
        AktualneZamowi.setItems(filmywzamowieniuobs);
    }

    private void onChoiceBoxSelected(Lokacja selectedItem) {
        dostepneFilmy = pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getMoviesInLocation(selectedItem.getId(),null,null);
        ObservableList<Film> dostepnefilmyobs = javafx.collections.FXCollections.observableArrayList(dostepneFilmy);
        tabelafilmow.setItems(dostepnefilmyobs);
    }


    @javafx.fxml.FXML
    public void wynajmijclicked() {
    }

    @javafx.fxml.FXML
    public void onEnter() {

    }

    @javafx.fxml.FXML
    public void wyszukaj() {
        dostepneFilmy = pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getMoviesInLocation(((Lokacja) lokacja.getSelectionModel().getSelectedItem()).getId(), movietitle.getText(), rezyser.getText());
        ObservableList<Film> dostepnefilmyobs = javafx.collections.FXCollections.observableArrayList(dostepneFilmy);
        tabelafilmow.setItems(dostepnefilmyobs);
    }

    @javafx.fxml.FXML
    public void addtoorder() {
        Film film = (Film) tabelafilmow.getSelectionModel().getSelectedItem();
        if (film != null) {
            filmywzamowieniu.add(film);
            ObservableList<Film> filmywzamowieniuobs = javafx.collections.FXCollections.observableArrayList(filmywzamowieniu);
            AktualneZamowi.setItems(filmywzamowieniuobs);
        }
        else{
            SimpleDialog simpleDialog = new SimpleDialog("Nie wybrano filmu");
        }
    }
}