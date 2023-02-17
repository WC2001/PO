package agh.ics.oop;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class SimulationThread extends Thread{


    private final String confFile;
    private final int index;
    private AbstractWorldMap map;
    private GridPane simulationGrid;
    private GridPane statsGrid;
    private final AtomicBoolean running =new AtomicBoolean(true);
    private boolean exit = false;
    private StatFileUtils statFileUtils;
    private static final int[] energyLevels = {0, 10, 20, 40};
    private final Lock graphicLock;

    public SimulationThread(int index, String confFile, Lock graphicLock){
        this.confFile = confFile;
        this.index = index;
        this.statFileUtils = new StatFileUtils(index);
        this.graphicLock = graphicLock;
        Platform.runLater(this::setUpSimulationStage);
    }

    public void setUpSimulationStage(){

        graphicLock.lock();

        ConfigurationLoader conf = new ConfigurationLoader(confFile);

        HashMap<String, Integer> options = conf.getConfiguration();

        float defaultWidth = 1500;
        float defaultHeight = 800;
        float grid1defaultWidth = 1200;
        Stage simulationStage = new Stage();
        boolean defaultGrassRespawn = options.get("middleGrowth") == 1;
        boolean randomMutate = options.get("randomMutation") == 1;
        boolean defaultAnimalBehavior = options.get("genesPredestination") == 1;
        boolean withPortal = options.get("standardMap") == 0;

        this.map = new SimulationMap(options.get("width"), options.get("height"), options.get("startingAnimals"),
                options.get("startingPlants"), defaultGrassRespawn, randomMutate, defaultAnimalBehavior,
                options.get("energyForPlant"), options.get("growth"), options.get("startingEnergy"), options.get("genesLength"),
                options.get("energyToReproduce"), withPortal);


        this.simulationGrid = setUpSimulationGrid(map, (int)(Math.floor((grid1defaultWidth-50) / options.get("width") )),
                (int)(Math.floor((defaultHeight-50) / options.get("height"))));

        fillGridPane(simulationGrid, map, (int)(Math.floor((grid1defaultWidth-50) / options.get("width") )),
                (int)(Math.floor((defaultHeight-50) / options.get("height"))));

        this.statsGrid = setUpStatGrid();
        HBox root = new HBox();
        root.getChildren().addAll(simulationGrid, statsGrid);
        Scene scene = new Scene(root, defaultWidth, defaultHeight, Color.BLUEVIOLET);
        simulationStage.setScene(scene);
        simulationStage.setResizable(false);
        simulationStage.setTitle("Simulation "+ index);
        simulationStage.setOnCloseRequest(event -> {
            statFileUtils.closeFile();
            exit = true;
        });

        simulationStage.show();

        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        graphicLock.unlock();

        start();

    }

    public void quitSimulation(){
        statFileUtils.closeFile();
        exit = true;
    }

    public GridPane setUpSimulationGrid(AbstractWorldMap map, int fieldWidth, int fieldHeight){
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        for(int i=0;i< map.getHeight();i++){
            gridPane.getRowConstraints().add(new RowConstraints(fieldHeight));
        }

        for(int j=0;j< map.getWidth();j++){
            gridPane.getColumnConstraints().add(new ColumnConstraints(fieldWidth));
        }

        return gridPane;
    }

    public GridPane setUpStatGrid(){
        ArrayList<Label> labels = new ArrayList<>();
        labels.add(new Label("Simulation Stats"));
        labels.add(new Label("Number of animals: " + map.numberOfAnimals()));
        labels.add(new Label("Number of plants: " + map.numberOfPlants()));
        labels.add( new Label("Number of empty fields: " + map.emptyFields()));
        labels.add( new Label("Most popular genes: " + map.mostPopularGenes()));
        labels.add(new Label("Mean animal energy: "+ map.meanEnergy()));
        labels.add( new Label("Mean lifetime: " + map.meanLifetime()));
        simulationLabelSetup(labels);

        Button stopSimulation = new Button("Stop simulation");
        stopSimulation.setPrefSize(120, 30);

        stopSimulation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                stopSimulation.setText(stopSimulation.getText().equals("Resume")? "Stop simulation" : "Resume");
                running.set(!running.get());
            }
        });

        stopSimulation.setStyle("-fx-background-color: transparent; -fx-border-color: #8A2BE2;-fx-border-radius: 45px; -fx-border-width: 1px; -fx-text-fill: #A3946DFF;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(100, 50, 100, 50));
        grid.setVgap(50);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < labels.size(); i++) {
            GridPane.setConstraints(labels.get(i), 0, i);
            GridPane.setHalignment(labels.get(i), HPos.CENTER);
            grid.getChildren().add(labels.get(i));
        }
        GridPane.setConstraints(stopSimulation, 0, labels.size());
        GridPane.setHalignment(stopSimulation, HPos.CENTER);
        grid.getChildren().addAll(stopSimulation);
        return grid;
    }

    public void simulationLabelSetup(ArrayList<Label> labels){
        for (Label l: labels) {
            l.setFont(new Font("ARIAL", 14));
            l.setTextFill(Color.rgb(163,148,109));
        }
    }

    public void fillGridPane(GridPane gridPane, AbstractWorldMap map, int cellWidth, int cellHeight) {
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                ImageView imageView = getImageView(i, j, map, cellWidth, cellHeight);
                gridPane.add(imageView, i, j);
            }
        }
    }

    public void updateGridPane(AbstractWorldMap map, GridPane gridPane){
        int index = 0;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof ImageView) {
                ImageView imageView = (ImageView) node;
                Image image = imageView.getImage();
                int width = (int) image.getWidth();
                int height = (int) image.getHeight();
                ImageView newImageView = getImageView(index/map.getWidth(), index % map.getWidth(),map, width, height);
                imageView.setImage(newImageView.getImage());
                index++;
            }
        }
    }
    public void updateStats(AbstractWorldMap map, GridPane gridPane){
        int index = 0;
        for (Node node : gridPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                label.setText(stat(index++, map));
            }
        }
    }

    public String stat(int index, AbstractWorldMap map){
        DecimalFormat df = new DecimalFormat("#.##");
        return switch (index){
            case 0 -> "Simulation Stats";
            case 1 -> "Number of animals: " + map.numberOfAnimals();
            case 2 -> "Number of plants: " + map.numberOfPlants();
            case 3 -> "Number of empty fields: " + map.emptyFields();
            case 4 -> "Most popular genes: " + map.mostPopularGenes();
            case 5 -> "Mean animal energy: "+ df.format(map.meanEnergy());
            case 6 -> "Mean lifetime: " + df.format(map.meanLifetime());
            default -> "";
        };
    }

    public List<String> stats(){
        return List.of(Integer.toString(map.numberOfAnimals()),
                Integer.toString(map.numberOfPlants()),
                Integer.toString(map.emptyFields()),
                map.mostPopularGenes(),
                Double.toString(map.meanEnergy()),
                Double.toString(map.meanLifetime()));
    }

    @Override
    public void run(){
        while(!exit){
            if(running.get()){
                this.map.performDay();
                graphicLock.lock();
                Platform.runLater(()->{

                    updateGridPane(map, simulationGrid);
                    updateStats(map, statsGrid);
                    statFileUtils.appendData(stats());
                });
                graphicLock.unlock();
                try {
                    Thread.sleep(500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }

    public ImageView getImageView(int i, int j, AbstractWorldMap map, int width, int height) {
        String ground = map.objectAt(new Vector2d(j, i)).stream()
                .anyMatch(e -> e instanceof Grass) ? "grass" : "desert";

        String energyLevel;

        double meanEnergy = map.meanAnimalEnergy(new Vector2d(j, i));

        if (meanEnergy == energyLevels[0])
            energyLevel = "0";
        else if (meanEnergy > energyLevels[0] && meanEnergy < energyLevels[1])
            energyLevel = "1";
        else if (meanEnergy >= energyLevels[1] && meanEnergy < energyLevels[2])
            energyLevel = "2";
        else if (meanEnergy >= energyLevels[2] && meanEnergy < energyLevels[3])
            energyLevel = "3";
        else
            energyLevel = "4";

        String imagePath = String.format("mapRepresentationFiles/%s%s.png", ground, energyLevel);

        ClassLoader classLoader = getClass().getClassLoader();
        Image image = new Image(classLoader.getResourceAsStream(imagePath));
        image = new Image(imagePath, width, height, false, false);

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        return imageView;
    }
}
