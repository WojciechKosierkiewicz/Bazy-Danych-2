package pwr.bazydanych.ui;

import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;

public class AddMovieController
{
    @javafx.fxml.FXML
    private TextField rezyserField;
    @javafx.fxml.FXML
    private TextField gatunekField;
    @javafx.fxml.FXML
    private TextField tytulField;
    @javafx.fxml.FXML
    private TextField cena;

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void onAddMovieButtonClick() {
        String tytul = tytulField.getText();
        String rezyser = rezyserField.getText();
        String gatunek = gatunekField.getText();
        Double cenna = Double.parseDouble(cena.getText());
        if (tytul.isEmpty() || rezyser.isEmpty() || gatunek.isEmpty() || cenna == null) {
            SimpleDialog simpleDialog = new SimpleDialog("Wszystkie pola musza byc wypelnione");
            return;
        }
        //TODO
        System.out.println("Dodaje film: " + tytul + " " + rezyser + " " + gatunek + " " + cenna);
    }
}