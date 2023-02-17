package agh.ics.oop;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {
    @Test
    void equalsTest(){
        assertTrue(new Vector2d(1,4).equals(new Vector2d(1,4)));
        assertFalse(new Vector2d(4,5).equals(new Vector2d(4, 4)));
        assertFalse(new Vector2d(1,2).equals("(1,2)"));
    }
    @Test
    void toStringTest(){
        String s = "(3,4)";
        Vector2d v = new Vector2d(1,4);
        String k = "(1,4)";
        assertEquals(v.toString(), k);
        assertNotEquals(v.toString(), s);
    }
    @Test
    void precedesTest(){
        assertTrue(new Vector2d(1,1).precedes(new Vector2d(1,5)));
        assertFalse(new Vector2d(1,2).precedes(new Vector2d(1,0)));


    }
    @Test
    void followsTest(){
        assertTrue(new Vector2d(3,3).follows(new Vector2d(3,0)));
        assertFalse(new Vector2d(3,4).follows(new Vector2d(4,4)));

    }
    @Test
    void upperRightTest(){
        assertEquals(new Vector2d(3,3), new Vector2d(3,1).upperRight(new Vector2d(1,3)));
        assertNotEquals(new Vector2d(3,5), new Vector2d(3, 5).upperRight(new Vector2d(3,7)));

    }
    @Test
    void lowerLeftTest(){
        assertEquals(new Vector2d(5,3), new Vector2d(5,19).lowerLeft(new Vector2d(6,3)));
        assertNotEquals(new Vector2d(5, 13), new Vector2d(5, 13).lowerLeft(new Vector2d(5,4)));
    }
    @Test
    void addTest(){
        assertEquals(new Vector2d(10,3), new Vector2d(11, -1).add(new Vector2d(-1,4)));
        assertNotEquals(new Vector2d(3,3), new Vector2d(2, 0).add(new Vector2d(1, 8)));
    }
    @Test
    void subtractTest(){
        assertEquals(new Vector2d(10,4), new Vector2d(5,6).subtract(new Vector2d(-5,2)));
        assertNotEquals(new Vector2d(4,2), new Vector2d(5,5).subtract(new Vector2d(1,-3)));
    }
    @Test
    void oppositeTest(){
        assertEquals(new Vector2d(1,4).opposite(), new Vector2d(-1,-4));
        assertNotEquals(new Vector2d(4,2), new Vector2d(-4, 0));
    }
}

