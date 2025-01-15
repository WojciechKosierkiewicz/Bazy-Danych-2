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
import pwr.bazydanych.bdanych.Zamowienie;

import java.util.Vector;

public class UserViewController {
    @FXML
    private TableColumn cenaColumn;
    @FXML
    private TableColumn tytulColumn;
    @FXML
    private TableView zamowienia;
    @FXML
    private TableColumn Id_Zamowienia;
    @FXML
    private TableView filmy;
    @FXML
    private TableColumn kosztColumn;
    @FXML
    private TableColumn dataWypozyczeniaColumn;

    @FXML
    public void initialize() {
        AdapterBazyDanych adapter = AdapterBazyDanych.getInstance();
        Vector<Zamowienie> zamowieniaVec = adapter.getZamowienia(SharedState.username);
        ObservableList<Zamowienie> zamowieniaObservable = FXCollections.observableArrayList(zamowieniaVec);
        Id_Zamowienia.setCellValueFactory(new PropertyValueFactory<>("ID_Zamowienia"));
        dataWypozyczeniaColumn.setCellValueFactory(new PropertyValueFactory<>("dataWypozyczenia"));
        kosztColumn.setCellValueFactory(new PropertyValueFactory<>("koszt"));
        zamowienia.setItems(zamowieniaObservable);
        zamowienia.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onZamowienieSelected((Zamowienie) newValue));
    }

    @Deprecated
    private void onZamowienieSelected(Zamowienie selectedZamowienie){
        Vector<WynajetyFilm> filmyVec = AdapterBazyDanych.getInstance().getWynajeteFilmy(selectedZamowienie.getID_Zamowienia());
        ObservableList<WynajetyFilm> filmyObservable = FXCollections.observableArrayList(filmyVec);
        tytulColumn.setCellValueFactory(new PropertyValueFactory<>("Tytul"));
        cenaColumn.setCellValueFactory(new PropertyValueFactory<>("aktualnykoszt"));
        filmy.setItems(filmyObservable);
    }

    @FXML
    public void browsemoviescliked() {
        util.switch_scene("MovieBrowser.fxml");
    }
}
