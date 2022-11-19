package controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
            "-fx-background-color: #7707ba;\n" +
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
    @FXML
    private JFXButton adaline;
    @FXML
    private JFXButton mlp;

    private Algorithm algorithm;
    @FXML
    private JFXButton submit;
    GsonHelper helper;
    private static TextArea textComponent;
    private File file;
    private Stage primaryStage;

    private JFXButton[] btns;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btns = new JFXButton[]{hebb, perceptron, mPerceptron, adaline, mlp};

        Map<String, Double> map = new HashMap<>();

        JFXButton button = new JFXButton();
        setButtonFunc(button);

        TextField field1 = createInput("Learning Rate");
        TextField field2 = createInput("Theta");
        TextField field3 = createInput("Count");
        TextField field4 = createInput("Hidden Neurones Count");
        TextField field5 = createInput("Seed");

        hebb.setOnAction(event -> {
            algorithm = Algorithm.HEBB;
            addItems(hebb, button);
        });

        perceptron.setOnAction(event -> {
            algorithm = Algorithm.PERCEPTRON;
            addItems(perceptron, button, field1, field2, field3);
        });

        mPerceptron.setOnAction(event -> {
            algorithm = Algorithm.MULTICLASSPERCEPTRON;
            addItems(mPerceptron, button, field1, field2, field3);
        });

        adaline.setOnAction(event -> {
            algorithm = Algorithm.ADALINE;
            addItems(adaline, button, field1, field3);
        });

        mlp.setOnAction(event -> {
            algorithm = Algorithm.MULTILAYERPERCEPTRON;
            addItems(mlp, button, field1, field3, field4,field5);
        });

        submit.setOnAction(event -> {
            if (file == null) {
                file = new File("mainData.json");
            }
            helper = new GsonHelper(file);

            String s1 = field1.getText().isEmpty() ? "0.1" : field1.getText();
            String s2 = field2.getText().isEmpty() ? "0" : field2.getText();
            String s3 = field3.getText().isEmpty() ? "100" : field3.getText();
            String s4 = field4.getText().isEmpty() ? "100" : field4.getText();
            String s5 = field5.getText().isEmpty() ? "97" : field5.getText();

            map.put("rate", Double.valueOf(s1));
            map.put("theta", Double.valueOf(s2));
            map.put("count", Double.valueOf(s3));
            map.put("hidden", Double.valueOf(s4));
            map.put("seed", Double.valueOf(s5));

            primaryStage.close();
            parent.getChildren().clear();
            for (JFXButton btn : btns) parent.getChildren().add(btn);
            startApplication(map);
            algorithm = null;
            checkButtons();
        });

    }

    private TextField createInput(String label) {
        TextField field = new TextField();
        field.setPromptText(label);
        field.getStyleClass().add("input");
        field.setPrefHeight(60);
        field.setAlignment(Pos.CENTER);
        field.setFont(Font.font(20));
        return field;
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

    private void addItems(JFXButton btn, Node... nodes) {
        parent.getChildren().addAll(nodes);
        for (JFXButton button : btns) {
            if (!button.equals(btn)) {
                parent.getChildren().remove(button);
            }
        }

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
            if (getSelectedAlgorithm().equals(Algorithm.HEBB)) {
                stage.setTitle("X O Recognition ,Hebb");
            } else if (getSelectedAlgorithm().equals(Algorithm.PERCEPTRON)) {
                stage.setTitle("X O Recognition ,Perceptron");
            } else if (getSelectedAlgorithm().equals(Algorithm.MULTICLASSPERCEPTRON)) {
                stage.setTitle("X O Recognition ,Multiclass Perceptron");
            } else if (getSelectedAlgorithm().equals(Algorithm.ADALINE)) {
                stage.setTitle("X O Recognition ,Adaline");
            }else if (getSelectedAlgorithm().equals(Algorithm.MULTILAYERPERCEPTRON)) {
                stage.setTitle("X O Recognition ,Multi Layer Perceptron");
            }
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
            } else if (algorithm.equals(Algorithm.ADALINE)) {
                log("Training Algorithm Is: Adaline");
                log("Algorithm Parameters:");
                log("Learning Rate: " + data.get("rate"));
                log("Train Count Is: " + data.get("count"));
            }else if (algorithm.equals(Algorithm.MULTILAYERPERCEPTRON)) {
                log("Training Algorithm Is: Multi Layer Perceptron");
                log("Algorithm Parameters:");
                log("Learning Rate: " + data.get("rate"));
                log("Hidden Neurons Count: " + data.get("hidden"));
                log("Train Count Is: " + data.get("count"));
            }
            logSep();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkButtons() {
        for (JFXButton btn : btns) btn.setStyle(BUTTON_DEFAULT_STYLE);
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
            if (algorithm.equals(Algorithm.ADALINE)) {
                adaline.setStyle(BUTTON_SELECTED_STYLE);
            }
            if (algorithm.equals(Algorithm.MULTILAYERPERCEPTRON)) {
                mlp.setStyle(BUTTON_SELECTED_STYLE);
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
        Platform.runLater(() -> textComponent.appendText(log + "\n"));

    }

    public static void logSep() {
        log("--------------");
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
