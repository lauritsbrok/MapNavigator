package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.Point;
import bfst22.vector.gui.ViewPort;

public class ViewPortTest {

    @Test
    void ConstructorTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(Double.NaN, 100, 200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(100, Double.NaN, 200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(100, 100, Double.NaN, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(100, 100, 200, Double.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(100, 100, -200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ViewPort(100, 100, 200, -200);
        });
    }

    @Test
    void setBoundsTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        viewPort.setBounds(50, 50, 100, 100);
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(Double.NaN, 100, 200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(100, Double.NaN, 200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(100, 100, Double.NaN, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(100, 100, 200, Double.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(100, 100, -200, 200);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            viewPort.setBounds(100, 100, 200, -200);
        });
    }

    @Test
    void containsTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        assertEquals(true, viewPort.contains(new Point(150, 150)));
        assertEquals(false, viewPort.contains(new Point(50, 50)));
        assertEquals(true, viewPort.contains(new OSMNode(150, 150)));
        assertEquals(false, viewPort.contains(new OSMNode(50, 50)));
    }

    @Test
    void sizeTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        assertEquals(100, viewPort.getWidth());
        assertEquals(100, viewPort.getHeigh());
    }

    @Test
    void midPointTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        assertEquals(new Point(150, 150), viewPort.getMidPoint());
    }

    @Test
    void moveTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        viewPort.move(50, 50);
        assertEquals(new Point(200, 200), viewPort.getMidPoint());
    }

    @Test
    void scaleTest() {
        ViewPort viewPort = new ViewPort(100, 100, 200, 200);
        viewPort.scale(2);
        assertEquals(new Point(150, 150), viewPort.getMidPoint());
        assertTrue(viewPort.getHeigh() == 200);
        assertTrue(viewPort.getWidth() == 200);
        viewPort.setBounds(0.007, 0.007, 0.014, 0.014);
        viewPort.scale(-2);
        assertTrue(viewPort.getWidth() == 0.007);
    }
}
