package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = fxmlLoader.load();
//        root.getStylesheets().add("/main.css"); TODO não tem nada nesse arquivo
        primaryStage.setTitle("Compilador");
//        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/toolbox-48.png"))); // acho melhor deixar sem o ícone
        primaryStage.setScene(new Scene(root));

        Controller controller = fxmlLoader.getController();
        controller.setStage(primaryStage);

        primaryStage.show();
    }
}