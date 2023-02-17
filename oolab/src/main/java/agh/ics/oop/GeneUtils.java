package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneUtils {

    ArrayList<Integer> randomGenes(int genesLength){
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < genesLength; i++) {
            list.add((int)(Math.floor(Math.random()*genesLength)));
        }
        return list;
    }

    public ArrayList<Integer> generateGenes (Animal a1, Animal a2, int genesLength, boolean randomMutate){
        int proportion = (int) Math.floor((float)(a1.getEnergy()*genesLength)/(a2.getEnergy()+a1.getEnergy()));
        ArrayList<Integer> genes = new ArrayList<>();
        if(Math.random()*2 < 1){
            genes.addAll(a1.getGenes().subList(0, proportion));
            genes.addAll(a2.getGenes().subList(proportion, genesLength));
        }else{
            genes.addAll(a2.getGenes().subList(0, proportion));
            genes.addAll(a1.getGenes().subList(proportion, genesLength));
        }
        int changing = (int)(Math.random()*genesLength);

        List<Integer> subsetIndexes = new ArrayList<>();

        Random rand = new Random();

        while(subsetIndexes.size() < changing){
            int randomIndex = rand.nextInt(genesLength);
            if(!subsetIndexes.contains(randomIndex)){
                subsetIndexes.add(randomIndex);
            }
        }
        for (Integer index: subsetIndexes) {
            int increase = rand.nextInt(2) % 2 == 0 ? -1 : 1;
            genes.set(index, randomMutate? rand.nextInt(8) : (genes.get(index)+increase)%8);
        }

        return genes;
    }
}
