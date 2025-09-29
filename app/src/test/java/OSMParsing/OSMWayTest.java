package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.OSMParsing.OSMWay;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.WayType;

public class OSMWayTest {
    static OSMWay way;

    @BeforeEach
    void setup() {
        ArrayList<OSMNode> nodes = new ArrayList<>();
        nodes.add(new OSMNode(1, 2));
        nodes.add(new OSMNode(3, 2));
        nodes.add(new OSMNode(5, 2));
        way = new OSMWay(nodes, WayType.HIGHWAY_MOTORWAY);
    }

    @Test
    void constructorTest() {
        OSMWay test = new OSMWay();
        assertTrue(test.getType() == WayType.UNKNOWN);
        test = new OSMWay(11000);
        assertTrue(test.getID() == 11000);
    }

    @Test
    void addNodes() {
        ArrayList<OSMNode> nodes = new ArrayList<>();
        nodes.add(new OSMNode(7, 2));
        nodes.add(new OSMNode(9, 2));
        nodes.add(new OSMNode(11, 2));
        way.addNodes(nodes);
        assertTrue(way.getLastPoint().getX() == 11);
        assertTrue(way.getLastPoint().getY() == 2);
    }

    @Test
    void addNode() {
        way.addNode(new OSMNode(13, 2));
        assertTrue(way.getLastPoint().getX() == 13);
    }

    @Test
    void setType() {
        way.setType(WayType.WATER);
        assertTrue(way.getType() == WayType.WATER);
    }

    @Test
    void setName() {
        way.setName("test");
        assertTrue(way.getName().equals("test"));
    }

    @Test
    void getFirstPoint() {
        assertTrue(way.getFirstPoint().equals(new Point(1, 2)));
    }

    @Test
    void getLastPoint() {
        assertTrue(way.getLastPoint().equals(new Point(5, 2)));
    }

    @Test
    void getFirstNode() {
        assertTrue(way.getFirstNode().equals(new OSMNode(1, 2)));
    }

    @Test
    void getLastNode() {
        assertTrue(way.getLastNode().equals(new OSMNode(5, 2)));
    }

    @Test
    void reverseNodes() {
        way.ReversedNodes();
        assertTrue(way.getFirstNode().equals(new OSMNode(5, 2)));
    }

    @Test
    void getNodes() {
        assertTrue(way.getNodes().size() == 3);
    }
}
