package algorithm;

import model.Data;

import java.util.Arrays;
import java.util.Objects;

import static controller.AlgorithmController.*;

public class Adaline {
    private double bias;
    private final double[] weights = new double[25];
    private final Data[] dataset;
    private final double learningRate;

    public Adaline(Data[] dataset, double learningRate) {
        log("Adaline Initialized.");
        this.dataset = dataset;
        this.learningRate = learningRate;
    }

    public void train(int count) {
        Arrays.fill(weights, 0);
        bias = 0;
        int x = 0;
        while (x < count) {
            for (Data data : dataset) {
                int label = Objects.equals(data.getLabel(), "X") ? 1 : -1;
                int[] matrix = data.getPoints();

                double yni = calculateSum(matrix);
                int prediction = stepFunction(yni);
                double error = label - prediction;
                    for (int j = 0; j < matrix.length; j++) {
                        weights[j] = weights[j] + (matrix[j] * error * learningRate);
                    }
                    bias = bias + error * learningRate;

            }
            x++;
        }
        log("The Dataset Trained Successfully.");
        logSep();

        log("The Trained Weights Are: \n" + Arrays.toString(weights));
        log("The Trained Bias Are: " + bias);
        logSep();
    }

    public double calculateSum(int[] points) {
        double sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += points[i] * weights[i];
        }
        sum += bias;

        return sum;
    }

    public int stepFunction(double sum) {
        if (sum >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public int predict(int[] points) {

        log("Start Prediction...");
        log("Data Matrix Is: ");
        logMatrix(points);
        logSep();

        double sum = calculateSum(points);
        log("Prediction Rate Is: " + sum);

        return stepFunction(sum);

    }
}
