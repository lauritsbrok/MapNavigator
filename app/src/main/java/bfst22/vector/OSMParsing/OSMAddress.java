package bfst22.vector.OSMParsing;

import java.io.Serializable;

import bfst22.vector.Util.XYPoint;

/**
 * This class is responsible for creating objects of OSMAddresses when parsed
 * from the OSM file
 */
public class OSMAddress implements Serializable {
    public static final long serialVersionUID = 451541;
    private String city;
    private String street;
    private String housenumber;
    private String postcode;

    private float xPoint, yPoint;

    public OSMAddress() {
        super();
    }

    public OSMAddress(Float lon, float lat) {
        this.xPoint = lon;
        this.yPoint = lat;
    }

    public OSMAddress(float lon, float lat, String city, String street, String housenumber,
            String postcode) {
        this.xPoint = lon;
        this.yPoint = lat;
        this.city = city;
        this.street = street;
        this.housenumber = housenumber;
        this.postcode = postcode;
    }

    public void setCoords(XYPoint node) {
        this.xPoint = node.getXPoint();
        this.yPoint = node.getYPoint();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHousenumber(String housenumber) {
        this.housenumber = housenumber;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHousenumber() {
        return housenumber;
    }

    public String getPostcode() {
        return postcode;
    }

    // + " # " + getXPoint() + " " + getYPoint()
    public String toString() {
        return this.street + " " + this.housenumber + ", " + this.postcode + " " +
                this.city;
    }

    public String getCoordinate() {
        return xPoint + ", " + yPoint;
    }
}
