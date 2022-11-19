package algorithm;

import javafx.application.Platform;
import model.Data;
import tools.GsonHelper;
import view.Chart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

import static controller.AlgorithmController.log;
import static controller.AlgorithmController.logSep;
import static java.lang.Math.min;

public class MultiLayerPerceptron {
    private final Data[] dataset;
    private final int hiddenLayerCount;
    private final int outputLayerCount;
    private final double alpha;

    private double[][] v;
    private double[][] w;
    private double[] bv;
    private double[] bw;
    private Random random;
    private final PrintWriter out;
    private final double[][] result;
    private final File file = new File("mlp-fault-log.txt");

    public MultiLayerPerceptron(Data[] dataset, int hiddenLayerCount, int outputLayerCount, double alpha) {
        log("Multi Layer Perceptron Initialized.");

        this.dataset = dataset;
        this.hiddenLayerCount = hiddenLayerCount;
        this.outputLayerCount = outputLayerCount;
        this.alpha = alpha;
        result = new double[outputLayerCount][outputLayerCount];
        for (int i = 0; i < outputLayerCount; i++) {
            result[i][i] = 1.0;
        }

        try {
            out = new PrintWriter(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void train(int maxCount, long seed) {
        File test = new File("testData.json");
        GsonHelper helper = new GsonHelper(test);
        log("TestSet File: " + test.getAbsolutePath());

        Data[] trainSet = helper.readData().toArray(new Data[0]);
        random = new Random(seed);
        v = new double[hiddenLayerCount][dataset[0].getPoints().length];
        w = new double[outputLayerCount][hiddenLayerCount];
        bv = new double[hiddenLayerCount];
        bw = new double[outputLayerCount];
        double[] z = new double[hiddenLayerCount];
        double[] y = new double[outputLayerCount];
        double[] deltaOut = new double[outputLayerCount];
        double[] deltaHidden = new double[hiddenLayerCount];

        initializeRandomWeights(v);
        initializeRandomWeights(w);
        initializeRandomBias(bv);
        initializeRandomBias(bw);
        double minFaultPercent = Double.MAX_VALUE;
        for (int i = 0; i < maxCount; i++) {
            for (Data data : dataset) {
                int t = data.getLabel().equals("X") ? 0 : 1;
                double[] output = result[t];

                for (int j = 0; j < hiddenLayerCount; j++) {
                    z[j] = sigmoid((calculateNetInput(data.getPoints(), v[j]) + bv[j]));
                }

                for (int j = 0; j < outputLayerCount; j++) {
                    y[j] = sigmoid((calculateNetInput(w[j], z) + bw[j]));
                }

                for (int j = 0; j < outputLayerCount; j++) {
                    deltaOut[j] = (output[j] - y[j]) * (sigmoidDerivative(y[j]));
                }

                for (int j = 0; j < hiddenLayerCount; j++) {
                    double[] wColumn = new double[outputLayerCount];
                    for (int k = 0; k < w.length; k++) {
                        wColumn[k] = w[k][j];
                    }
                    deltaHidden[j] = (calculateNetInput(wColumn, deltaOut)) * (sigmoidDerivative(z[j]));
                }

                for (int j = 0; j < outputLayerCount; j++) {
                    for (int k = 0; k < hiddenLayerCount; k++) {
                        w[j][k] += alpha * deltaOut[j] * z[k];
                    }
                    bw[j] += alpha * deltaOut[j];
                }

                for (int j = 0; j < hiddenLayerCount; j++) {
                    for (int k = 0; k < data.getPoints().length; k++) {
                        v[j][k] += alpha * deltaHidden[j] * data.getPoints()[k];
                    }
                    bv[j] += alpha * deltaHidden[j];
                }
            }
            int faultCount = 0;
            for (Data data : trainSet) {
                if (predict(data.getPoints()) != (data.getLabel().equals("X") ? 1 : -1)) {
                    faultCount++;
                }
            }
            double faultPercent = faultCount / (double) trainSet.length;
            minFaultPercent = min(minFaultPercent, faultPercent);
            out.println(faultPercent);

            if (faultCount == 0) {
                break;
            }
        }
        Platform.runLater(Chart::new);
        out.close();

        log("The Dataset Trained Successfully.");
        logSep();

        log("The Trained Hidden Weights Are: \n" + Arrays.deepToString(v));
        log("The Trained Hidden Bias Are: " + Arrays.toString(bv));
        logSep();

        log("The Trained Output Weights Are: \n" + Arrays.deepToString(w));
        log("The Trained Output Bias Are: " + Arrays.toString(bw));
        logSep();

        log("Minimum Fault Percent: "+minFaultPercent);
        logSep();
    }

    public int predict(int[] points) {
        double[] h = new double[hiddenLayerCount];
        double[] o = new double[outputLayerCount];

        for (int i = 0; i < hiddenLayerCount; i++) {
            h[i] = sigmoid((calculateNetInput(points, v[i]) + bv[i]));
        }

        for (int i = 0; i < outputLayerCount; i++) {
            double x = calculateNetInput(h, w[i]) + bw[i];
            o[i] = Math.round(sigmoid(x));
        }

        if (o[0] == 1.0 && o[1] == 0.0) {
            return 1;
        } else if (o[0] == 0.0 && o[1] == 1.0) {
            return -1;
        } else {
            return 0;
        }
    }

    private void initializeRandomBias(double[] weights) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble() - 0.5;
        }
    }

    private void initializeRandomWeights(double[][] weights) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = random.nextDouble() - 0.5;
            }
        }
    }

    private double calculateNetInput(int[] a, double[] b) {
        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += b[i] * a[i];
        }

        return sum;
    }

    private double calculateNetInput(double[] a, double[] b) {
        double sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }

        return sum;
    }

    private double sigmoid(double x) {
        return (1.0 / (1.0 + Math.exp(-x)));
    }

    private double sigmoidDerivative(double x) {
        return sigmoid(x) * (1.0 - sigmoid(x));
    }
}
