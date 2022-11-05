package algorithm;

import controller.AlgorithmController;
import model.Data;

import java.util.Arrays;
import java.util.Objects;

import static controller.AlgorithmController.*;

public class Perceptron {
    private double bias;
    private final double[] weights = new double[25];
    private final Data[] dataset;
    private final double theta;
    private final double learningRate;

    public Perceptron(Data[] dataset, double theta, double learningRate) {
        log("Perceptron Initialized.");
        this.dataset = dataset;
        this.theta = theta;
        this.learningRate = learningRate;
    }

    public void train(int count) {
        Arrays.fill(weights, 0);
        bias = 0;
        int x = 0;
        double minFault = Double.MAX_VALUE;
        boolean stopped = false;
        while (x < count) {
            double faultCount = 0;
            for (Data data : dataset) {
                int label = Objects.equals(data.getLabel(), "X") ? 1 : -1;
                int[] matrix = data.getPoints();
                if (predict(matrix,false) != label) {
                    for (int j = 0; j < matrix.length; j++) {
                        weights[j] = weights[j] + (matrix[j] * label * learningRate);
                    }
                    bias = bias + label * learningRate;
                    faultCount++;
                }
            }
            minFault = Math.min(minFault,faultCount/(double) dataset.length);

            if (faultCount==0){
                stopped = true;
                break;
            }
            x++;
        }
        log("The Dataset Trained Successfully.");
        logSep();
        if (stopped){
            log("The Training Stopped At Number "+(x-1)+" Epochs. Cause Weights Are No Longer Updated.");
            logSep();
        }

        log("The Trained Weights Are: \n" + Arrays.toString(weights));
        log("The Trained Bias Are: " + bias);
        logSep();
        log("Minimum Fault Percent: "+(minFault/ (double) dataset.length));
        logSep();
    }

    public int predict(int[] points, boolean p) {
        if (p) {
            log("Start Prediction...");
            log("Data Matrix Is: ");
            logMatrix(points);
            logSep();
        }

        double sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += points[i] * weights[i];
        }
        sum += bias;
        if (p) {
            log("Prediction Rate Is: " + sum);
        }

        if (sum > theta) {
            return 1;
        } else if (sum >= (-1 * theta)) {
            return 0;
        } else {
            return -1;
        }
    }
}
