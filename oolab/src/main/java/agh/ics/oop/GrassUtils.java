package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GrassUtils {

    private final SimulationMap simulation;
    public GrassUtils(SimulationMap map){
        this.simulation = map;
    }

    public void spawnGrass(int quantity){
        int spawned = 0;

        while (spawned < quantity && simulation.numberOfPlants() < simulation.getHeight() * simulation.getWidth()) {
            Vector2d position = null;
            boolean preferred = Math.random() < .8;

            if (preferred) {
                position = randomPreferredSquare();
            } else {
                position = randomUnoccupiedSquare();
            }

            if (position != null) {
                Grass newGrass = new Grass(position);
                this.simulation.hashMap.get(position).add(newGrass);
                spawned++;
            }
        }
    }

    public Vector2d randomPreferredSquare() {
        List<Vector2d> notContainingGrass = simulation.preferredGrassField.stream().filter(e -> !containsGrass(e)).toList();
        if (notContainingGrass.isEmpty()) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(notContainingGrass.size());
            return notContainingGrass.get(index);
        }
    }

    public Vector2d randomUnoccupiedSquare() {
        List<Vector2d> unoccupiedSquares = new ArrayList<>();
        for (int i = 0; i < simulation.getHeight(); i++) {
            for (int j = 0; j < simulation.getWidth(); j++) {
                Vector2d toCheck = new Vector2d(i, j);
                if (!simulation.preferredGrassField.contains(toCheck) && !containsGrass(toCheck)) {
                    unoccupiedSquares.add(toCheck);
                }
            }
        }
        if (unoccupiedSquares.isEmpty()) {
            return null;
        } else {
            Random random = new Random();
            int index = random.nextInt(unoccupiedSquares.size());
            return unoccupiedSquares.get(index);
        }
    }

    public boolean containsGrass(Vector2d position){
        if (position == null)
            return false;
        return simulation.hashMap.get(position).stream().
                anyMatch(elem -> elem instanceof Grass);
    }


    public void initPreferredGrass(){
        int numberOfPreferredFields = (int) (SimulationMap.getPreferredFieldsRatio() * simulation.getHeight() * simulation.getWidth());
        if(simulation.getStandardGrassRespawn()){
            int middleRow = simulation.getHeight()/2;
            int setToPreferred = 0;
            int dy= 0;
            while(setToPreferred < numberOfPreferredFields){
                for(int i=0;i<simulation.getWidth();i++){
                    simulation.preferredGrassField.add(new Vector2d(middleRow+dy, i));
                    if(++setToPreferred == numberOfPreferredFields)
                        break;
                    if(!simulation.preferredGrassField.contains(new Vector2d(middleRow-dy, i))){
                        simulation.preferredGrassField.add(new Vector2d(middleRow-dy, i));
                        if(++setToPreferred == numberOfPreferredFields)
                            break;
                    }
                }
                dy++;
            }
        }else{
            ArrayList<Integer>result = new ArrayList<>();
            Random random = new Random();

            while (result.size() < numberOfPreferredFields){
                int index = random.nextInt(simulation.getWidth() * simulation.getHeight());
                if(!result.contains(index))
                    result.add(index);
            }

            for (int index: result) {
                simulation.preferredGrassField.add(new Vector2d(index/simulation.getWidth(), index % simulation.getWidth()));
            }
        }
    }

    public List<Vector2d> updatedPreferredGrassSquares(){
        return simulation.getDeadAnimalMap().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit((int) Math.ceil(simulation.getHeight() * simulation.getWidth() * 0.2))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
