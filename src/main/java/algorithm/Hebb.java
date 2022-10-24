package algorithm;

import main.Main;
import model.Data;

import java.util.Arrays;
import java.util.Objects;

public class Hebb {
    private double bias;
    private final double[] weights = new double[25];
    private final Data[] dataset;

    public Hebb(Data[] dataset) {
        this.dataset = dataset;
    }

    public void train() {
        Arrays.fill(weights, 0);
        bias = 0;

        for (Data data : dataset) {
            int label = Objects.equals(data.getLabel(), "X") ? 1 : -1;
            int[] matrix = data.getPoints();

            for (int j = 0; j < matrix.length; j++) {
                weights[j] = weights[j] + (matrix[j] * label);
            }
            bias = bias + label;
        }

        Main.log("The Dataset Trained Successfully.");
        Main.log("The Trained Weights Are: \n" + Arrays.toString(weights));
        Main.log("The Trained Bias Are: " + bias);
    }

    public boolean predict(int[] points) {
        Main.log("Start Prediction...");
        Main.log("Data Matrix Is: ");
        Main.logMatrix(points);

        double sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += points[i] * weights[i];
        }
        sum += bias;

        Main.log("Prediction Rate Is: " + sum);

        return sum >= 0;
    }
}
