package main;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private static TextArea textComponent;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(new File("src/main/java/view/MainView.fxml").toURI().toURL());
        Parent parent = loader.load();
        MainController controller = loader.getController();
        textComponent = controller.getArea();
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.setTitle("X O Recognition ,Hebb");
        primaryStage.show();
        Main.log("Application Started Successfully.");
        logSep();
    }

    public static void log(String log) {
        textComponent.appendText(log + "\n");
    }

    public static void logSep() {
        textComponent.appendText("--------------" + "\n");
    }

    public static void logMatrix(int[] data) {
        for (int i = 0; i < 5; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                builder.append(data[i * 5 + j] == 1 ? "#" : ".").append("\t");
            }
            Main.log(builder.toString());
        }
    }
}
