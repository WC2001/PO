package agh.ics.oop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ConfigurationLoader {

    private static final String defaultConfFilepath = "configuration/confFile";
    private static final String defaultMapRestrictions = "configuration/mapRestrictions";

    private final HashMap<String,Integer> configuration = new HashMap<>();
    private final HashMap<String, int[]> bounds = new HashMap<>();

    private final String[] optionsToCheck = {"growth", "startingAnimals", "startingPlants"};

    public ConfigurationLoader(String filename){

        loadMapBounds();
        loadDefaultConfiguration();
        if(!defaultConfFilepath.equals(filename))
            loadUserConfiguration(filename);
    }

    public Integer parseValue(String key, String value){
        if(bounds.containsKey(key)){
            if(Integer.parseInt(value) < bounds.get(key)[0])
                return bounds.get(key)[0];
            if(Integer.parseInt(value) > bounds.get(key)[1])
                return bounds.get(key)[1];
        }
        return Integer.parseInt(value);
    }

    public HashMap<String, Integer> getConfiguration(){
        return this.configuration;
    }

    public void loadDefaultConfiguration(){
        ClassLoader classLoader = getClass().getClassLoader();
        File confFile = new File(Objects.requireNonNull(classLoader.getResource(defaultConfFilepath)).getFile());
        try (BufferedReader reader = new BufferedReader(new FileReader(confFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();
                Integer parsedValue = Integer.parseInt(value);
                configuration.put(key, parsedValue);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadMapBounds(){
        ClassLoader classLoader = getClass().getClassLoader();
        File boundsFile = new File(Objects.requireNonNull(classLoader.getResource(defaultMapRestrictions)).getFile());
        try (BufferedReader reader = new BufferedReader(new FileReader(boundsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0].trim();
                String minValue = parts[1].trim();
                String maxValue = parts[2].trim();
                int parsedMinValue = Integer.parseInt(minValue);
                int parsedMaxValue = Integer.parseInt(maxValue);
                bounds.put(key, new int[]{parsedMinValue, parsedMaxValue});
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadUserConfiguration(String filename){
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0].trim();
                String value = parts[1].trim();
                Integer parsedValue = parseValue(key, value);

                configuration.put(key, parsedValue);
            }

            for (String option: optionsToCheck) {
                configuration.put(option,
                        Math.min(configuration.get(option),
                                configuration.get("height") * configuration.get("width") / 2)
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
