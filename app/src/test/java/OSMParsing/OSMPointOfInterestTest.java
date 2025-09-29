package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMPointOfInterest;

public class OSMPointOfInterestTest {
    @Test
    void classTest() {
        OSMPointOfInterest poi = new OSMPointOfInterest(1.00f, 2.00f, "test", "blue");
        assertTrue(poi.getName().equals("test"));
        assertTrue(poi.getColor().equals("blue"));
        assertTrue(poi.toString().equals("test"));
    }
}
