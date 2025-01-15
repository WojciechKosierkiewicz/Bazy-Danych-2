package pwr.bazydanych.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.Film;
import pwr.bazydanych.bdanych.Lokacja;
import javafx.scene.control.TableColumn;
import java.util.Vector;

public class WidokWypozyczalniController
{
    @javafx.fxml.FXML
    private ChoiceBox<Lokacja> lokacja;
    @javafx.fxml.FXML
    private TableView tabelafilm;
    @javafx.fxml.FXML
    private TableColumn ilosccolumn;
    @javafx.fxml.FXML
    private TableColumn cenacolumn;
    @javafx.fxml.FXML
    private TableColumn tytulcolumn;
    @javafx.fxml.FXML
    private TextField iloscfield;
    @javafx.fxml.FXML
    private TextField cenafield;

    @javafx.fxml.FXML
    public void initialize() {
        lokacja.getItems().addAll(AdapterBazyDanych.getInstance().getLokacje());
        lokacja.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onChoiceBoxSelectionChanged(newValue));

        tytulcolumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, String>("tytul"));
        ilosccolumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, Integer>("Ilosc"));
        cenacolumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<Film, Double>("cena"));
        tabelafilm.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTableRowSelected((Film) newValue));
    }

    @Deprecated
    private void onTableRowSelected(Film selectedFilm) {
        if (selectedFilm != null) {
            iloscfield.setText(String.valueOf(selectedFilm.getIlosc()));
            cenafield.setText(String.valueOf(selectedFilm.getCena()));
        }
    }



    @javafx.fxml.FXML
    private void onChoiceBoxSelectionChanged(Lokacja newValue) {
        Vector<Film> filmy = AdapterBazyDanych.getInstance().getLocationsMovies(newValue.getId());
        ObservableList<Film> filmyobs = javafx.collections.FXCollections.observableArrayList(filmy);
        tabelafilm.setItems(filmyobs);
    }

    @javafx.fxml.FXML
    public void onChoiceBoxSelectionChanged() {
    }
}