package controller;

import algorithm.Hebb;
import algorithm.MultiClassPerceptron;
import algorithm.Perceptron;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Algorithm;
import model.Data;
import tools.GsonHelper;

import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController implements Initializable {
    public static final int SIZE = 5;
    public static final String BUTTON_DEFAULT_STYLE =
            "-fx-border-color: #00edff;" +
                    " -fx-border-radius: 5px;" +
                    "-fx-border-width: 2px;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;";

    public static final String BUTTON_SELECTED_STYLE =
            "-fx-border-color: #00edff;" +
                    " -fx-border-radius: 5px;" +
                    "-fx-border-width: 2px;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;" +
                    "-fx-background-color: rgba(5,140,213,0.79)";

    @FXML
    private Text text;

    @FXML
    private AnchorPane board;

    @FXML
    private JFXButton add;

    @FXML
    private JFXButton train;

    @FXML
    private JFXButton predict;

    @FXML
    private JFXButton random;

    @FXML
    private JFXButton clear;

    @FXML
    private JFXButton x;

    @FXML
    private JFXButton o;

    @FXML
    private JFXTextArea area;
    @FXML
    private JFXButton change;

    private Hebb hebb;
    private Perceptron perceptron;
    private MultiClassPerceptron multiClassPerceptron;
    private GsonHelper helper;
    private Algorithm algorithm;
    private Map<String, Double> data;
    private Stage primaryStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        area
                .textProperty()
                .addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> area.setScrollTop(Double.MAX_VALUE));
        AtomicBoolean addMode = new AtomicBoolean(false);
        int[] matrix = new int[25];
        GridPane pane = new GridPane();
        StackPane centerPane = new StackPane(pane);

        Arrays.fill(matrix, -1);

        pane.setAlignment(Pos.CENTER);
        pane.setHgap(15);
        pane.setVgap(15);

        AnchorPane.setBottomAnchor(centerPane, 0.0);
        AnchorPane.setLeftAnchor(centerPane, 0.0);
        AnchorPane.setRightAnchor(centerPane, 0.0);
        AnchorPane.setTopAnchor(centerPane, 0.0);

        drawButtons(matrix, pane, centerPane);

        board.getChildren().add(centerPane);

        train.setOnAction(event -> {
            this.helper.previewBuilder();
            AlgorithmController.log("Start Reading Data...");
            Data[] trainSet = helper.readData().toArray(new Data[0]);
            AlgorithmController.log("Reading Data Finished.");
            AlgorithmController.log("Start Training For " + trainSet.length + " Data.");

                long startTime = System.currentTimeMillis();
                if (algorithm.equals(Algorithm.HEBB)) {
                    hebb = new Hebb(trainSet);
                    hebb.train();
                } else if (algorithm.equals(Algorithm.PERCEPTRON)) {
                    perceptron = new Perceptron(trainSet, data.get("theta"), data.get("rate"));
                    perceptron.train((int) data.get("count").doubleValue());
                } else if (algorithm.equals(Algorithm.MULTICLASSPERCEPTRON)) {
                    multiClassPerceptron = new MultiClassPerceptron(trainSet, data.get("theta"), data.get("rate"));
                    multiClassPerceptron.train((int) data.get("count").doubleValue());
                }

                AlgorithmController.log("Train Time: " + (System.currentTimeMillis() - startTime) + " Milli Seconds.");
                AlgorithmController.logSep();

                    predict.setDisable(false);

                    text.setText("Select An Option:");


        });

        predict.setOnAction(event -> {
            int result = 0;
            if (algorithm.equals(Algorithm.HEBB)) {
                result = hebb.predict(matrix) ? 1 : -1;
            } else if (algorithm.equals(Algorithm.PERCEPTRON)) {
                result = perceptron.predict(matrix, true);
            } else if (algorithm.equals(Algorithm.MULTICLASSPERCEPTRON)) {
                result = multiClassPerceptron.predict(matrix);
            }
            if (result == 1) {
                text.setText("The Predict Character Is: X");
                AlgorithmController.log("X Predicted.");
            } else if (result == -1) {
                text.setText("The Predict Character Is: O");
                AlgorithmController.log("O Predicted.");
            } else {
                text.setText("The Predict Character Is: UNKNOWN");
                AlgorithmController.log("Unable To Predict.");
            }
            AlgorithmController.logSep();
        });

        random.setOnAction(event -> generateRandomData(matrix, pane, centerPane));
        clear.setOnAction(event -> clear(matrix, pane, centerPane));

        add.setOnAction(event -> {
            text.setText("Please Specify Label:");
            if (!addMode.get()) {
                addMode.set(true);
                add.setStyle("-fx-background-color: #32002f");
                x.setDisable(false);
                o.setDisable(false);
                AlgorithmController.log("Add Mode Enabled.");
            } else {
                add.setStyle("-fx-background-color: #0073da");
                addMode.set(false);
                x.setDisable(true);
                o.setDisable(true);
                AlgorithmController.log("Add Mode Disabled.");
            }
        });

        change.setOnAction(event -> {
            change.getScene().getWindow().hide();
            primaryStage.show();
        });

        x.setOnAction(event -> {
            if (addMode.get()) {
                Data data = new Data("X", matrix);
                helper.writeData(data);
                AlgorithmController.log("Data");
                AlgorithmController.logMatrix(matrix);
                AlgorithmController.log("Added To Train Set As: X");
                AlgorithmController.logSep();
                text.setText("Select An Option:");

                add.setStyle("-fx-background-color: #0073da");
                addMode.set(false);
                x.setDisable(true);
                o.setDisable(true);
                clear(matrix, pane, centerPane);
            }
        });

        o.setOnAction(event -> {
            if (addMode.get()) {
                Data data = new Data("O", matrix);
                helper.writeData(data);

                AlgorithmController.log("Data");
                AlgorithmController.logMatrix(matrix);
                AlgorithmController.log("Added To Train Set As: O");
                AlgorithmController.logSep();
                text.setText("Select An Option:");

                add.setStyle("-fx-background-color: #0073da");
                addMode.set(false);
                x.setDisable(true);
                o.setDisable(true);
                clear(matrix, pane, centerPane);
            }
        });

    }

    private void clear(int[] matrix, GridPane pane, StackPane centerPane) {
        Arrays.fill(matrix, -1);
        drawButtons(matrix, pane, centerPane);
        text.setText("Select An Option:");
        AlgorithmController.log("Board Cleared.");
        AlgorithmController.logSep();
    }

    private void drawButtons(int[] matrix, GridPane pane, StackPane centerPane) {
        pane.getChildren().clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JFXButton button = new JFXButton(i + "-" + j);
                button.setFocusTraversable(false);
                button.setRipplerFill(Color.rgb(23, 150, 200));
                button.setCursor(Cursor.HAND);
                int buttonIndex = j * SIZE + i;
                if (matrix[buttonIndex] == 1) {
                    button.setStyle(BUTTON_SELECTED_STYLE);
                } else {
                    button.setStyle(BUTTON_DEFAULT_STYLE);
                }
                button.setOnAction(event -> {
                    matrix[buttonIndex] = matrix[buttonIndex] == -1 ? 1 : -1;
                    if (matrix[buttonIndex] == 1) {
                        button.setStyle(BUTTON_SELECTED_STYLE);
                    } else {
                        button.setStyle(BUTTON_DEFAULT_STYLE);
                    }
                });
                button.setPrefWidth(1000);
                button.setPrefHeight(1000);
                button.prefWidthProperty().bind(Bindings.min(centerPane.widthProperty().divide(SIZE),
                        centerPane.heightProperty().divide(SIZE)));
                button.prefHeightProperty().bind(Bindings.min(centerPane.widthProperty().divide(SIZE),
                        centerPane.heightProperty().divide(SIZE)));
                pane.add(button, i, j);
            }
        }
    }

    private void generateRandomData(int[] matrix, GridPane pane, StackPane centerPane) {
        AlgorithmController.log("Start Generating Random Data:");
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = random.nextInt() % 2 == 0 ? 1 : -1;
        }
        AlgorithmController.log("The Data Is Created:");
        AlgorithmController.logMatrix(matrix);
        AlgorithmController.logSep();
        drawButtons(matrix, pane, centerPane);
    }

    public TextArea getArea() {
        return area;
    }

    public void setAlgorithm(Algorithm selectedAlgorithm) {
        this.algorithm = selectedAlgorithm;
    }

    public void setHelperClass(GsonHelper helperClass) {
        this.helper = helperClass;
        this.helper.previewBuilder();
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
