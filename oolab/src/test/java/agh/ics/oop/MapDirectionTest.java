package agh.ics.oop;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static agh.ics.oop.MapDirection.N;
import static agh.ics.oop.MapDirection.NE;
import static agh.ics.oop.MapDirection.E;
import static agh.ics.oop.MapDirection.SE;
import static agh.ics.oop.MapDirection.S;
import static agh.ics.oop.MapDirection.SW;
import static agh.ics.oop.MapDirection.W;
import static agh.ics.oop.MapDirection.NW;


public class MapDirectionTest {

    @Test
    void nextTest(){
        assertEquals(N.next(), NE);
        assertEquals(NE.next(), E);
        assertEquals(E.next(), SE);
        assertEquals(SE.next(), S);
        assertEquals(S.next(), SW);
        assertEquals(SW.next(), W);
        assertEquals(W.next(), NW);
        assertEquals(NW.next(), N);
    }

    @Test
    void previousTest(){
        assertEquals(N.previous(), NW);
        assertEquals(NW.previous(), W);
        assertEquals(W.previous(), SW);
        assertEquals(SW.previous(), S);
        assertEquals(S.previous(), SE);
        assertEquals(SE.previous(), E);
        assertEquals(E.previous(), NE);
        assertEquals(NE.previous(), N);
    }

}

