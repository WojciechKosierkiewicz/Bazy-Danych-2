package pwr.bazydanych.ui;

import com.sun.source.tree.TryTree;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import pwr.bazydanych.HelloApplication;
import pwr.bazydanych.SharedState;
import pwr.bazydanych.WorkerApplication;

import java.io.IOException;
import java.net.URL;


public class util {
    private static String getMainClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // The main class will usually be at the last element of the stack trace
        // (excluding the system call to start the JVM)
        for (StackTraceElement element : stackTrace) {
            if ("main".equals(element.getMethodName())) {
                return element.getClassName();
            }
        }

        return "Unknown";
    }
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static void switch_scene(String fxml) {
        try {
            FXMLLoader fxmlLoader;
            if(SharedState.FrontName.equals("UserAPP")) {
                fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            } else if (SharedState.FrontName.equals("WorkerAPP")) {
                fxmlLoader = new FXMLLoader(WorkerApplication.class.getResource(fxml));
            } else {
                System.out.println("Nie ustawiony appname");
                return;
            }
            Scene scene = new Scene(fxmlLoader.load());
            SharedState.stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

