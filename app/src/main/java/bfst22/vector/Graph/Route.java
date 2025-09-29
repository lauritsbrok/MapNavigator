package bfst22.vector.Graph;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;

import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.Point;
import bfst22.vector.gui.Drawable;
import javafx.scene.canvas.GraphicsContext;

/**
 * This class is responsible for creating the route between to points.
 */

public class Route implements Drawable {
    private ArrayList<Edge> path;
    private ArrayList<Edge> analyzedPath;
    private RouteDescription routeDesciption;
    private double[] distance;
    private double totalDistance;
    private Vertex[] vertices;
    private Point destination;
    private TransportType transport;
    private int avgSpeed;

    /**
     * The constructor creates a new obejct from the class.
     * 
     * @param path         The final path
     * @param analyzedPath is all the path that is visited to find the optimal path
     * @param vertices     is the points that the path goes through
     * @param to           where the path is going
     * @param transport    the transport in use
     */

    public Route(ArrayList<Edge> path, ArrayList<Edge> analyzedPath, Vertex[] vertices, float[] to,
            TransportType transport) {
        Collections.reverse(path);
        this.distance = new double[path.size()];
        this.totalDistance = 0;
        this.path = path;
        this.analyzedPath = analyzedPath;
        this.vertices = vertices;
        this.transport = transport;
        this.destination = new Point(to[0], to[1]);
        calculateDistanceForEachPath();
        getRouteDescription();
    }

    /**
     * Caltulated total distance of the route
     */
    public void calculateDistanceForEachPath() {
        for (int i = 0; i < distance.length; i++) {
            distance[i] = MapMath.haversineDistance(path.get(i).getPointFrom(), path.get(i).getPointTo());
            totalDistance += distance[i];
        }
    }

    /**
     * Caltulated avg speed on route
     */
    private void calculateAvgSpeed() {
        switch (transport) {
            case Bike_Route:
                avgSpeed = 20;
                break;
            case Car_Route:
                int i = 0;
                int totalSpeed = 0;
                for (Edge edge : path) {
                    if (edge.getSpeed() > 0) {
                        i++;
                        totalSpeed += edge.getSpeed();
                    }
                }
                avgSpeed = totalSpeed / i;
                break;
            case Walk_Route:
                avgSpeed = 6;
                break;
            default:
                break;
        }
    }

    /**
     * Caltulated estimated travel time
     */
    public String getTravelTime() {
        calculateAvgSpeed();
        int time = (int) Math.ceil(totalDistance * 60 / avgSpeed);
        int hours = time / 60;
        int minutes = time % 60;
        String minutesString = "";
        if (minutes < 10) {
            minutesString += "0";
        }
        minutesString += minutes + "";
        return hours + ":" + minutesString;
    }

    public void getRouteDescription() {
        if (path.size() <= 0) {
            return;
        }
        Edge firstPath = path.get(0);
        routeDesciption = new RouteDescription(MapMath.getAngle(firstPath.getPointFrom(),
                firstPath.getPointTo()),
                firstPath.getDesciption());
        Edge lastEdge = path.get(0);
        for (int i = 0; i < distance.length; i++) {
            if (!path.get(i).isRoundAbout()) {
                if (lastEdge.isRoundAbout()) {
                    routeDesciption.directionRoundAbout(path.get(i).getDesciption());
                }
                if (i + 1 < distance.length) {
                    if (vertices[path.get(i).getIdTo()].getAdjSize() > 2) {
                        float oldHeading = MapMath.getAngle(path.get(i).getPointFrom(), path.get(i).getPointTo());
                        float newHeading = MapMath.getAngle(path.get(i + 1).getPointFrom(),
                                path.get(i + 1).getPointTo());
                        routeDesciption.changeHeading(newHeading, oldHeading, path.get(i).getDesciption(),
                                path.get(i + 1).getDesciption(),
                                distance[i]);
                    } else {
                        routeDesciption.addDistanceToLastDirection(distance[i]);
                    }
                } else {
                    routeDesciption.addDistanceToLastDirection(distance[i]);
                }
            } else {
                if (vertices[path.get(i).getIdTo()].getAdjSize() > 1) {
                    routeDesciption.addRoundAboutExit();
                }
            }
            lastEdge = path.get(i);
        }
        float intialHeading = MapMath.getAngle(path.get(path.size() - 1).getPointFrom(),
                path.get(path.size() - 1).getPointTo());
        float headingToDestination = MapMath.getAngle(path.get(path.size() - 1).getPointTo(), destination);
        routeDesciption.setFinalDirection(intialHeading, headingToDestination);
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public ArrayList<String> getDirectionsString() {
        return routeDesciption.getDirectionsString();
    }

    @Override
    public void trace(GraphicsContext gc) {
        for (Edge edge : path) {
            edge.trace(gc);
        }

    }

    /**
     * Draws which area has been analysed, to conclude the optimal route.
     * 
     * @param gc Is the graphic context.
     */
    public void drawAnalysedArea(GraphicsContext gc) {
        gc.beginPath();
        for (Edge edge : analyzedPath) {
            edge.trace(gc);
        }
        gc.stroke();
    }

    public ArrayList<Edge> getPath() {
        return path;
    }
}
