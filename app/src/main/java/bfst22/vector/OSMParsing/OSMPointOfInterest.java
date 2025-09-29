package bfst22.vector.OSMParsing;

public class OSMPointOfInterest extends OSMNode {

    private String name;
    private String color;

    public OSMPointOfInterest(float lon, float lat, String name, String color) {
        super(lon, lat);
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name;
    }

}
