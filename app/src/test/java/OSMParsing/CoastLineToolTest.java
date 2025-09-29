package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.CoastLineTool;
import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.OSMParsing.OSMWay;
import bfst22.vector.Util.Point;

public class CoastLineToolTest {
    static HashMap<Point, OSMWay> coastLineMap;

    @BeforeAll
    static void setup() {
        coastLineMap = new HashMap<>();
    }

    @Test
    void connectCoastLineTest() {
        CoastLineTool connectCoastLine = new CoastLineTool();
        assertNotNull(connectCoastLine);
        List<OSMNode> nodes = new ArrayList<>();
        nodes.add(new OSMNode(1, 2));
        nodes.add(new OSMNode(4, 2));
        OSMWay firstUnconnected = new OSMWay();
        firstUnconnected.addNodes(nodes);
        CoastLineTool.connectCoastLine(coastLineMap, firstUnconnected);
        assertEquals(2, coastLineMap.keySet().size());
        nodes.clear();
        nodes.add(new OSMNode(4, 1));
        nodes.add(new OSMNode(1, 1));
        OSMWay secondUnconnected = new OSMWay();
        secondUnconnected.addNodes(nodes);
        CoastLineTool.connectCoastLine(coastLineMap, secondUnconnected);
        assertEquals(4, coastLineMap.keySet().size());
        nodes.clear();
        nodes.add(new OSMNode(4, 2));
        nodes.add(new OSMNode(4, 1));
        OSMWay thirdUnconnected = new OSMWay();
        thirdUnconnected.addNodes(nodes);
        CoastLineTool.connectCoastLine(coastLineMap, thirdUnconnected);
        assertEquals(2, coastLineMap.keySet().size());
    }
}
