package agh.ics.oop;

import java.util.*;
import java.util.stream.Collectors;

public class SimulationMap extends AbstractWorldMap{

    private final int width;
    private final int height;
    private final int energyForPlant;
    private final int startingEnergy;
    private final boolean standardGrassSpawn;
    private final boolean randomMutate;
    private final boolean standardAnimalBehaviour;
    private final boolean withPortal;
    private final int genesLength;
    private final int energyToGiveBirth;
    private final int dailyGrassSpawn;
    static double preferredFieldsRatio = .2;
    private int deadAnimals = 0;
    private int sumOfLifetime = 0;

    private static final GeneUtils geneUtils = new GeneUtils();
    private final GrassUtils grassUtils;
    private final Map<Vector2d, Integer> deadAnimalCounter = new HashMap<>();

    public SimulationMap(int width, int height, int startingAnimals, int startingGrass,
                         boolean standardGrassSpawn, boolean randomMutate,
                         boolean standardAnimalBehaviour, int energyForPlant,
                         int dailyGrassSpawn, int startingEnergy, int genesLength,
                         int energyToGiveBirth, boolean withPortal){

        this.width = width;
        this.height = height;
        this.energyForPlant = energyForPlant;
        this.standardAnimalBehaviour = standardAnimalBehaviour;
        this.standardGrassSpawn = standardGrassSpawn;
        this.randomMutate = randomMutate;
        this.startingEnergy = startingEnergy;
        this.genesLength = genesLength;
        this.energyToGiveBirth = energyToGiveBirth;
        this.dailyGrassSpawn = dailyGrassSpawn;
        this.withPortal = withPortal;
        this.grassUtils = new GrassUtils(this);

        if(!standardGrassSpawn)
            initDeadAnimalCounter();

        initHashMap();
        grassUtils.initPreferredGrass();
        grassUtils.spawnGrass(startingGrass);
        spawnAnimals(startingAnimals);
    }

    public void initDeadAnimalCounter(){
        for (int i=0;i<this.height;i++){
            for (int j=0;j<this.width;j++){
                this.deadAnimalCounter.put(new Vector2d(i,j), 0);
            }
        }
    }

    public void initHashMap(){
        for (int i=0;i<this.height;i++){
            for (int j=0;j<this.width;j++){
                this.hashMap.put(new Vector2d(i,j), new ArrayList<>(0));
            }
        }
    }

    public void performDay(){
        cleanMap();
        performMove();
        resolveEating();
        resolveReproduce();
        grassUtils.spawnGrass(dailyGrassSpawn);
        resolveAging();
        if(!standardGrassSpawn)
           setPreferredGrassField(grassUtils.updatedPreferredGrassSquares());
    }

