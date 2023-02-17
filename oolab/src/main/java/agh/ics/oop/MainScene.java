package agh.ics.oop;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainScene {

    private final Lock graphicLock= new ReentrantLock();

    private int simulationCounter = 1;
    private static final int defaultWidth = 700;
    private static final int defaultHeight = 400;
    private static final Color defaultColor = Color.BLUEVIOLET;

    private static final String defaultConfFilepath = "configuration/confFile";
    private String confFilePath = defaultConfFilepath;

    private List<SimulationThread> activeThreads = new ArrayList<>();

    public MainScene(Stage primaryStage){

        GridPane grid = setUpPrimaryGrid(primaryStage);
        Scene scene = new Scene(grid, defaultWidth, defaultHeight, defaultColor);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Welcome");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stopActiveThreads();
            }
        });
        primaryStage.show();
    }

    public GridPane setUpPrimaryGrid(Stage primaryStage){
        Label messageLabel = new Label("Welcome! Choose the configuration file.");
        messageLabel.setFont(new Font("ARIAL",24));
        messageLabel.setTextFill(Color.rgb(163,148,109));

        FileChooser fileChooser = new FileChooser();

        Button configurationButton = new Button("Set configuration");
        configurationButton.setStyle("-fx-background-color: transparent; -fx-border-color: #8A2BE2;-fx-border-radius: 45px; -fx-border-width: 1px; -fx-text-fill: #A3946DFF;");

        configurationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    configurationButton.setText(file.getAbsolutePath());
                    confFilePath = file.getAbsolutePath();
                }
            }
        });
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                SimulationThread  simulation = new SimulationThread(simulationCounter++, confFilePath, graphicLock);
                activeThreads.add(simulation);
            }
        });
        confirmButton.setStyle("-fx-background-color: transparent; -fx-border-color: #8A2BE2;-fx-border-radius: 45px; -fx-border-width: 1px; -fx-text-fill: #A3946DFF;");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(100, 100, 100, 100));
        grid.setVgap(50);
        grid.setAlignment(Pos.CENTER);
        GridPane.setConstraints(messageLabel, 2, 0);
        GridPane.setConstraints(configurationButton,2, 1);
        GridPane.setConstraints(confirmButton, 2, 2);
        GridPane.setHalignment(messageLabel, HPos.CENTER);
        GridPane.setHalignment(configurationButton, HPos.CENTER);
        GridPane.setHalignment(confirmButton, HPos.CENTER);
        grid.getChildren().addAll(messageLabel, configurationButton, confirmButton);
        return grid;
    }

    private void stopActiveThreads() {

        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }
        Thread[] threads = new Thread[rootGroup.activeCount()];
        while (rootGroup.enumerate(threads, true) == threads.length) {
            threads = new Thread[threads.length * 2];
        }

        for (Thread t : threads) {
            if (t != null && t.isAlive() && t instanceof SimulationThread) {
                SimulationThread simulationThread = (SimulationThread) t;
                simulationThread.quitSimulation();
            }
        }
    }

    public String getConfFilePath(){
        return confFilePath;
    }
}
