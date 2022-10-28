package tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.Main;
import model.Data;

import java.io.*;
import java.util.*;

public class GsonHelper {
    private final Gson gson;
    private final File file;
    private Scanner scanner;
    private PrintWriter writer;

    public GsonHelper(File file) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
        this.file = file;
    }

    public void fileOpen() {
        try {
            scanner = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(Data[] data) {
        try {
            ArrayList<Data> dataArrayList = readData();
            dataArrayList.addAll(Arrays.asList(data));
            writer = new PrintWriter(new FileOutputStream(file));
            writer.println(gson.toJson(dataArrayList));
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeData(Data data) {
        try {
            ArrayList<Data> dataArrayList = readData();
            dataArrayList.add(data);
            writer = new PrintWriter(new FileOutputStream(file));
            writer.println(gson.toJson(dataArrayList));
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Data> readData() {
        fileOpen();
        StringBuilder dataString = new StringBuilder();
        while (scanner.hasNextLine()) {
            dataString.append(scanner.nextLine());
        }

        if (dataString.toString().isEmpty()) {
            return new ArrayList<>();
        }
        scanner.close();
        return new ArrayList<>(Arrays.asList(gson.fromJson(dataString.toString(), Data[].class)));
    }

    public void previewBuilder() {
        ArrayList<Data> data = readData();
        try {
            PrintWriter writer1 = new PrintWriter(new FileOutputStream("preview.txt"));
            writer1.println("[");
            for (Data d : data) {
                writer1.println("\t{");
                writer1.println("\t\tlabel: " + d.getLabel() + ",");
                writer1.println("\t\tdata: \"");
                for (int i = 0; i < 5; i++) {
                    StringBuilder builder = new StringBuilder();
                    for (int j = 0; j < 5; j++) {
                        builder.append(d.getPoints()[i * 5 + j] == 1 ? "#" : ".").append("\t");
                    }
                    writer1.println("\t\t" + builder);
                }
                writer1.println("\t\"},");
            }
            writer1.println("]");
            writer1.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
