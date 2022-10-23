import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(new File("src/main/java/view/MainView.fxml").toURI().toURL());
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
