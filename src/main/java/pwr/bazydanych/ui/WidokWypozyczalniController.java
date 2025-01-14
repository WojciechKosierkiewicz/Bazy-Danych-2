package pwr.bazydanych.ui;

import javafx.scene.control.ChoiceBox;
import pwr.bazydanych.bdanych.AdapterBazyDanych;
import pwr.bazydanych.bdanych.Lokacja;

public class WidokWypozyczalniController
{
    @javafx.fxml.FXML
    private ChoiceBox<Lokacja> lokacja;

    @javafx.fxml.FXML
    public void initialize() {
        lokacja.getItems().addAll(AdapterBazyDanych.getInstance().getLokacje());
    }

    @javafx.fxml.FXML
    public void onChoiceBoxSelectionChanged() {
    }
}