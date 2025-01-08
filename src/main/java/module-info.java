module pwr.bazydanych {
    requires javafx.controls;
    requires javafx.fxml;


    opens pwr.bazydanych to javafx.fxml;
    exports pwr.bazydanych;
    exports pwr.bazydanych.bdanych;
    opens pwr.bazydanych.bdanych to javafx.fxml;
    exports pwr.bazydanych.ui;
    opens pwr.bazydanych.ui to javafx.fxml;
}