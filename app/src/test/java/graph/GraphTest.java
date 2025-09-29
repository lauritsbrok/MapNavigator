package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.Graph.Edge;
import bfst22.vector.Graph.Graph;
import bfst22.vector.Graph.TransportType;
import bfst22.vector.OSMParsing.OSMNode;

public class GraphTest {
    static Graph graph;

    @BeforeAll
    static void setup() {
        graph = new Graph();
        OSMNode one = new OSMNode(3, 7);
        OSMNode two = new OSMNode(5, 10);
        OSMNode three = new OSMNode(6, 5);
        OSMNode four = new OSMNode(2, 4);
        OSMNode five = new OSMNode(9, 10);
        OSMNode six = new OSMNode(7, 3);
        OSMNode seven = new OSMNode(5, 1);
        OSMNode eight = new OSMNode(10, 5);
        OSMNode nine = new OSMNode(14, 8);
        graph.addEdge(new Edge(one, four, "1-4", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(one, two, "1-2", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(one, three, "1-3", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(two, three, "2-3", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(two, five, "2-5", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(five, nine, "5-9", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(five, three, "5-3", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(nine, eight, "9-8", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(eight, three, "8-3", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(eight, six, "8-6", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(six, seven, "6-7", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(six, three, "6-3", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(seven, four, "7-4", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(four, three, "7-4", (short) 0, true, false, true, false), false);
        graph.optimizeGraph();
    }

    @Test
    void GraphVertexTest() {
        assertEquals(9, graph.getVertices().length);
    }

    @Test
    void getRoadNameTest() {
        assertEquals("1-4", graph.getNearestRoadName(new float[] { 1, 4 }));
    }

    @Test
    void getNearestVertexTest() {
        assertNotNull(graph.getNearestVertex(new float[] { 13, 7 }, TransportType.Car_Route));
    }
}
