package controller;

import algorithm.Hebb;
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
import main.Main;
import model.Data;
import tools.GsonHelper;

import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
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

    private Hebb hebb;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        area.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> area.setScrollTop(Double.MAX_VALUE));
        AtomicBoolean addMode = new AtomicBoolean(false);
        GsonHelper helper = new GsonHelper();
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
            Main.log("Start Reading Data...");
            Data[] trainSet = helper.readData().toArray(new Data[0]);
            Main.log("Reading Data Finished.");
            Main.log("Start Training For " + trainSet.length + " Data.");
            hebb = new Hebb(trainSet);
            long startTime = System.currentTimeMillis();
            hebb.train();

            Main.log("Train Time: " + (System.currentTimeMillis() - startTime) + " Milli Seconds.");
            Main.logSep();
            predict.setDisable(false);

            text.setText("Select An Option:");
        });

        predict.setOnAction(event -> {
            boolean result = hebb.predict(matrix);
            if (result) {
                text.setText("The Predict Character Is: X");
                Main.log("X Predicted.");
            } else {
                text.setText("The Predict Character Is: O");
                Main.log("O Predicted.");
            }
            Main.logSep();
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
                Main.log("Add Mode Enabled.");
            } else {
                add.setStyle("-fx-background-color: #0073da");
                addMode.set(false);
                x.setDisable(true);
                o.setDisable(true);
                Main.log("Add Mode Disabled.");
            }
        });

        x.setOnAction(event -> {
            if (addMode.get()) {
                Data data = new Data("X", matrix);
                helper.writeData(data);
                Main.log("Data");
                Main.logMatrix(matrix);
                Main.log("Added To Train Set As: X");
                Main.logSep();
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

                Main.log("Data");
                Main.logMatrix(matrix);
                Main.log("Added To Train Set As: O");
                Main.logSep();
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
        Main.log("Board Cleared.");
        Main.logSep();
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
        Main.log("Start Generating Random Data:");
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = random.nextInt() % 2 == 0 ? 1 : -1;
        }
        Main.log("The Data Is Created:");
        Main.logMatrix(matrix);
        Main.logSep();
        drawButtons(matrix, pane, centerPane);
    }

    public TextArea getArea() {
        return area;
    }
}
