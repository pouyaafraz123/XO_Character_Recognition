package tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Data;

import java.io.*;
import java.util.*;

public class GsonHelper {
    private final Gson gson;
    private final File file;
    private Scanner scanner;
    private PrintWriter writer;

    public GsonHelper() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();

        file = new File("data1.txt");
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
}
