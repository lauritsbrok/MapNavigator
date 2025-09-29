package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.OSMParsing.OSMRelation;
import bfst22.vector.OSMParsing.OSMWay;
import bfst22.vector.Util.XYPoint;

/**
 * This class is reponsible for create a multipolygon for relations, drawn or
 * filled.
 */
public class MultiPolygon implements Drawable, XYPoint, Serializable {
    public static final long serialVersionUID = 40891422;
    List<Drawable> parts;
    float avgOuterX;
    float avgOuterY;
    float xmax, ymax, xmin, ymin;

    /**
     * Creates a polygon from a relation which is parsed from the OSM file.
     * 
     * @param tempRelation the OSM relation
     */
    public MultiPolygon(OSMRelation tempRelation) {
        tempRelation.correctOuters();
        this.parts = new ArrayList<Drawable>();
        for (OSMWay way : tempRelation.inners) {
            this.parts.add(new PolyLine(way.nodes));
        }
        for (OSMWay way : tempRelation.outers) {
            this.parts.add(new PolyLine(way.nodes));
        }
        getAvgXYinWays(tempRelation.outers);
    }

    /**
     * Traces the parts into a polygon
     */
    public void trace(GraphicsContext gc) {
        for (Drawable part : parts) {
            part.trace(gc);
        }
    }

    /**
     * Finds the avarage center point of the polygon, which is used later for our
     * KdTree
     * 
     * @param ways the different ways the creates the polygon
     */
    public void getAvgXYinWays(ArrayList<OSMWay> ways) {
        if (ways.size() != 0) {
            int count = 0;
            float sumX = 0f;
            float sumY = 0f;
            for (int i = 0; i < ways.size(); i++) {
                ArrayList<OSMNode> nodes = new ArrayList<>(ways.get(i).getNodes());
                for (OSMNode node : nodes) {
                    count++;
                    sumX += node.getXPoint();
                    sumY += node.getYPoint();
                }
            }
            avgOuterX = sumX / count;
            avgOuterY = sumY / count;
            return;
        }
    }

    @Override
    public float getXPoint() {
        return avgOuterX;
    }

    @Override
    public float getYPoint() {
        return avgOuterY;
    }

    @Override
    public String getCoordinate() {
        return avgOuterX + " , " + avgOuterY;
    }

}