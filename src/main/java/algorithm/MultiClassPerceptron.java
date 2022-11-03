package algorithm;

import controller.AlgorithmController;
import main.Main;
import model.Data;

import java.util.Arrays;
import java.util.Objects;

public class MultiClassPerceptron {
    private final double[] bias = new double[2];
    private final double[][] weights = new double[2][25];
    private final Data[] dataset;

    private final double theta;
    private final double learningRate;

    public MultiClassPerceptron(Data[] dataset, double theta, double learningRate) {
        this.dataset = dataset;
        this.theta = theta;
        this.learningRate = learningRate;
    }

    public void train(int count) {
        Arrays.fill(weights[0], 0);
        Arrays.fill(weights[1], 0);
        bias[0] = 0;
        bias[1] = 0;

        double minFault = Double.MAX_VALUE;
        boolean stopped = false;

        int x = 0;
        while (x < count) {
            double faultCount = 0;
            for (Data data : dataset) {
                int label = Objects.equals(data.getLabel(), "X") ? 1 : -1;
                int[] matrix = data.getPoints();
                if (activationFunction(matrix, weights[0], bias[0]) != label) {
                    for (int j = 0; j < matrix.length; j++) {
                        weights[0][j] = weights[0][j] + (matrix[j] * label * learningRate);
                    }
                    bias[0] = bias[0] + label * learningRate;

                    faultCount++;
                }
                if ((activationFunction(matrix, weights[1], bias[1]) * -1) != label) {
                    for (int j = 0; j < matrix.length; j++) {
                        weights[1][j] = weights[1][j] + (matrix[j] * label * learningRate * -1);
                    }
                    bias[1] = bias[1] + (label * learningRate * -1);

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
        AlgorithmController.log("The Dataset Trained Successfully.");
        AlgorithmController.logSep();
        if (stopped){
            AlgorithmController.log("The Training Stopped At Number "+(x-1)+" Epochs. Cause Weights Are No Longer Updated.");
            AlgorithmController.logSep();
        }
        AlgorithmController.log("The Trained Weights For X Are: \n" + Arrays.toString(weights[0]));
        AlgorithmController.log("The Trained Bias For X Are: " + bias[0]);
        AlgorithmController.logSep();

        AlgorithmController.log("The Trained Weights For O Are: \n" + Arrays.toString(weights[1]));
        AlgorithmController.log("The Trained Bias For O Are: " + bias[1]);

        AlgorithmController.logSep();
        AlgorithmController.log("Minimum Fault Percent: "+(minFault/ (double) dataset.length));
        AlgorithmController.logSep();
    }

    public int activationFunction(int[] points, double[] weights, double bias) {
        double sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum += points[i] * weights[i];
        }
        sum += bias;

        if (sum > theta) {
            return 1;
        } else if (sum >= (-1 * theta)) {
            return 0;
        } else {
            return -1;
        }
    }

    public int predict(int[] points) {
        int x = activationFunction(points, weights[0], bias[0]);
        int o = activationFunction(points, weights[1], bias[1]);
        AlgorithmController.log("Prediction Rate For X = " + x + ".");
        AlgorithmController.log("Prediction Rate For O = " + o + ".");

        if (x == 1 && o == -1) {
            return 1;
        } else if (x == -1 && o == 1) {
            return -1;
        } else {
            return 0;
        }
    }
}
