package pwr.bazydanych.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import pwr.bazydanych.bdanych.*;

import java.util.Vector;

public class WorkerUserManagementController {
    private User user;

    @FXML
    public TextField useridtextfield;
    @FXML
    public Button returnmovies;

    @FXML
    private TableColumn<WynajetyFilm, String> tytulColumn;
    @FXML
    private TableColumn<WynajetyFilm, Double> kosztColumn;
    @FXML
    private TableColumn cenaColumn;
    @FXML
    private TableView zamowienia;
    @FXML
    private TableColumn dataWypozyczeniaColumn;
    @FXML
    private TableColumn dataZwrotuColumn;
    @FXML
    private Label imie;
    @FXML
    private CheckBox UserVerificated;
    @FXML
    private TableColumn Id_Zamowienia;
    @FXML
    private TableView filmy;
    @FXML
    private Label Nazwisko;
    @FXML
    private Label nrDowodu;


    @FXML
    public void initialize() {
        returnmovies.setDisable(true);
        zamowienia.setVisible(false);
        filmy.setVisible(false);
        UserVerificated.setVisible(false);
    }


    private void AfterUserFoundOperations() {
        returnmovies.setDisable(false);
        zamowienia.setVisible(true);
        filmy.setVisible(true);
        UserVerificated.setVisible(true);
        imie.setText(user.imie);
        Nazwisko.setText(user.nazwisko);
        nrDowodu.setText(user.nrDowodu);
        AdapterBazyDanych adb = AdapterBazyDanych.getInstance();
        Vector<Zamowienie> zamowieniavec = adb.getZamowienia(user.id);
        System.out.println(user.id + " " + zamowieniavec.size());
        ObservableList<Zamowienie> zamowienialist = FXCollections.observableArrayList(zamowieniavec);
        Id_Zamowienia.setCellValueFactory(new PropertyValueFactory<>("ID_Zamowienia"));
        dataWypozyczeniaColumn.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));
        dataZwrotuColumn.setCellValueFactory(new PropertyValueFactory<>("dataZwrotu"));
        kosztColumn.setCellValueFactory(new PropertyValueFactory<>("koszt"));
        zamowienia.setItems(zamowienialist);
        zamowienia.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onZamowienieSelected((Zamowienie) newValue));
    }

    private void AfterUserNotFoundOperations() {
        returnmovies.setDisable(true);
        zamowienia.setVisible(false);
        filmy.setVisible(false);
        UserVerificated.setVisible(false);
    }


    @FXML
    public void onZalogujCliked(MouseEvent mouseEvent) {
        AdapterBazyDanych adb = AdapterBazyDanych.getInstance();
        user = adb.getUser(useridtextfield.getText());
        if (user == null) {
            SimpleDialog err = new SimpleDialog("Nie znaleziono użytkownika o podanym id.");
            AfterUserNotFoundOperations();
            return;
        }
        AfterUserFoundOperations();
    }

    @FXML
    public void wyporzczylaniawiev() {
        util.switch_scene("WidokWypozyczalni.fxml");
    }

    @Deprecated
    private void onZamowienieSelected(Zamowienie selectedZamowienie) {
        if (selectedZamowienie != null) {
            Vector<WynajetyFilm> wynajete = AdapterBazyDanych.getInstance().getWynajeteFilmy(selectedZamowienie.ID_Zamowienia);
            ObservableList<WynajetyFilm> wynajetelist = FXCollections.observableArrayList(wynajete);
            tytulColumn.setCellValueFactory(new PropertyValueFactory<>("Tytul"));
            cenaColumn.setCellValueFactory(new PropertyValueFactory<>("aktualnykoszt"));
            filmy.setItems(wynajetelist);
        }
    }


    @FXML
    public void returnmovie() {
        //TODO
        Zamowienie selectedZamowienie = (Zamowienie) zamowienia.getSelectionModel().getSelectedItem();
        if (selectedZamowienie == null) {
            SimpleDialog err = new SimpleDialog("Wybierz zamowienie");
            return;
        }
        //removing from table
        ObservableList<WynajetyFilm> allItems = zamowienia.getItems();
        allItems.remove(selectedZamowienie);
        zamowienia.setItems(allItems);
    }
}
