package pwr.bazydanych.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.Rezyser;

import java.util.Vector;

public class AddMovieController
{
    @javafx.fxml.FXML
    private TextField gatunekField;
    @javafx.fxml.FXML
    private TextField tytulField;
    @javafx.fxml.FXML
    private TextField cena;
    @javafx.fxml.FXML
    private TableColumn imieColumn;
    @javafx.fxml.FXML
    private TableColumn nazwiskoColumn;
    @javafx.fxml.FXML
    private TableColumn idColumn;
    @javafx.fxml.FXML
    private TextField rezysername;
    @javafx.fxml.FXML
    private TableView tabela;
    @javafx.fxml.FXML
    private Button powrot;

    @javafx.fxml.FXML
    public void initialize() {
        imieColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("imie"));
        nazwiskoColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nazwisko"));
        idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        Vector<Rezyser> rezyserzy = AdapterBazyDanych.getInstance().getRezyserzy();
        tabela.getItems().addAll(rezyserzy);
    }

    @javafx.fxml.FXML
    public void onAddMovieButtonClick() {
        String tytul = tytulField.getText();
        String gatunek = gatunekField.getText();
        Rezyser selectedRezyser ;
        try{
            selectedRezyser = (Rezyser) tabela.getSelectionModel().getSelectedItem();
        }
        catch (Exception e){
            SimpleDialog simpleDialog = new SimpleDialog("Wybierz rezysera");
            return;
        }
        Double cenna = Double.parseDouble(cena.getText());
        if (tytul.isEmpty() ||  gatunek.isEmpty() || cenna == null) {
            SimpleDialog simpleDialog = new SimpleDialog("Wszystkie pola musza byc wypelnione");
            return;
        }
        //TODO
        System.out.println("Dodaje film: " + tytul + " " + selectedRezyser.getId() + " " + gatunek + " " + cenna);
    }

    @javafx.fxml.FXML
    public void szukajrezysera() {
        Vector<Rezyser> rezyserzy = AdapterBazyDanych.getInstance().getRezyserzyByName(rezysername.getText());
        tabela.getItems().clear();
        tabela.getItems().addAll(rezyserzy);
    }

    @javafx.fxml.FXML
    public void powrot() {
        util.switch_scene("WidokSzefa.fxml");
    }
}