package bfst22.vector.Graph;

import java.util.ArrayList;
import java.io.Serializable;
import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.XYPoint;

/**
 * This class is responsible for making the vertex points.
 */

public class Vertex implements XYPoint, Comparable<Vertex>, Serializable {
    public static final long serialVersionUID = 564654;
    float x;
    float y;
    int id;
    ArrayList<Edge> edges;
    double distanceToSource;
    double distanceToGoal;
    Edge pathToSource;

    /**
     * The constructor creates a new obejct from the class.
     * 
     * @param x coordinate
     * @param y coordinate
     */
    public Vertex(float x, float y) {
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
        this.distanceToSource = Float.POSITIVE_INFINITY;
        this.distanceToGoal = 0f;
        this.pathToSource = null;
        this.id = -1;
    }

    public void resetVertex() {
        this.distanceToSource = Float.POSITIVE_INFINITY;
        this.distanceToGoal = 0;
        this.pathToSource = null;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public double getAStarDistance() {
        return distanceToSource + distanceToGoal;
    }

    public double getDistanceToSource() {
        return distanceToSource;
    }

    public void setDistanceToSource(double distanceToSource) {
        this.distanceToSource = distanceToSource;
    }

    public void calculateDistanceToGoal(Vertex goal) {
        distanceToGoal = MapMath.haversineDistance(this.getPoint(), goal.getPoint()) * 1000;
    }

    public void setDistanceToGoal(double calc) {
        distanceToGoal = calc;
    }

    public int getAdjSize() {
        return edges.size();
    }

    @Override
    public float getXPoint() {
        return x;
    }

    @Override
    public float getYPoint() {
        return y;
    }

    @Override
    public String getCoordinate() {
        return x + "," + y;
    }

    @Override
    public int compareTo(Vertex o) {
        if (this.getAStarDistance() > o.getAStarDistance()) {
            return 1;
        } else if (this.getAStarDistance() < o.getAStarDistance()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vertex) {
            if (this.getXPoint() == ((Vertex) o).getXPoint() && this.getYPoint() == ((Vertex) o).getYPoint()) {
                return true;
            }
        }
        return false;
    }

    public Edge getPathToSource() {
        return pathToSource;
    }

    public void setPathToSource(Edge pathToSource) {
        this.pathToSource = pathToSource;
    }

    public Point getPoint() {
        return new Point(this.x, this.y);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean transportIsAllowed(TransportType type) {
        for (Edge edge : edges) {
            if (edge.isTransportAllowed(type)) {
                return edge.isTransportAllowed(type);
            }
        }
        return false;
    }
}
