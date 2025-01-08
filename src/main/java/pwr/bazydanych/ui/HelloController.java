package pwr.bazydanych.ui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import pwr.bazydanych.HelloApplication;
import pwr.bazydanych.SharedState;
import pwr.bazydanych.bdanych.User;
import pwr.bazydanych.bdanych.AdapterBazyDanych;

import java.io.IOException;

public class HelloController {
    @FXML
    private TextField nameField;

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void switch_scene(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene scene = new Scene(fxmlLoader.load());
            SharedState.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onHelloButtonClick() {
        if (nameField.getText().isBlank()) {
            new SimpleDialog("Please enter your name.");
        } else {
            if (isNumeric(nameField.getText())) {
                AdapterBazyDanych adapter = AdapterBazyDanych.getInstance();
                User user = adapter.getUser(nameField.getText());
                if (user != null) {
                    new SimpleDialog("Hello, " + user.imie + " " + user.nazwisko + "!");
                    SharedState.username = user.imie + " " + user.nazwisko;
                    switch_scene("user-view.fxml");
                } else {
                    new SimpleDialog("Nie znaleziono użytkownika o podanym id.");
                }
            } else {
                new SimpleDialog("Please enter a valid name.");
            }
        }
    }
}