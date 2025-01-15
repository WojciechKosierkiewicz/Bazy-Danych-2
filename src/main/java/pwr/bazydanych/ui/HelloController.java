package pwr.bazydanych.ui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pwr.bazydanych.SharedState;
import pwr.bazydanych.bdanych.User;
import pwr.bazydanych.bdanych.AdapterBazyDanych;



public class HelloController {
    @FXML
    public TextField nameField;

    @FXML
    protected void onHelloButtonClick() {
        if (nameField.getText().isBlank()) {
            new SimpleDialog("Please enter your name.");
        } else {
            if (util.isNumeric(nameField.getText())) {
                AdapterBazyDanych adapter = AdapterBazyDanych.getInstance();
                User user = adapter.getUser(nameField.getText());
                if (user != null) {
                    new SimpleDialog("Hello, " + user.imie + " " + user.nazwisko + "!");
                    SharedState.username = user.id;
                    util.switch_scene("user-view.fxml");
                } else {
                    new SimpleDialog("Nie znaleziono użytkownika o podanym id.");
                }
            } else {
                new SimpleDialog("Please enter a valid name.");
            }
        }
    }

    @FXML
    public void registeriser() {
        util.switch_scene("rejestracja.fxml");
    }
}