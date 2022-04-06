package com.example.lab09;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class HelloApplication extends Application {
    private static double width = 800;
    private static double height = 600;
    private static double spacing = 50;
    private static double lowest = 0;
    private static double highest = 0;

    @Override
    public void start(Stage stage) throws IOException {
        ArrayList<Float> stock1 = downloadStockPrices("AMZN");
        ArrayList<Float> stock2 = downloadStockPrices("GOOG");
        ArrayList<Float> stock3 = downloadStockPrices("AAPL");


        Group group = drawLinePlot(stock1, stock2, stock3);
        Scene scene = new Scene(group, width, height);
        stage.setTitle("Lab09");
        stage.setScene(scene);
        stage.show();
    }

    public static ArrayList<Float> downloadStockPrices(String ticker) throws FileNotFoundException, MalformedURLException {
        URL url = new URL("https://query1.finance.yahoo.com/v7/finance/download/"+ ticker +"?period1=1262322000&period2=1451538000&interval=1mo&events=history&includeAdjustedClose=true");
        try (InputStream in = url.openStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream fos = new FileOutputStream("stocks.csv")) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line;
        ArrayList<Float> stonks = new ArrayList<Float>();

        //Reading from CSV
        try (BufferedReader br = new BufferedReader(new FileReader("stocks.csv"))) {
            br.readLine();
            while((line = br.readLine()) != null){
                Float values = Float.parseFloat(line.split(",")[4]);
                stonks.add(values);
            }
        } catch (Exception e){
            System.out.println(e);
        }

        // this finds the total range for all stock amounts.
        for(int ii = 0; ii<stonks.size(); ii++) {
            if(stonks.get(ii)>highest){
                highest = stonks.get(ii);
            }
            if(stonks.get(ii)<lowest){
                lowest = stonks.get(ii);
            }

        }
        return stonks;
    }

    public static Group drawLinePlot(ArrayList<Float> list1, ArrayList<Float> list2, ArrayList<Float> list3) {
        // Axises (Axes?)
        Line xAxis = new Line();
        xAxis.setStartX(spacing);
        xAxis.setStartY(height-spacing);
        xAxis.setEndX(width-spacing);
        xAxis.setEndY(height-spacing);

        Line yAxis = new Line();
        yAxis.setStartX(spacing);
        yAxis.setStartY(spacing);
        yAxis.setEndX(spacing);
        yAxis.setEndY(height-spacing);

        Group stock1 = plotLine(list1,Color.CADETBLUE);
        Group stock2 = plotLine(list2,Color.CRIMSON);
        Group stock3 = plotLine(list3,Color.DARKORANGE);


        return new Group(xAxis,yAxis,stock1,stock2, stock3);
    }

    public static Group plotLine(ArrayList<Float> stock,Color color) {
        System.out.println("TEST PLOTLINE");
        double plotWidth = width-(spacing*2);
        double plotHeight = height-(spacing*2);

        double numPoints = stock.size();
        double pointSpacing = plotWidth/numPoints;
        double vRange = highest-lowest;
        System.out.println(vRange);

//        System.out.println(numPoints);
        System.out.println(plotHeight);

        Group plotted = new Group();

        for (int ii = 1; ii<numPoints; ii++) {
//            System.out.println(height-spacing-plotHeight/stock.get(ii-1));
//            System.out.println("TEST FOR LOOP");
            Line line = new Line();
            line.setStartX(spacing+(ii-1)*pointSpacing);
            line.setEndX(spacing+ii*pointSpacing);
            line.setStartY(height-spacing-stock.get(ii-1)/vRange*plotHeight);
            line.setEndY(height-spacing-stock.get(ii)/vRange*plotHeight);
            line.setStroke(color);
            plotted.getChildren().add(line);
        }


        return plotted;
    }

    public static void main(String[] args) {
        launch();
    }
}