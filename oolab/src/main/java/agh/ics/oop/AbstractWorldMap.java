package agh.ics.oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver{
    protected List<Animal> animals = new ArrayList<>();
    protected Map<Vector2d, List<AbstractWorldMapElement>> hashMap = new HashMap<>();
    protected List<Vector2d> preferredGrassField = new ArrayList<>();

    public boolean canMoveTo(Vector2d position){
        return position.x >= 0 && position.y >= 0;
    }

    public boolean place(Animal animal) {
        if(canMoveTo(animal.getPosition())) {
            this.animals.add(animal);
            this.hashMap.get(animal.getPosition()).add(animal);
            return true;
        }
        throw new IllegalArgumentException("Cannot place animal at " + animal.getPosition());
    }

    public List<AbstractWorldMapElement> objectAt(Vector2d position) {

        return this.hashMap.get(position);
    }

    public boolean isOccupied(Vector2d position) {
        return this.hashMap.get(position) != null && this.hashMap.get(position).size() > 0;
    }
    public int getWidth(){
        return 0;
    }
    public int getHeight(){
        return 0;
    }



    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, IMapElement elem){
        List<AbstractWorldMapElement>oldField = this.hashMap.get(oldPosition);
        List<AbstractWorldMapElement>newField = this.hashMap.get(newPosition);
        if(oldField != null)
            this.hashMap.get(oldPosition).remove((AbstractWorldMapElement) elem);
        if(newField != null)
            this.hashMap.get(newPosition).add((AbstractWorldMapElement) elem);
    }

    public abstract void performDay();

    public abstract int numberOfAnimals();

    public abstract int numberOfPlants();

    public abstract int emptyFields();

    public abstract double meanEnergy();

    public abstract double meanLifetime();

    public abstract String mostPopularGenes();

    public abstract double meanAnimalEnergy(Vector2d position);

    public abstract Vector2d randomPosition();

    public abstract int getEnergyToGiveBirth();
    
    public abstract int getStartingEnergy();

    public abstract int getEnergyForPlant();

    public abstract boolean isStandardAnimalBehaviour();

    public abstract boolean isWithPortal();

}

