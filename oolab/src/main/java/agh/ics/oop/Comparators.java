package agh.ics.oop;

import java.util.Comparator;

public class Comparators {

    static Comparator<AbstractWorldMapElement> compareX = new Comparator<>() {
        @Override
        public int compare(AbstractWorldMapElement o1, AbstractWorldMapElement o2) {

            Vector2d v1 = o1.getPosition();
            Vector2d v2 = o2.getPosition();
            if(v1.x < v2.x)
                return -1;

            if(v1.x > v2.x)
                return 1;

            if(v1.y < v2.y)
                return -1;

            if(v1.y > v2.y)
                return 1;

            return 0;
        }
    };

    static Comparator<AbstractWorldMapElement> compareY = new Comparator<>() {
        @Override
        public int compare(AbstractWorldMapElement o1, AbstractWorldMapElement o2) {

            Vector2d v1 = o1.getPosition();
            Vector2d v2 = o2.getPosition();

            if(v1.y < v2.y)
                return -1;
            if(v1.y > v2.y)
                return 1;
            if(v1.x < v2.x)
                return -1;
            if(v1.x > v2.x)
                return 1;
            return 0;
        }
    };
}

