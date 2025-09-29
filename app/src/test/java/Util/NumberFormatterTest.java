package Util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import bfst22.vector.Util.NumberFormatter;

public class NumberFormatterTest {
    @Test
    void formatDecimalTest() {
        assertEquals("1,124", NumberFormatter.formatDecimal(3, 1.12392412f));
        assertEquals("10", NumberFormatter.formatDecimal(0, 9.999f));
        assertEquals("10,0", NumberFormatter.formatDecimal(1, 9.999f));
    }

    @Test
    void formatDistanceTest() {
        assertEquals("131 m", NumberFormatter.formatDistance(0.131f));
        assertEquals("132 m", NumberFormatter.formatDistance(0.1319f));
        assertEquals("1,14 km", NumberFormatter.formatDistance(1.139f));
        assertEquals("2,00 km", NumberFormatter.formatDistance(1.999));
    }

    @Test
    @SuppressWarnings("unused")
    void NumberFormatterClassTest() {
        NumberFormatter numberFormatter = new NumberFormatter();
    }
}
