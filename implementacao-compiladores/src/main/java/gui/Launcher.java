package gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher {
    // This is a workaround required for creating JavaFX apps in a single archive (.JAR)
    public static void main(String[] args) {
        App.main(args);
    }
}
