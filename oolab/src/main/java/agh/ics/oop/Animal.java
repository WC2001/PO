package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Animal extends AbstractWorldMapElement{
    private MapDirection direction;
    private Vector2d position;
    private final AbstractWorldMap map;
    private int energy;
    private int daysSurvived;
    private final ArrayList<Integer> genes;
    private int children;
    private int energyToGiveBirth;
    private int energyForPlant;
    private int currentGene;
    private boolean withPortal;
    private boolean normalBehaviour;

    private final List<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(AbstractWorldMap map, Vector2d initialPosition, ArrayList<Integer> genes){

        this.direction = MapDirection.random();
        this.position = initialPosition;
        this.map = map;
        this.addObserver(this.map);
        this.genes = genes;
        this.children = 0;
        this.currentGene = 0;
        this.daysSurvived = 0;
        setMapOptions();

    }

    public void setMapOptions(){
        this.energyToGiveBirth = map.getEnergyToGiveBirth();
        this.energy = map.getStartingEnergy();
        this.energyForPlant = map.getEnergyForPlant();
        this.normalBehaviour = map.isStandardAnimalBehaviour();
        this.withPortal = map.isWithPortal();
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public MapDirection getDirection(){
        return this.direction;
    }

    boolean isAt(Vector2d position){
        return this.position.x == position.x && this.position.y == position.y;
    }

    public void changeDirection(MapDirection direction){
        this.direction = direction;
    }

    public String toString(){
        return switch (this.direction){
            case E -> "E";
            case W -> "W";
            case S -> "S";
            case N -> "N";
            case NE -> "NE";
            case NW -> "NW";
            case SE -> "SE";
            case SW -> "SW";
        };

    }

    public void move(){

        Vector2d newPosition = new Vector2d(this.position.x + direction.toUnitVector().x,
                (this.position.y + direction.toUnitVector().y + this.map.getWidth()) % this.map.getWidth());

        if (this.map.canMoveTo(newPosition)){
            positionChanged(this.position, newPosition);
            this.position = newPosition;
        }else{
            if(!withPortal)
                this.direction = this.direction.opposite();
            else{
                this.energy -= this.energyToGiveBirth;
                newPosition = this.map.randomPosition();
                positionChanged(this.position, newPosition);
                this.position = newPosition;
            }
        }
    }

    private void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    private void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observer : this.observers)
            observer.positionChanged(oldPosition, newPosition, this);
    }
    public void eat(){
        this.energy += this.energyForPlant;
    }

    public int getEnergy(){
        return this.energy;
    }
    public void setEnergy(int energy){
        this.energy = energy;
    }
    public int getDaysSurvived(){
        return this.daysSurvived;
    }
    public int getChildren(){
        return this.children;
    }

    public void turn(){
        for (int i=0;i<this.genes.get(currentGene);i++){
            this.direction = this.direction.next();
        }
    }
    public ArrayList<Integer>getGenes(){
        return this.genes;
    }

    public void increaseChildren(){
        this.children++;
    }

    public void increaseAge(){
        this.daysSurvived++;
        this.energy--;
    }

    public void changeGene(){
        Random random = new Random();
        if(this.normalBehaviour || random.nextDouble()<0.8)
            currentGene = (currentGene+1) % genes.size();
        else {
            int newGene = (int)(Math.floor(Math.random() * genes.size()));
            while (newGene == currentGene){
                newGene = (int)(Math.floor(Math.random() * genes.size()));
            }
            currentGene = newGene;
        }
    }

}

