package bfst22.vector.Util;

import javafx.geometry.Point2D;

public class Point {
    float x, y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point(Point2D point) {
        this.x = (float) point.getX();
        this.y = (float) point.getY();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point temp = (Point) obj;
            if (temp.getX() == this.x && temp.getY() == this.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(x) + Float.floatToIntBits(y);
    }
}
