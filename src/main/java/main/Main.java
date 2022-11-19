package main;

import controller.AlgorithmController;
import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    public static void startApp(){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader aLoader = new FXMLLoader(new File("src/main/java/view/AlgorithmView.fxml").toURI().toURL());

        Scene aScene = new Scene(aLoader.load());
        AlgorithmController controller = aLoader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.setScene(aScene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Algorithms");
        primaryStage.show();
    }


}
