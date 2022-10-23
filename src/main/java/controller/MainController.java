package controller;

import algorithm.Hebb;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private JFXButton add;

    @FXML
    private JFXButton predict;

    @FXML
    private AnchorPane board;

    @FXML
    private Text text;

    @FXML
    private JFXButton random;

    @FXML
    private JFXButton clear;

    @FXML
    private JFXButton x;

    @FXML
    private JFXButton o;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AtomicBoolean addMode = new AtomicBoolean(false);

        GsonHelper helper = new GsonHelper();
        Hebb hebb = new Hebb(helper.readData().toArray(new Data[0]));
        hebb.train();

        int[] matrix = new int[25];
        Arrays.fill(matrix, -1);

        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(15);
        pane.setVgap(15);

        StackPane centerPane = new StackPane(pane);
        AnchorPane.setBottomAnchor(centerPane, 0.0);
        AnchorPane.setLeftAnchor(centerPane, 0.0);
        AnchorPane.setRightAnchor(centerPane, 0.0);
        AnchorPane.setTopAnchor(centerPane, 0.0);

        drawButtons(matrix, pane, centerPane);

        board.getChildren().add(centerPane);

        predict.setOnAction(event -> {
            boolean result = hebb.predict(matrix);
            if (result){
                text.setText("The Predict Character Is: X");
            }else{
                text.setText("The Predict Character Is: O");
            }
        });
        random.setOnAction(event -> generateRandomData(matrix, pane, centerPane));
        clear.setOnAction(event -> clear(matrix, pane, centerPane));

        add.setOnAction(event -> {
            if (!addMode.get()) {
                addMode.set(true);
                add.setStyle("-fx-background-color: #32002f");
                x.setDisable(false);
                o.setDisable(false);
            } else {
                add.setStyle("-fx-background-color: #0073da");
                addMode.set(false);
                x.setDisable(true);
                o.setDisable(true);
            }
        });

        x.setOnAction(event -> {
            if (addMode.get()) {
                Data data = new Data("X", matrix);
                helper.writeData(data);

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
        text.setText("The Predict Character Is: UNKNOWN");
    }

    private void drawButtons(int[] matrix, GridPane pane, StackPane centerPane) {
        pane.getChildren().clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JFXButton button = new JFXButton(i + "-" + j);
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
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = random.nextInt() % 2 == 0 ? 1 : -1;
        }
        drawButtons(matrix, pane, centerPane);
    }
}
