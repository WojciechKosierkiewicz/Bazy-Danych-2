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

    private void refresh_table(){
        Lokacja selectedItem = lokacja.getSelectionModel().getSelectedItem();
        Vector<Film> filmy = AdapterBazyDanych.getInstance().getLocationsMovies(selectedItem.getId());
        ObservableList<Film> filmyobs = javafx.collections.FXCollections.observableArrayList(filmy);
        tabelafilm.setItems(filmyobs);
    }

    @javafx.fxml.FXML
    public void onChoiceBoxSelectionChanged() {
    }

    @javafx.fxml.FXML
    public void zmiencene() {
        Film wybrane = (Film) tabelafilm.getSelectionModel().getSelectedItem();
        if(wybrane == null){
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz film");
        }
        try {
            if (AdapterBazyDanych.getInstance().changePrice(wybrane.getId(), Double.parseDouble(cenafield.getText()))) {
                SimpleDialog simpleDialog = new SimpleDialog("Cena zmieniona");
            }
            else {
                SimpleDialog simpleDialog = new SimpleDialog("Nie mozna zmienic ceny");
            }
        }
        catch (Exception e){
            SimpleDialog simpleDialog = new SimpleDialog(e.getMessage());
        }
        refresh_table();
    }

    @javafx.fxml.FXML
    public void zmienilosc() {
        Film wybrane = (Film) tabelafilm.getSelectionModel().getSelectedItem();
        Lokacja wybranaLokacja = lokacja.getSelectionModel().getSelectedItem();
        if (wybranaLokacja == null) {
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz lokacje");
            return;
        }
        if(wybrane == null){
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz film");
        }
        try {
            if (AdapterBazyDanych.getInstance().changeMovieAvailability(wybrane.getId(),wybranaLokacja.getId(), Integer.parseInt(iloscfield.getText()))) {
                SimpleDialog simpleDialog = new SimpleDialog("Ilosc zmieniona");
            }
            else {
                SimpleDialog simpleDialog = new SimpleDialog("Nie mozna zmienic ilosci");
            }
        }
        catch (Exception e){
            SimpleDialog simpleDialog = new SimpleDialog(e.getMessage());
        }
        refresh_table();
    }
}