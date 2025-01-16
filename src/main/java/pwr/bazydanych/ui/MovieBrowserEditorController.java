package pwr.bazydanych.ui;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.Film;

import java.util.Vector;

public class MovieBrowserEditorController
{
    @javafx.fxml.FXML
    private TextField rezyserField;
    @javafx.fxml.FXML
    private TableColumn tytulColumn;
    @javafx.fxml.FXML
    private TableColumn rezyserColumn;
    @javafx.fxml.FXML
    private ChoiceBox gatunek;
    @javafx.fxml.FXML
    private TableView tableView;
    @javafx.fxml.FXML
    private TableColumn gatunekColumn;
    @javafx.fxml.FXML
    private TextField tytulField;

    @javafx.fxml.FXML
    public void initialize() {
        gatunek.getItems().addAll(pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getGenres());
        tytulColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tytul"));
        rezyserColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rezyser"));
        gatunekColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gatunek"));
    }

    @javafx.fxml.FXML
    public void szukajclicked() {
        Vector<Film> filmyquery;
        String tytul = tytulField.getText();
        String rezyser = rezyserField.getText();
        String genre;
        try {
            genre = gatunek.getValue().toString();
        }
        catch (Exception e){
            genre = "";
        }
        System.out.println("SZUKAM TYTUL : " + tytul+ "Rezyser : " + rezyser + "Gatunek : " + genre);
        filmyquery = pwr.bazydanych.bdanych.AdapterBazyDanych.getInstance().getMoviesByArg(rezyser,tytul,genre);
        tableView.getItems().clear();
        tableView.getItems().addAll(filmyquery);
    }

    @javafx.fxml.FXML
    public void onAddButtonClick() {
    }

    @javafx.fxml.FXML
    public void usunfilmclicked() {
        Film film = (Film) tableView.getSelectionModel().getSelectedItem();
        if (film != null) {
            System.out.println("Usuwam film: " + film.tytul);
            tableView.getItems().remove(film);
        }
    }

    @javafx.fxml.FXML
    public void wrocclicked() {
        util.switch_scene("WidokSzefa.fxml");
    }
}