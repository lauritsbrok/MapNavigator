package bfst22.vector.OSMParsing;

import java.io.Serializable;

import bfst22.vector.Util.WayType;
import bfst22.vector.Util.XYPoint;
import javafx.geometry.BoundingBox;

@SuppressWarnings("unused")
public class OSMNode extends OSMItem implements XYPoint, Serializable {
    private float lat;
    private float lon;

    OSMNode() {

    }

    public OSMNode(float lon, float lat) {
        super();
        this.lon = lon;
        this.lat = lat;
    }

    public OSMNode(long id, float lon, float lat) {
        super(id);
        this.lon = lon;
        this.lat = lat;
    }

    public float getLatitude() {
        return lat;
    }

    public float getLongitude() {
        return lon;
    }

    public void setLatitude(float lat) {
        this.lat = lat;
    }

    public void setLongitude(float lon) {
        this.lon = lon;
    }

    public String getCoordinate() {
        return lon + "," + lat;
    }

    @Override
    public float getXPoint() {
        return lon;
    }

    @Override
    public float getYPoint() {
        return lat;
    }

    public OSMNode getnode() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OSMNode) {
            OSMNode temp = (OSMNode) obj;
            if (temp.getXPoint() == this.getXPoint() && temp.getYPoint() == this.getYPoint()) {
                return true;
            }
        }
        return false;
    }

}