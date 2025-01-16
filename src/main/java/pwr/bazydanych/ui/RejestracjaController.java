package pwr.bazydanych.ui;

import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;

public class RejestracjaController
{
    @javafx.fxml.FXML
    private TextField imietxt;
    @javafx.fxml.FXML
    private TextField nazwiskotxt;
    @javafx.fxml.FXML
    private TextField idnumber;

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void back() {
        util.switch_scene("hello-view.fxml");
    }

    @javafx.fxml.FXML
    public void rejestrujclicked() {
        if (imietxt.getText()==null){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj imie");
            return;
        }
        if (imietxt.getText() == ""){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj imie");
            return;
        }
        if (nazwiskotxt.getText()==null){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj nazwisko");
            return;
        }
        if (nazwiskotxt.getText() == ""){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj nazwisko");
            return;
        }
        if (idnumber.getText()==null){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj numer dowodu");
            return;
        }
        if (idnumber.getText() == ""){
            SimpleDialog simpleDialog = new SimpleDialog("Podaj numer dowodu");
            return;
        }
        if (AdapterBazyDanych.getInstance().rejestruj(imietxt.getText(), nazwiskotxt.getText(), idnumber.getText())){
            SimpleDialog simpleDialog = new SimpleDialog("Zarejestrowano");
       }
        else {
            SimpleDialog simpleDialog = new SimpleDialog("Nie mozna zarejestrowac");
        }
    }
}