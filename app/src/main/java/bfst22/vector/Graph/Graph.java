package bfst22.vector.Graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import bfst22.vector.Util.KdTree;
import bfst22.vector.Util.Point;
import bfst22.vector.gui.PolyLine;

/**
 * This class is responsible for making and storing the graph.
 */

public class Graph implements Serializable {
    public static final long serialVersionUID = 124311;
    // Graph's structure can be changed only applying changes to this class.
    public ArrayList<Edge> edgeList;
    public KdTree<Vertex> vertices;
    public Vertex[] verticesArray;
    public boolean[] visitedArray;
    public HashMap<Point, Vertex> vertexMap;

    /**
     * The constructor creates a new obejct from the class.
     */

    public Graph() {
        this.vertices = new KdTree<>();
        this.edgeList = new ArrayList<>();
        this.vertexMap = new HashMap<>();
    }

    /**
     * This method gives all vertexes id´s and creates a kd tree
     */
    public void optimizeGraph() {
        fillVertexMap();
        createKdTree();
        vertexMap = null;
    }

    /**
     * This method adds edges to a list
     * 
     * @param edge   is the graph edges
     * @param oneway is the parameter that gives a true or false if the way is a one
     *               way road.
     */
    public void addEdge(Edge edge, boolean oneway) {
        edgeList.add(edge);
        if (!oneway) {
            edge = edge.getReversedEdge();
            edgeList.add(edge);
        }
    }

    /**
     * Fills all the existing vertexes' cordinates into a map
     */
    private void fillVertexMap() {
        int id = 0;
        for (Edge edge : edgeList) {
            Vertex from = new Vertex(edge.getFrom()[0], edge.getFrom()[1]);
            Vertex to = new Vertex(edge.getTo()[0], edge.getTo()[1]);
            vertexMap.putIfAbsent(edge.getPointFrom(), from);
            vertexMap.putIfAbsent(edge.getPointTo(), to);
            from = vertexMap.get(edge.getPointFrom());
            to = vertexMap.get(edge.getPointTo());
            if (from.getId() == -1) {
                from.setId(id++);
            }
            edge.setIdFrom(from.getId());
            from.addEdge(edge);
            if (to.getId() == -1) {
                to.setId(id++);
            }
            edge.setIdTo(to.getId());
        }
        verticesArray = new Vertex[vertexMap.values().size()];
        for (Vertex vertex : vertexMap.values()) {
            verticesArray[vertex.getId()] = vertex;
        }
    }

    private void createKdTree() {
        ArrayList<Vertex> vertexList = new ArrayList<>(vertexMap.values());
        vertices = new KdTree<>(vertexList);
    }

    public String getNearestRoadName(float[] point) {
        String output = "UNKNOWN";
        ArrayList<Vertex> v = vertices.nearestNeighborSearch(5, new Vertex(point[0], point[1]));
        if (v != null && v.size() > 0) {
            if (v.get(0).getEdges().size() > 0) {
                output = v.get(0).getEdges().get(0).getDesciption();
            }
        }
        return output;
    }

    public PolyLine getNearestRoadNameLine() {
        return vertices.nearestPoint;
    }

    public Vertex getNearestVertex(float[] point, TransportType type) {
        ArrayList<Vertex> resultList = vertices.nearestNeighborSearch(10, new Vertex(point[0], point[1]));
        for (Vertex v : resultList) {
            if (v.transportIsAllowed(type)) {
                return v;
            }
        }
        return resultList.get(0);
    }

    public Vertex[] getVertices() {
        return verticesArray;
    }

}
