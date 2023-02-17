package agh.ics.oop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StatFileUtils {

    private final String separator = ";";
    private FileWriter writer;
    private static final String defaultStatFileLocation = "oolab/src/main/resources/simulationFiles/";

    public StatFileUtils(int index){
        String filename = defaultStatFileLocation + "simulation" + index + ".csv";
        try {
            this.writer = new FileWriter(filename);
            this.writer.append("NumberOfAnimals").append(separator)
                    .append("NumberOfPlants").append(separator)
                    .append("EmptyFields").append(separator)
                    .append("MostPopularGenes").append(separator)
                    .append("MeanAnimalEnergy").append(separator)
                    .append("MeanLifetime").append("\n");

            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendData(List<String> data) {
        try {
            this.writer.append(String.join(separator, data))
                    .append("\n")
                    .flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
