package view;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.ChartZoomManager;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Chart extends Stage {
    private final XYChart.Series<Number,Number> series = new XYChart.Series<>();
    private final ArrayList<Pair<Long,Double>> pairs = new ArrayList<>();
    public Chart(){
        try {
            readData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(createView());
        this.setScene(scene);
        this.show();
    }

    private void readData() throws IOException {
        Scanner scanner = new Scanner(Files.newInputStream(Paths.get("mlp-fault-log.txt")));
        long i=0;
        while (scanner.hasNextLine()){
            pairs.add(new Pair<>((++i),Double.parseDouble(scanner.nextLine())));
        }
    }

    public AnchorPane createView(){
        AnchorPane pane = new AnchorPane();
        pane.getStyleClass().add("root");
        pane.setPrefWidth(600);
        pane.setPrefHeight(400);
        series.setName("Multi Layer Perceptron Fault");
        for (Pair<Long,Double> pair:pairs){
            series.getData().add(new XYChart.Data<>(pair.getKey(),pair.getValue()));
        }
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Epoch");
        xAxis.setAutoRanging(true);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Fault");
        LineChart<Number,Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(true);
        chart.setCreateSymbols(false);
        ChartZoomManager zoomManager = new ChartZoomManager(pane,new Rectangle(600,400),chart);
        zoomManager.setMouseWheelZoomAllowed(true);
        zoomManager.setMouseFilter(event -> {
            if (event.isDragDetect())event.consume();
        });
        zoomManager.setZoomAnimated(true);

        zoomManager.start();
        ChartPanManager panManager = new ChartPanManager(chart);
        panManager.start();
        try {
            pane.getStylesheets().add(new File("src/main/java/view/style.css").toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        chart.getData().add(series);
        AnchorPane.setLeftAnchor(chart,0.0);
        AnchorPane.setRightAnchor(chart,0.0);
        AnchorPane.setTopAnchor(chart,0.0);
        AnchorPane.setBottomAnchor(chart,0.0);
        pane.getChildren().add(chart);
        return pane;
    }
}
