package bfst22.vector.Util;

public class MapMath {
    // Code taken from
    // https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    public static double haversineDistance(Point from, Point to) {

        // convert from Point2D to lat and lon
        double lat1 = from.getX();
        double lat2 = to.getX();
        double lon1 = from.getY();
        double lon2 = to.getY();

        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    public static float getAngle(Point from, Point to) {
        float angle = (float) Math
                .toDegrees(Math.atan2(from.getY() - to.getY(), from.getX() - to.getX()));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public static double EuclideanDistance(Point from, Point to) {
        double sum = 0;

        double d = from.getX() - to.getX();
        sum += d * d;
        d = from.getY() - to.getY();
        return Math.sqrt(sum);
    }
}
