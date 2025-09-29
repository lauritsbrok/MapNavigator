package bfst22.vector.Graph;

import java.util.ArrayList;

import bfst22.vector.Util.IndexMinPQ;
import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.Point;

/**
 * This class is the route finder. Finds the route on the graph.
 */

public class RouteFinder {
    public ArrayList<Edge> analyzedPath;
    private IndexMinPQ<Vertex> queue;
    private Graph graph;
    public boolean[] visitedArray;

    /**
     * Initialyses RouteFinder.
     * 
     * @param graph The graph, that the route is found upon.
     */
    public RouteFinder(Graph graph) {
        this.graph = graph;
        this.analyzedPath = new ArrayList<>();
        queue = new IndexMinPQ<>(graph.getVertices().length);
        visitedArray = new boolean[graph.getVertices().length];
    }

    /**
     * This get route gets a point to get the route from.
     * 
     * @param from          Startpoint
     * @param to            Endpoint
     * @param includeAStar  If A star sould be included
     * @param transport     is the type of transport, the user wishes to use
     * @param shortestRoute is the shortest route
     * @return Returns the route
     */
    public Route getRoute(Point from, Point to, boolean includeAStar, TransportType transport,
            boolean shortestRoute) {
        return getRoute(getFloatArrayFromPoint(from), getFloatArrayFromPoint(to), includeAStar, transport,
                shortestRoute);
    }

    private static float[] getFloatArrayFromPoint(Point point) {
        return new float[] { point.getX(), point.getY() };
    }

    /**
     * This get route get a float array to get the route from
     * 
     * @param from          Startpoint
     * @param to            Endpoint
     * @param includeAStar  If A star sould be included
     * @param transport     is the type of transport, the user wishes to use
     * @param shortestRoute is the shortest route
     * @return Returns the route
     */
    public Route getRoute(float[] from, float[] to, boolean includeAStar, TransportType transport,
            boolean shortestRoute) {
        Vertex start = graph.getNearestVertex(from, transport);
        Vertex end = graph.getNearestVertex(to, transport);
        boolean solutionFound = false;
        start.setDistanceToSource(0f);
        if (includeAStar) {
            start.setDistanceToGoal(Astarweight(start, end, -1, transport));
        }
        queue.insert(start.getId(), start);
        while (!queue.isEmpty() && !solutionFound) {
            Vertex temp = graph.getVertices()[queue.delMin()];
            if (temp == end) {
                solutionFound = true;
                break;
            } else {
                getAndRelaxNeighbours(temp, end, includeAStar, transport, shortestRoute);
                visitedArray[temp.getId()] = true;
            }
            if (queue.isEmpty() && !solutionFound) {
                return null;
            }
        }
        Vertex first = end;
        ArrayList<Edge> path = new ArrayList<>();
        ArrayList<Vertex> verticesInPath = new ArrayList<>();
        while (first.getPathToSource() != null && first != null) {
            path.add(first.getPathToSource());
            verticesInPath.add(first);
            first = graph.getVertices()[first.getPathToSource().getIdFrom()];
            if (first == start) {
                break;
            }
        }
        // System.out.println(System.nanoTime() - startTime);
        return new Route(path, analyzedPath, graph.getVertices(), to, transport);
    }

    public boolean isAllowed(Edge edge, TransportType transport) {
        switch (transport) {
            case Car_Route:
                return edge.isCarAllowed();
            case Bike_Route:
                return edge.isBikeAllowed();
            case Walk_Route:
                return edge.isWalkAllowed();
        }
        return false;
    }

    public void getAndRelaxNeighbours(Vertex vertex, Vertex goal, boolean AStar, TransportType transport,
            boolean shortestRoute) {
        if (vertex.getPathToSource() != null) {
            analyzedPath.add(vertex.getPathToSource());
        }
        ArrayList<Edge> edges = vertex.getEdges();
        for (Edge edge : edges) {
            if (!isAllowed(edge, transport)) {
                continue;
            }
            Vertex neighbor = graph.getVertices()[edge.getIdTo()];
            if (!visitedArray[neighbor.getId()]) {
                neighbor.resetVertex();
                if (AStar) {
                    neighbor.setDistanceToGoal(Astarweight(neighbor, goal, -1, transport));
                }
                if (transport == TransportType.Car_Route && !shortestRoute && AStar) {
                    relaxNeighboursWithSpeed(vertex, goal, transport, edge, neighbor);
                } else {
                    relaxNeighbours(vertex, edge, neighbor);
                }
                if (!queue.contains(neighbor.getId())) {
                    queue.insert(neighbor.getId(), neighbor);
                } else {
                    queue.changeKey(neighbor.getId(), neighbor);
                }
            }
            analyzedPath.add(edge);
        }
    }

    /**
     * This method is a part of dijkstra's algorith, and is used to calculate the
     * shortest path
     * 
     * @param vertex   is the points used to calculate shortest path
     * @param edge     is the lines between vertexes
     * @param neighbor is the closest vertex.
     */

    public void relaxNeighbours(Vertex vertex, Edge edge, Vertex neighbor) {
        if (vertex.getDistanceToSource() + edge.getWeight() < neighbor.getDistanceToSource()) {
            neighbor.setDistanceToSource(vertex.getDistanceToSource() + edge.getWeight());
            neighbor.setPathToSource(edge);
        }
    }

    /**
     * This method is a shortest path aswell, but it takes the speed limits into
     * account.
     * 
     * @param vertex
     * @param edge
     * @param neighbor
     */
    public void relaxNeighboursWithSpeed(Vertex vertex, Vertex goal, TransportType transport,
            Edge edge, Vertex neighbor) {
        if (vertex.getDistanceToSource() + edge.getWeightWithSpeed() < neighbor.getDistanceToSource()) {
            neighbor.setDistanceToSource(
                    vertex.getDistanceToSource()
                            + Astarweight(neighbor, goal, edge.getSpeed(), transport) + edge.getWeightWithSpeed());
            neighbor.setPathToSource(edge);
        }
    }

    private double Astarweight(Vertex start, Vertex goal, int maxSpeed, TransportType transport) {
        double distance = MapMath.haversineDistance(start.getPoint(), goal.getPoint()) * 1000;
        double speed = 1;
        if (maxSpeed < 1) {
            maxSpeed = 1;
        }
        switch (transport) {
            case Car_Route:
                speed = maxSpeed;
                break;
            default:
                speed = 1;
                break;
        }
        return distance / speed;
    }
}
