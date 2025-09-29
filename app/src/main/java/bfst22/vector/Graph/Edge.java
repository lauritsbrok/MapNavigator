package bfst22.vector.Graph;

import javafx.scene.canvas.GraphicsContext;
import java.io.Serializable;
import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.Drawable;

/**
 * This class discripes the edges of the graph
 */
public class Edge implements XYPoint, Drawable, Serializable {
    public static final long serialVersionUID = 313131;

    private float xFrom;
    private float yFrom;
    private float xTo;
    private float yTo;
    private int idFrom;
    private int idTo;
    private String desciption;
    private short speed;
    private boolean carAllowed;
    private boolean bikeAllowed;
    private boolean walkAllowed;
    private boolean roundabout;

    /**
     * The constructor creates a new obejct from the class.
     * 
     * @param from        is the startpoint of the edge.
     * @param to          endpoint of the edge.
     * @param desciption  desctipton of the path.
     * @param speed       is the speed allowed to drive on the road.
     * @param carAllowed  if it is allowed to drive a car.
     * @param bikeAllowed if it is allowed to drive a bike.
     * @param walkAllowed if it is allowed to walk on the roead.
     * @param roundabout  if there is a roundabout.
     */

    public Edge(OSMNode from, OSMNode to, String desciption, short speed, boolean carAllowed, boolean bikeAllowed,
            boolean walkAllowed, boolean roundabout) {
        this.idFrom = -1;
        this.idTo = -1;
        this.xFrom = from.getLongitude();
        this.yFrom = from.getLatitude();
        this.xTo = to.getLongitude();
        this.yTo = to.getLatitude();
        this.speed = speed;
        this.desciption = desciption;
        this.carAllowed = carAllowed;
        this.bikeAllowed = bikeAllowed;
        this.walkAllowed = walkAllowed;
        this.roundabout = roundabout;
    }

    public int getIdTo() {
        return idTo;
    }

    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }

    public int getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    /**
     * Same as the other constructor, but this one takes a float array instead of a
     * OSM node.
     * 
     * @param from        is the startpoint of the edge.
     * @param to          endpoint of the edge.
     * @param desciption  desctipton of the path.
     * @param speed       is the speed allowed to drive on the road.
     * @param carAllowed  if it is allowed to drive a car.
     * @param bikeAllowed if it is allowed to drive a bike.
     * @param walkAllowed if it is allowed to walk on the roead.
     * @param roundabout  if there is a roundabout.
     */
    public Edge(float[] from, float[] to, String desciption, short speed, boolean carAllowed, boolean bikeAllowed,
            boolean walkAllowed, boolean roundabout) {
        this.idFrom = -1;
        this.idTo = -1;
        this.xFrom = from[0];
        this.yFrom = from[1];
        this.xTo = to[0];
        this.yTo = to[1];
        this.desciption = desciption;
        this.speed = speed;
        this.carAllowed = carAllowed;
        this.bikeAllowed = bikeAllowed;
        this.walkAllowed = walkAllowed;
        this.roundabout = roundabout;
    }

    /**
     * Calculates the distance from to points
     * 
     * @return the distance
     */
    private double calculateDistance() {
        return MapMath.haversineDistance(new Point(xFrom, yFrom), new Point(xTo, yTo)) * 1000;
    }

    public double getWeight() {
        return calculateDistance();
    }

    public double getWeightWithSpeed() {
        if (speed == 0) {
            return calculateDistance();
        }
        return calculateDistance() / speed;
    }

    public double getDistance() {
        return calculateDistance();
    }

    /**
     * Gives a true or false on whether bike, car or walking is allowed.
     * 
     * @param type is the type of transport
     * @return true or false on whether bike, car or walking is allowed.
     */
    public boolean isTransportAllowed(TransportType type) {
        switch (type) {
            case Bike_Route:
                return bikeAllowed;
            case Car_Route:
                return carAllowed;
            case Walk_Route:
                return walkAllowed;
        }
        return false;
    }

    public float[] getFrom() {
        return new float[] { xFrom, yFrom };
    }

    public float[] getTo() {
        return new float[] { xTo, yTo };
    }

    @Override
    public float getXPoint() {
        return xFrom;
    }

    @Override
    public float getYPoint() {
        return yFrom;
    }

    public Point getPointFrom() {
        return new Point(xFrom, yFrom);
    }

    public Point getPointTo() {
        return new Point(xTo, yTo);
    }

    public String getCoordinateTo() {
        return xTo + ", " + yTo;
    }

    @Override
    public String getCoordinate() {
        return xFrom + ", " + yFrom;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public String toString() {
        return this.getDesciption() + ": " + this.getCoordinate() + " -----> " + this.getTo()[0] + ", "
                + this.getTo()[1];
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.moveTo(xFrom, yFrom);
        gc.lineTo(xTo, yTo);
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public boolean isCarAllowed() {
        return carAllowed;
    }

    public void setCarAllowed(boolean carAllowed) {
        this.carAllowed = carAllowed;
    }

    public boolean isBikeAllowed() {
        return bikeAllowed;
    }

    public void setBikeAllowed(boolean bikeAllowed) {
        this.bikeAllowed = bikeAllowed;
    }

    public boolean isWalkAllowed() {
        return walkAllowed;
    }

    public void setWalkAllowed(boolean walkAllowed) {
        this.walkAllowed = walkAllowed;
    }

    /**
     * This method is used if the path that needs to be calculated meets a
     * roundabout, and the route wants to go the wrong way.
     * This method makes sure that the path can go the wrong way.
     * 
     * @return a different route to the original, if the original meets any
     *         restrictions, like a roundabout
     */
    public Edge getReversedEdge() {
        return new Edge(this.getTo(), this.getFrom(), this.getDesciption(), this.getSpeed(),
                this.isCarAllowed(), this.isBikeAllowed(), this.isWalkAllowed(), this.isRoundAbout());
    }

    boolean isRoundAbout() {
        return roundabout;
    }
}
