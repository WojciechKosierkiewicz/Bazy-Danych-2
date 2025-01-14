package pwr.bazydanych.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.User;
import pwr.bazydanych.bdanych.WynajetyFilm;

import java.util.Vector;

public class WorkerUserManagementController {
    private User user;

    //FXML declarations
    @FXML
    public TableView<WynajetyFilm> tabelawynajetych;
    @FXML
    public TextField useridtextfield;
    @FXML
    public Button returnmovies;

    @FXML
    private TableColumn<WynajetyFilm, String> tytulColumn;
    @FXML
    private TableColumn<WynajetyFilm, String> dataWynajmuColumn;
    @FXML
    private TableColumn<WynajetyFilm, Double> kosztColumn;



    @FXML
    public void initialize() {
        returnmovies.setDisable(true);
        tabelawynajetych.setVisible(false);
    }


    private void AfterUserFoundOperations() {
        returnmovies.setDisable(false);
        tabelawynajetych.setVisible(true);
        AdapterBazyDanych adb = AdapterBazyDanych.getInstance();
        Vector<WynajetyFilm> wynajete = adb.getFilmyWynajeteBy(user.id);
        ObservableList<WynajetyFilm> wynajetelist = FXCollections.observableArrayList(wynajete);
        tytulColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("Tytul"));
        dataWynajmuColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dataWypozyczenia"));
        kosztColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("aktualnykoszt"));
        tabelawynajetych.setItems(wynajetelist);
    }

    private void AfterUserNotFoundOperations() {
        returnmovies.setDisable(true);
        tabelawynajetych.setVisible(false);
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
}
