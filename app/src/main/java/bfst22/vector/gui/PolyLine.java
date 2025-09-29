package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.XYPoint;

/**
 * This class is responsible for creating the lines for ways
 * It keeps track of the coordinates for the ways and the center point of the
 * ways combined.
 */
public class PolyLine implements Drawable, XYPoint, Serializable {
    public static final long serialVersionUID = 40891428;
    public float[] xCoords;
    public float[] yCoords;
    public float middleX;
    public float middleY;

    /**
     * This contrusctor creates a polyline that consits of different nodes.
     * 
     * @param nodes nodes from ways
     */
    public PolyLine(List<OSMNode> nodes) {
        this.xCoords = new float[nodes.size()];
        this.yCoords = new float[nodes.size()];
        int i = 0;
        for (XYPoint node : nodes) {
            this.xCoords[i] = node.getXPoint();
            this.yCoords[i] = node.getYPoint();
            i++;
        }
        int middle = xCoords.length / 2;
        this.middleX = xCoords[middle];
        this.middleY = yCoords[middle];
    }

    /**
     * This constructor creates a line, where we can call it manually with two
     * custom points.
     * It's used when creating a line fromt the cursor to the nearest point.
     */
    public PolyLine(float firstX, float firstY, float secondX, float secondY) {
        this.xCoords = new float[2];
        this.yCoords = new float[2];
        this.xCoords[0] = firstX;
        this.yCoords[0] = firstY;
        this.xCoords[1] = secondX;
        this.yCoords[1] = secondY;
    }

    public float[] getXCoords() {
        return xCoords;
    }

    public float[] getYCoords() {
        return yCoords;
    }

    /**
     * Traces the lines which can then be filled or drawn
     */
    public void trace(GraphicsContext gc) {
        if (xCoords != null && xCoords.length > 0) {
            gc.moveTo(xCoords[0], yCoords[0]);
            for (int i = 1; i < xCoords.length; i += 1) {
                gc.lineTo(xCoords[i], yCoords[i]);
            }
        }
    }

    public float lastX() {
        return xCoords[xCoords.length - 1];
    }

    public float lastY() {
        return yCoords[yCoords.length - 1];
    }

    public float firstX() {
        return xCoords[0];
    }

    public float firstY() {
        return yCoords[0];
    }

    @Override
    public float getXPoint() {
        return middleX;
    }

    @Override
    public float getYPoint() {
        return middleY;
    }

    @Override
    public String getCoordinate() {
        return middleX + " , " + middleY;
    }

    public String getEndCoordinate() {
        return xCoords[xCoords.length - 1] + " , " + yCoords[yCoords.length - 1];
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(middleX);
        result = prime * result + Float.floatToIntBits(middleY);
        result = prime * result + Arrays.hashCode(xCoords);
        result = prime * result + Arrays.hashCode(yCoords);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PolyLine other = (PolyLine) obj;
        if (Float.floatToIntBits(middleX) != Float.floatToIntBits(other.middleX))
            return false;
        if (Float.floatToIntBits(middleY) != Float.floatToIntBits(other.middleY))
            return false;
        if (!Arrays.equals(xCoords, other.xCoords))
            return false;
        if (!Arrays.equals(yCoords, other.yCoords))
            return false;
        return true;
    }
}