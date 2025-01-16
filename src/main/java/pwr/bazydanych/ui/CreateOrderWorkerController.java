package pwr.bazydanych.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pwr.bazydanych.bdanych.Film;
import pwr.bazydanych.bdanych.Lokacja;

import java.util.Vector;

public class CreateOrderWorkerController
{
    @javafx.fxml.FXML
    private DatePicker datazakonczeniaui;
    @javafx.fxml.FXML
    private TableColumn cenaColumndostepnosc;
    @javafx.fxml.FXML
    private TableView tabeladostepnosci;
    @javafx.fxml.FXML
    private TableColumn cenaColumnwybrane;
    @javafx.fxml.FXML
    private TableColumn tytulColumndostepnosc;
    @javafx.fxml.FXML
    private TableView Tabelazbieracz;
    @javafx.fxml.FXML
    private TableColumn tytulColumnwybrane;

    private Vector<Film> wybrane_filmy = new Vector<Film>();
    @javafx.fxml.FXML
    private ChoiceBox lokacja;

    @javafx.fxml.FXML
    public void initialize() {
        cenaColumndostepnosc.setCellValueFactory(new PropertyValueFactory<>("cena"));
        tytulColumndostepnosc.setCellValueFactory(new PropertyValueFactory<>("tytul"));
        cenaColumnwybrane.setCellValueFactory(new PropertyValueFactory<>("cena"));
        tytulColumnwybrane.setCellValueFactory(new PropertyValueFactory<>("tytul"));


        lokacja.getItems().addAll(pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getLokacje());
        lokacja.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChoiceBoxSelected((pwr.bazydanych.bdanych.Lokacja) newValue));
    }

    public void onChoiceBoxSelected(Lokacja newValue) {
        Vector<Film> filmy = pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getLocationsMovies(newValue.getId());
        ObservableList<Film> filmyobs = javafx.collections.FXCollections.observableArrayList(filmy);
        tabeladostepnosci.getItems().clear();
        tabeladostepnosci.getItems().addAll(filmyobs);
    }
    @javafx.fxml.FXML
    public void rent_clicked() {
    }

    @javafx.fxml.FXML
    public void wroc_clicked() {
        util.switch_scene("worker-user-management.fxml");
    }

    @javafx.fxml.FXML
    public void usun_clicked() {
        Film wybranyfilm = (Film) Tabelazbieracz.getSelectionModel().getSelectedItem();
        if (wybranyfilm != null) {
            wybrane_filmy.remove(wybranyfilm);
            ObservableList<Film> filmyobs = javafx.collections.FXCollections.observableArrayList(wybrane_filmy);
            Tabelazbieracz.getItems().clear();
            Tabelazbieracz.getItems().addAll(filmyobs);
        }
        else {
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz film");
        }
    }

    @javafx.fxml.FXML
    public void dodaj_clicked() {
        Film wybranyfilm = (Film) tabeladostepnosci.getSelectionModel().getSelectedItem();
        if (wybranyfilm != null) {
            wybrane_filmy.add(wybranyfilm);
            ObservableList<Film> filmyobs = javafx.collections.FXCollections.observableArrayList(wybrane_filmy);
            Tabelazbieracz.getItems().clear();
            Tabelazbieracz.getItems().addAll(filmyobs);
        }
        else {
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz film");
        }
    }
}