package pwr.bazydanych.ui;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;


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
    private ChoiceBox gatunek;

    @javafx.fxml.FXML
    public void initialize() {
        gatunek.getItems().addAll(AdapterBazyDanych.getInstance().getGatunki());
    }

    @javafx.fxml.FXML
    public void onAddButtonClick() {
    }
}