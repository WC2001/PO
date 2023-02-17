package agh.ics.oop;

import java.util.Random;

enum MapDirection{
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW;

    public String toString() {
        return switch (this) {
            case N -> "Północ";
            case NE -> "Północny-wschód";
            case E -> "Wschód";
            case SE -> "Południowy-wschód";
            case S -> "Południe";
            case SW -> "Południowy-zachód";
            case W -> "Zachód";
            case NW -> "Północny-zachód";
        };
    }
    public MapDirection previous(){
        return switch (this) {
            case N -> NW;
            case NW -> W;
            case W -> SW;
            case SW -> S;
            case S -> SE;
            case SE -> E;
            case E -> NE;
            case NE -> N;
        };
    }
    public MapDirection next(){
        return switch (this) {
            case N -> NE;
            case NE -> E;
            case E -> SE;
            case SE -> S;
            case S -> SW;
            case SW -> W;
            case W -> NW;
            case NW -> N;

        };
    }
    public Vector2d toUnitVector(){
        return switch (this) {
            case N -> new Vector2d(-1, 0);
            case NE -> new Vector2d(-1,1);
            case NW -> new Vector2d(-1,-1);
            case S -> new Vector2d(1, 0);
            case SE -> new Vector2d(1,1);
            case SW -> new Vector2d(1,-1);
            case E -> new Vector2d(0, 1);
            case W -> new Vector2d(0, -1);
        };
    }

    public MapDirection opposite(){
        return switch (this) {
            case N -> S;
            case NE -> SW;
            case E -> W;
            case SE -> NW;
            case S -> N;
            case SW -> NE;
            case W -> E;
            case NW -> SE;

        };
    }

    public static MapDirection random(){
        Random rand = new Random();
        MapDirection[] directions = MapDirection.values();
        int randomIndex = rand.nextInt(directions.length);

        return directions[randomIndex];
    }
}
