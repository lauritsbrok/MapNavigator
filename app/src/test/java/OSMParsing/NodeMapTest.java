package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.NodeMap;
import bfst22.vector.OSMParsing.OSMNode;;

public class NodeMapTest {
    private static NodeMap map;

    @BeforeAll
    public static void setup() {
        map = new NodeMap();
    }

    @AfterAll
    public static void tearDown() {
        map = null;
        Runtime.getRuntime().gc();
    }

    @Test
    void assertSizeTrue() {
        map = new NodeMap();
        assertEquals(0, map.size());
    }

    @Test
    void assertGet() {
        map = new NodeMap();

        OSMNode node = new OSMNode((long) 7, 58.4500f, -99.0000f);
        map.add(node);
        assertNotNull(map.get((long) 7));
    }

    @Deprecated
    @Test
    void assertGetMultipleNodes() {
        map = new NodeMap();

        OSMNode node1 = new OSMNode((long) 5, 58.0000f, -66.0000f);
        OSMNode node2 = new OSMNode((long) 4, -55.0000f, 99.0090f);
        OSMNode node3 = new OSMNode((long) 3, 58.74630f, 21.0340f);
        OSMNode node4 = new OSMNode((long) 2, 9.4500f, -96.0030f);
        OSMNode node5 = new OSMNode((long) 1, 123.0000f, 87.0090f);
        OSMNode node6 = new OSMNode((long) 0, 58.74630f, 34.0340f);

        assertTrue(map.add(node1));
        assertTrue(map.add(node2));
        assertTrue(map.add(node3));
        assertTrue(map.add(node4));
        assertTrue(map.add(node5));
        assertTrue(map.add(node6));

        assertEquals(node5, map.get(1l));
        assertEquals(node5, map.get(1l));
    }

    @Test
    void assertGetReturnsNull() {
        map = new NodeMap();
        OSMNode node1 = new OSMNode((long) 5, 58.0000f, -66.0000f);
        map.add(node1);
        assertEquals(null, map.get(7l));

    }

    @Test
    void assertAddNodesWork() {
        map = new NodeMap();
        OSMNode node1 = new OSMNode((long) 12, 58.0000f, -66.0000f);
        OSMNode node2 = new OSMNode((long) 9, 85.0000f, 99.0000f);
        map.add(node1);
        map.add(node2);
        assertTrue(map.contains(node1));
        assertTrue(map.contains(node2));
    }

}
