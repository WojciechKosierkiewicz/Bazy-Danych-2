package pwr.bazydanych.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.User;

public class WorkerUserManagementController {
    User user;
    @FXML
    public TextField useridtextfield;

    @FXML
    public Button returnmovies;

    @FXML
    public void onZalogujCliked(MouseEvent mouseEvent) {
        AdapterBazyDanych adb = AdapterBazyDanych.getInstance();
        user = adb.getUser(useridtextfield.getText());
        if (user == null){
            SimpleDialog err = new SimpleDialog("Nie znaleziono użytkownika o podanym id.");
            return;
        }
        returnmovies.setDisable(false);
    }
}
