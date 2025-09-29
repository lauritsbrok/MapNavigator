package bfst22.vector.OSMParsing;

import java.util.HashMap;

import bfst22.vector.Util.Point;

/**
 * This class is responsible for keeping track of coastlines and connecting them
 * to a polygon.
 */
public class CoastLineTool {
    public static void connectCoastLine(HashMap<Point, OSMWay> coastLineMap, OSMWay current) {
        OSMWay prev = coastLineMap.remove(current.getFirstPoint());
        OSMWay next = coastLineMap.remove(current.getLastPoint());
        OSMWay merged = new OSMWay();

        if (prev != null) {
            merged.addNodes(prev.nodes);
        }
        merged.addNodes(current.nodes);
        if (prev != next && next != null) {
            merged.addNodes(next.nodes);
        }
        coastLineMap.put(merged.getFirstPoint(), merged);
        coastLineMap.put(merged.getLastPoint(), merged);
    }
}
