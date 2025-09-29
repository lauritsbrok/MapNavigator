package bfst22.vector.Util;

public interface XYPoint {
    float getXPoint();

    float getYPoint();

    default String getCoordinate() {
        return getXPoint() + ", " + getYPoint();
    };

    default Point getPoint() {
        return new Point(getYPoint(), getYPoint());
    }

    default double haversineDistanceTo(XYPoint o1) {
        return MapMath.haversineDistance(o1.getPoint(), this.getPoint());
    }

}
