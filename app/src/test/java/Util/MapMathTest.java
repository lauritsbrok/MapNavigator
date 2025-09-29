package Util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.Point;

public class MapMathTest {

    @BeforeAll
    public static void setup() {
    }

    @Test
    void testMapMath() {
        MapMath map = new MapMath();

        assertNotNull(map);
    }

    @Test
    void calculateDistanceForEachPathTest() {
        double distance = MapMath.haversineDistance(new Point(8.222, 55.098), new Point(8.485, 55.098));

        assertEquals(Math.round(distance), 29);
    }

    @Test
    void testAngles() {
        float angles = MapMath.getAngle(new Point(8.222, 55.098), new Point(8.485, 55.650));

        assertEquals(Math.round(angles), 245);
    }

}
