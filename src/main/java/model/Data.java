package model;

public class Data {
    private String label;
    private int[] points;

    public Data() {
        points = new int[25];
        label = "";
    }

    public Data(String label, int[] points) {
        this.label = label;
        this.points = points;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int[] getPoints() {
        return points;
    }

    public void setPoints(int[] points) {
        this.points = points;
    }
}