    public void cleanMap(){
        Iterator<Animal> iterator = animals.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if (animal.getEnergy() <= 0) {
                iterator.remove();
                hashMap.get(animal.getPosition()).remove(animal);
                deadAnimals++;
                sumOfLifetime += animal.getDaysSurvived();
                if(!standardGrassSpawn)
                    deadAnimalCounter.put(animal.getPosition(), deadAnimalCounter.get(animal.getPosition()) + 1);
            }
        }
    }

    public void performMove(){
        for (Animal a:animals) {
            a.turn();
            a.move();
            a.changeGene();
        }
    }

    public void resolveEating(){
        hashMap.forEach((key,value)->{
            if(grassUtils.containsGrass(key) && value.size()>=2){
                findWhichEats(value);
                value.removeIf(o-> o instanceof Grass);
            }
        });
    }

    public void resolveReproduce(){
        hashMap.forEach((key,value)->{
            List<Animal> animals = value.stream().filter(e->e instanceof Animal )
                    .map(o->(Animal) o).filter(e->e.getEnergy()>energyToGiveBirth).collect(Collectors.toList());

            if(animals.size() >= 2){
                Animal[] reproducing = animals.stream()
                        .sorted((a1, a2)->{
                            if (a1.getEnergy() != a2.getEnergy()) {
                                return Integer.compare(a1.getEnergy(), a2.getEnergy());
                            } else if (a1.getDaysSurvived() != a2.getDaysSurvived()) {
                                return Integer.compare(a1.getDaysSurvived(), a2.getDaysSurvived());
                            } else {
                                return Integer.compare(a1.getChildren(), a2.getChildren());
                            }
                        })
                        .limit(2)
                        .toArray(Animal[]::new);

                reproduce(reproducing[0], reproducing[1]);
            }
        });
    }

    public void resolveAging(){
        for (Animal a: animals) {
            a.increaseAge();
        }
    }

    public Vector2d randomPosition(){
        return new Vector2d((int)(Math.floor(Math.random()*height)), (int)(Math.floor(Math.random()*width)));
    }

    public void spawnAnimals(int quantity){
        Vector2d position;
        int spawned = 0;
        while (spawned < quantity){
            position = randomPosition();
            Animal animal = new Animal(this, position, geneUtils.randomGenes(genesLength));
            hashMap.get(position).add(animal);
            animals.add(animal);
            spawned++;
        }
    }

    public void findWhichEats(List<AbstractWorldMapElement> list){

        List<Animal> animals = list.stream().filter(e->e instanceof Animal)
                .map(o->(Animal) o).collect(Collectors.toList());
        if( animals.size()> 0){
            Animal eating = Collections.max(animals, (a1, a2)->{
                if (a1.getEnergy() != a2.getEnergy()) {
                    return Integer.compare(a1.getEnergy(), a2.getEnergy());
                } else if (a1.getDaysSurvived() != a2.getDaysSurvived()) {
                    return Integer.compare(a1.getDaysSurvived(), a2.getDaysSurvived());
                } else {
                    return Integer.compare(a1.getChildren(), a2.getChildren());
                }
            });
            eating.eat();
        }
    }

    public void reproduce(Animal a1, Animal a2){
        ArrayList<Integer> genes = geneUtils.generateGenes(a1, a2, genesLength, randomMutate);
        a1.setEnergy(a1.getEnergy()-energyToGiveBirth);
        a2.setEnergy(a2.getEnergy()-energyToGiveBirth);
        Animal newAnimal = new Animal(this, a1.getPosition(), genes);

        a1.increaseChildren();
        a2.increaseChildren();
        animals.add(newAnimal);
        hashMap.get(a1.getPosition()).add(newAnimal);
    }

    public int numberOfAnimals(){
        return animals.size();
    }

    public int numberOfPlants(){
        return hashMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(Grass.class::isInstance)
                .reduce(0, (count, grass) -> count + 1, Integer::sum);
    }

    public int emptyFields(){
        return hashMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(list -> list.isEmpty())
                .reduce(0, (count, emptyList) -> count + 1, Integer::sum);
    }

    public double meanEnergy(){
        return animals.stream()
                .mapToInt(Animal::getEnergy)
                .average()
                .orElse(0.0);
    }

    public double meanLifetime(){
        return deadAnimals == 0 ? 0 : (double) sumOfLifetime / deadAnimals;
    }

    public String mostPopularGenes(){
        HashMap<ArrayList<Integer>, Integer> geneCounts = new HashMap<>();

        for (List<AbstractWorldMapElement> field : hashMap.values()) {
            for (Object element : field) {
                if (element instanceof Animal){
                    ArrayList<Integer> genes = ((Animal)element).getGenes();
                    if (geneCounts.containsKey(genes)) {
                        geneCounts.put(genes, geneCounts.get(genes) + 1);
                    } else {
                        geneCounts.put(genes, 1);
                    }
                }
            }
        }

        ArrayList<Integer> mostPopularGenes = null;
        int highestCount = 0;
        for (Map.Entry<ArrayList<Integer>, Integer> entry : geneCounts.entrySet()) {
            ArrayList<Integer> genes = entry.getKey();
            int count = entry.getValue();
            if (count > highestCount) {
                mostPopularGenes = genes;
                highestCount = count;
            }
        }

        return mostPopularGenes == null ? " " : String.join("", mostPopularGenes.stream().map(Object::toString).collect(Collectors.toList()));
    }

    public Map<Vector2d, List<AbstractWorldMapElement>> getMap(){
        return this.hashMap;
    }

    public Map<Vector2d, Integer> getDeadAnimalMap(){
        return this.deadAnimalCounter;
    }

    public boolean isRandomMutate() {
        return randomMutate;
    }

    public int getDailyGrassSpawn() {
        return dailyGrassSpawn;
    }

    public int getEnergyForPlant(){
        return energyForPlant;
    }

    public int getStartingEnergy() {
        return startingEnergy;
    }

    public int getEnergyToGiveBirth() {
        return energyToGiveBirth;
    }

    public boolean isStandardAnimalBehaviour() {
        return standardAnimalBehaviour;
    }

    public boolean getStandardGrassRespawn(){
        return this.standardGrassSpawn;
    }

    public static double getPreferredFieldsRatio() {
        return preferredFieldsRatio;
    }

    public boolean isWithPortal(){
        return this.withPortal;
    }

    public double meanAnimalEnergy(Vector2d position){
        List<AbstractWorldMapElement> list = hashMap.get(position);
        return list.stream()
                .filter(o -> o instanceof Animal)
                .mapToDouble(o -> ((Animal) o).getEnergy())
                .average()
                .orElse(0);
    }


    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean canMoveTo(Vector2d position){
        return position.x >= 0 && position.x < this.height;
    }

    public void setPreferredGrassField(List<Vector2d> updatedList){
        this.preferredGrassField = updatedList;
    }
}
