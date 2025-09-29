package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bfst22.vector.Graph.Edge;
import bfst22.vector.Graph.Graph;
import bfst22.vector.Graph.Route;
import bfst22.vector.Graph.RouteFinder;
import bfst22.vector.Graph.TransportType;
import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.Point;

public class routeFinderTest {
    static Graph graph;

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
        OSMNode nine = new OSMNode(14, 10);
        graph.addEdge(new Edge(one, four, "1-4", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(one, two, "1-2", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(one, three, "1-3", (short) 130, true, false, false, false), false);
        graph.addEdge(new Edge(two, three, "2-3", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(two, five, "2-5", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(five, nine, "5-9", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(five, three, "5-3", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(nine, eight, "9-8", (short) 0, true, false, true, false), false);
        graph.addEdge(new Edge(eight, three, "8-3", (short) 130, true, false, true, false), false);
        graph.addEdge(new Edge(eight, six, "8-6", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(six, seven, "6-7", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(six, three, "6-3", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(seven, four, "7-4", (short) 0, true, false, false, false), false);
        graph.addEdge(new Edge(three, four, "3-4", (short) 0, true, false, true, false), false);
        graph.optimizeGraph();
    }

    @Test
    void DjikstraTest() {
        setup();
        RouteFinder routeFinder = new RouteFinder(graph);
        Route route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), false, TransportType.Car_Route,
                true);
        String resultt = "[" + route.getPath().get(0).getFrom()[0] + ", " + route.getPath().get(0).getFrom()[1] + "]";
        for (int i = 0; i < route.getPath().size(); i++) {
            resultt += ", [" + route.getPath().get(i).getTo()[0] + ", " + route.getPath().get(i).getTo()[1] + "]";
        }
        assertEquals("[3.0, 7.0], [5.0, 10.0], [9.0, 10.0], [14.0, 10.0]", resultt);
        routeFinder = new RouteFinder(graph);
        route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), false, TransportType.Walk_Route,
                true);
        resultt = "[" + route.getPath().get(0).getFrom()[0] + ", " + route.getPath().get(0).getFrom()[1] + "]";
        for (int i = 0; i < route.getPath().size(); i++) {
            resultt += ", [" + route.getPath().get(i).getTo()[0] + ", " + route.getPath().get(i).getTo()[1] + "]";
        }
        assertEquals("[3.0, 7.0], [2.0, 4.0], [6.0, 5.0], [10.0, 5.0], [14.0, 10.0]", resultt);
        route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), false, TransportType.Bike_Route,
                true);
        assertTrue(route == null);
    }

    @Test
    void AstarTest() {
        setup();
        RouteFinder routeFinder = new RouteFinder(graph);
        Route route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), true, TransportType.Car_Route, true);
        String resultt = "[" + route.getPath().get(0).getFrom()[0] + ", " + route.getPath().get(0).getFrom()[1] + "]";
        for (int i = 0; i < route.getPath().size(); i++) {
            resultt += ", [" + route.getPath().get(i).getTo()[0] + ", " + route.getPath().get(i).getTo()[1] + "]";
        }
        assertEquals("[3.0, 7.0], [5.0, 10.0], [9.0, 10.0], [14.0, 10.0]", resultt);
        routeFinder = new RouteFinder(graph);
        route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), true, TransportType.Walk_Route,
                true);
        resultt = "[" + route.getPath().get(0).getFrom()[0] + ", " + route.getPath().get(0).getFrom()[1] + "]";
        for (int i = 0; i < route.getPath().size(); i++) {
            resultt += ", [" + route.getPath().get(i).getTo()[0] + ", " + route.getPath().get(i).getTo()[1] + "]";
        }
        assertEquals("[3.0, 7.0], [2.0, 4.0], [6.0, 5.0], [10.0, 5.0], [14.0, 10.0]", resultt);
        routeFinder = new RouteFinder(graph);
        route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), true, TransportType.Car_Route,
                false);
        resultt = "[" + route.getPath().get(0).getFrom()[0] + ", " + route.getPath().get(0).getFrom()[1] + "]";
        for (int i = 0; i < route.getPath().size(); i++) {
            resultt += ", [" + route.getPath().get(i).getTo()[0] + ", " + route.getPath().get(i).getTo()[1] + "]";
        }
        assertEquals("[3.0, 7.0], [6.0, 5.0], [10.0, 5.0], [14.0, 10.0]", resultt);
        routeFinder = new RouteFinder(graph);
        route = routeFinder.getRoute(new Point(3, 7), new Point(14, 10), true, TransportType.Bike_Route,
                true);
        assertTrue(route == null);
    }
}
