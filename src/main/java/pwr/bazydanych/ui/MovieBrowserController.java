package pwr.bazydanych.ui;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.Film;

import java.util.Formattable;
import java.util.Vector;


public class MovieBrowserController
{
    @javafx.fxml.FXML
    private TextField rezyserField;
    @javafx.fxml.FXML
    private TableColumn tytulColumn;
    @javafx.fxml.FXML
    private TableColumn rezyserColumn;
    @javafx.fxml.FXML
    private TableView tableView;
    @javafx.fxml.FXML
    private TableColumn gatunekColumn;
    @javafx.fxml.FXML
    private TextField tytulField;
    @javafx.fxml.FXML
    private ChoiceBox gatunek = new ChoiceBox<>();

    Vector<Film> filmyquery;

    @javafx.fxml.FXML
    public void initialize() {
        gatunek.getItems().addAll(AdapterBazyDanych.getInstance().getGenres());
        tytulColumn.setCellValueFactory(new PropertyValueFactory<>("tytul"));
        rezyserColumn.setCellValueFactory(new PropertyValueFactory<>("rezyser"));
        gatunekColumn.setCellValueFactory(new PropertyValueFactory<>("gatunek"));
    }

    @javafx.fxml.FXML
    public void onAddButtonClick() {
        String tytul = tytulField.getText();
        String rezyser = rezyserField.getText();
        String genre;
        if (gatunek.getValue() != null) {
            genre = gatunek.getValue().toString();
        } else {
            genre = null; // Jeśli nie wybrano wartości, ustaw null.
        }
        System.out.println("SZUKAM TYTUL : " + tytul+ "Rezyser : " + rezyser + "Gatunek : " + genre);
        filmyquery = AdapterBazyDanych.getInstance().getMoviesByArg(rezyser,tytul,genre);
        for(Film x : filmyquery) {
            System.out.println(x.tytul);
        }
        tableView.getItems().clear();
        tableView.getItems().addAll(filmyquery);
    }

    @javafx.fxml.FXML
    public void goback() {
        util.switch_scene("user-view.fxml");
    }
}