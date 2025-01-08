package pwr.bazydanych.ui;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;

public class SimpleDialog  {
    public SimpleDialog(String message) {
        Dialog dialog = new Dialog();
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Ok !", ButtonData.CANCEL_CLOSE));
        dialog.showAndWait();
    }
}
