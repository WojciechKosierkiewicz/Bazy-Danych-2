package pwr.bazydanych.ui;

import javafx.fxml.FXML;

public class WorkerViewController {


    @FXML
    protected void wybranozarzadzajfilmami() {
        System.out.println("zarzadzaj filmami");
    }

    @FXML
    protected void wybranozarzadzajuzytkownikami() {
        util.switch_scene("worker-user-management.fxml");
    }

    @FXML
    public void movetoszef() {
        util.switch_scene("WidokSzefa.fxml");
    }
}
