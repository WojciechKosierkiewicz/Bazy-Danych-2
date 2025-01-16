package pwr.bazydanych.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pwr.bazydanych.SharedState;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.Rezerwacja;

import java.util.Vector;

public class UsersReservations
{
    @javafx.fxml.FXML
    private TableColumn lokcol;
    @javafx.fxml.FXML
    private TableView tabela;
    @javafx.fxml.FXML
    private TableColumn idcol;
    @javafx.fxml.FXML
    private TableColumn datazakonczeniacol;
    @javafx.fxml.FXML
    private TableColumn tytulcol;
    @javafx.fxml.FXML
    private TableColumn cenacol;
    @javafx.fxml.FXML
    private TableColumn datarozpoczeciacol;

    @javafx.fxml.FXML
    public void initialize() {
        Vector<Rezerwacja> rezerwacjevec = AdapterBazyDanych.getInstance().getReservations(SharedState.username);
        lokcol.setCellValueFactory(new PropertyValueFactory<>("nazwaLokacji"));
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        datazakonczeniacol.setCellValueFactory(new PropertyValueFactory<>("data_zakonczenia"));
        tytulcol.setCellValueFactory(new PropertyValueFactory<>("tytul"));
        cenacol.setCellValueFactory(new PropertyValueFactory<>("cena"));
        datarozpoczeciacol.setCellValueFactory(new PropertyValueFactory<>("data_rozpoczecia"));
        ObservableList<Rezerwacja> rezerwacjeobs = javafx.collections.FXCollections.observableArrayList(rezerwacjevec);
        tabela.setItems(rezerwacjeobs);
    }

    @javafx.fxml.FXML
    public void backpressed() {
        util.switch_scene("user-view.fxml");
    }

    @javafx.fxml.FXML
    public void cancelreservation() {
        Rezerwacja selected_rezerwacja = (Rezerwacja) tabela.getSelectionModel().getSelectedItem();
        if(selected_rezerwacja ==null ){
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz rezerwacje");
            return;
        }
        System.out.println(selected_rezerwacja.getId());
        if (AdapterBazyDanych.getInstance().closeReservation(selected_rezerwacja.getId())){
            SimpleDialog simpleDialog = new SimpleDialog("Rezerwacja anulowana");
        }
        else {
            SimpleDialog simpleDialog = new SimpleDialog("Nie mozna anulowac rezerwacji");
        }
        initialize();
    }
}