package pwr.bazydanych.ui;

public class WidokSzefaController
{
    @javafx.fxml.FXML
    public void initialize() {
    }

    @javafx.fxml.FXML
    public void usunfilmpressed() {
        util.switch_scene("MovieBrowserEditor.fxml");
    }

    @javafx.fxml.FXML
    public void dodajfilmpressed() {
        util.switch_scene("AddMovie.fxml");
    }

    @javafx.fxml.FXML
    public void changeiloscpressed() {
    }
}