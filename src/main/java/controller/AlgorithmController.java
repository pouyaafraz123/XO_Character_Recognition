package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Algorithm;
import tools.GsonHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AlgorithmController implements Initializable {
    public static final String BUTTON_DEFAULT_STYLE =
            "-fx-background-color: #0073da;\n" +
                    "-fx-text-fill: #fff;\n" +
                    "-fx-background-radius: 250px;";
    public static final String BUTTON_SELECTED_STYLE =
            "-fx-background-color: #32002f;\n" +
                    "-fx-text-fill: #fff;\n" +
                    "-fx-background-radius: 250px;";
    @FXML
    private VBox parent;

    @FXML
    private JFXButton hebb;

    @FXML
    private JFXButton perceptron;

    @FXML
    private JFXButton mPerceptron;

    private Algorithm algorithm;
    @FXML
    private JFXButton submit;
    GsonHelper helper;
    private static TextArea textComponent;
    private File file;
    private Stage primaryStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXButton button = new JFXButton();
        setButtonFunc(button);

        TextField field1 = new TextField();
        field1.setPromptText("Learning Rate");
        field1.getStyleClass().add("input");
        field1.setPrefHeight(60);
        field1.setAlignment(Pos.CENTER);
        field1.setFont(Font.font(20));

        TextField field2 = new TextField();
        field2.setPromptText("Theta");
        field2.getStyleClass().add("input");
        field2.setPrefHeight(60);
        field2.setAlignment(Pos.CENTER);
        field2.setFont(Font.font(20));

        TextField field3 = new TextField();
        field3.setPromptText("Count");
        field3.getStyleClass().add("input");
        field3.setPrefHeight(60);
        field3.setAlignment(Pos.CENTER);
        field3.setFont(Font.font(20));

        Map<String, Double> map = new HashMap<>();
        hebb.setOnAction(event -> {
            algorithm = Algorithm.HEBB;

            parent.getChildren().add(button);
            parent.getChildren().remove(perceptron);
            parent.getChildren().remove(mPerceptron);

            checkButtons();
        });

        perceptron.setOnAction(event -> {
            algorithm = Algorithm.PERCEPTRON;

            addItems(button, field1, field2, field3, hebb, mPerceptron);
        });

        mPerceptron.setOnAction(event -> {
            algorithm = Algorithm.MULTICLASSPERCEPTRON;

            addItems(button, field1, field2, field3, perceptron, hebb);
        });

        submit.setOnAction(event -> {
            if (file == null) {
                file = new File("mainData.json");
            }
            helper = new GsonHelper(file);

            String s1 = field1.getText().isEmpty() ? "0.1" : field1.getText();
            String s2 = field2.getText().isEmpty() ? "0" : field2.getText();
            String s3 = field3.getText().isEmpty() ? "100" : field3.getText();

            map.put("rate", Double.valueOf(s1));
            map.put("theta", Double.valueOf(s2));
            map.put("count", Double.valueOf(s3));

            primaryStage.close();
            parent.getChildren().clear();
            parent.getChildren().add(hebb);
            parent.getChildren().add(perceptron);
            parent.getChildren().add(mPerceptron);
            startApplication(map);
            algorithm = null;
            checkButtons();
        });

    }

    private void createInput(TextField field,String label){

    }

    private void setButtonFunc(JFXButton button) {
        button.setPrefHeight(60);
        button.setPrefWidth(1000);
        button.setOnAction(event1 -> {
            FileDialog dialog = new FileDialog(new JFrame());
            dialog.setVisible(true);
            if (dialog.getFiles().length > 0) {
                file = dialog.getFiles()[0];
            }
        });
        button.setText("Select Dataset File");
        button.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, FontPosture.ITALIC, 20));
        button.getStyleClass().add("btn");
        VBox.setMargin(button, new Insets(25, 0, 0, 0));
    }

    private void addItems(JFXButton button, TextField field1, TextField field2, TextField field3, JFXButton hebb, JFXButton mPerceptron) {
        parent.getChildren().add(button);
        parent.getChildren().add(field1);
        parent.getChildren().add(field2);
        parent.getChildren().add(field3);
        parent.getChildren().remove(hebb);
        parent.getChildren().remove(mPerceptron);

        checkButtons();
    }

    private void startApplication(Map<String, Double> data) {
        try {
            FXMLLoader loader = new FXMLLoader(new File("src/main/java/view/MainView.fxml").toURI().toURL());

            Parent parent = loader.load();
            MainController controller = loader.getController();
            controller.setAlgorithm(getSelectedAlgorithm());
            controller.setHelperClass(getHelperClass());
            controller.setData(data);
            controller.setPrimaryStage(primaryStage);
            textComponent = controller.getArea();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("X O Recognition ,Hebb");
            stage.show();

            log("Application Started Successfully.");
            log("Dataset File: " + file.getAbsolutePath());
            logSep();

            if (algorithm.equals(Algorithm.HEBB)) {
                log("Training Algorithm Is: Hebb");
            } else if (algorithm.equals(Algorithm.PERCEPTRON)) {
                log("Training Algorithm Is: Perceptron");
                log("Algorithm Parameters:");
                log("Learning Rate: " + data.get("rate"));
                log("Theta: " + data.get("theta"));
                log("Train Count Is: " + data.get("count"));
            } else if (algorithm.equals(Algorithm.MULTICLASSPERCEPTRON)) {
                log("Training Algorithm Is: MultiClass Perceptron");
                log("Algorithm Parameters:");
                log("Learning Rate: " + data.get("rate"));
                log("Theta: " + data.get("theta"));
                log("Train Count Is: " + data.get("count"));
            }
            logSep();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkButtons() {
        hebb.setStyle(BUTTON_DEFAULT_STYLE);
        perceptron.setStyle(BUTTON_DEFAULT_STYLE);
        mPerceptron.setStyle(BUTTON_DEFAULT_STYLE);
        if (algorithm != null) {
            if (algorithm.equals(Algorithm.HEBB)) {
                hebb.setStyle(BUTTON_SELECTED_STYLE);
            }
            if (algorithm.equals(Algorithm.PERCEPTRON)) {
                perceptron.setStyle(BUTTON_SELECTED_STYLE);
            }
            if (algorithm.equals(Algorithm.MULTICLASSPERCEPTRON)) {
                mPerceptron.setStyle(BUTTON_SELECTED_STYLE);
            }
        }
    }

    public Algorithm getSelectedAlgorithm() {
        return algorithm;
    }

    public GsonHelper getHelperClass() {
        return helper;
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
            log(builder.toString());
        }

    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
