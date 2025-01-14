package pwr.bazydanych.ui;

import javafx.scene.control.TextField;
import pwr.bazydanych.bdanych.AdapterBazyDanych;

public class AddDirectorController
{
    @javafx.fxml.FXML
    private TextField nazwiskoField;
    @javafx.fxml.FXML
    private TextField imieField;

    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void onAddDirectorButtonClick() {
        if (nazwiskoField.getText().isBlank() || imieField.getText().isBlank()) {
            new SimpleDialog("Podaj imie i nazwisko rezysera.");
        } else {
            AdapterBazyDanych adapter = AdapterBazyDanych.getInstance();
            if (adapter.addDirector(imieField.getText(), nazwiskoField.getText())) {
                new SimpleDialog("Dodano nowego rezysera.");
            }else {
                new SimpleDialog("Nie udalo sie dodac rezysera.");
            }
        }
    }

    @javafx.fxml.FXML
    public void switchbackclicked() {
        util.switch_scene("WidokSzefa.fxml");
    }
}