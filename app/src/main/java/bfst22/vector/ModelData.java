package bfst22.vector;

import java.util.ArrayList;
import java.util.EnumMap;

import bfst22.vector.Graph.Graph;
import bfst22.vector.OSMParsing.OSMAddress;
import bfst22.vector.OSMParsing.OSMPointOfInterest;
import bfst22.vector.Util.KdTree;
import bfst22.vector.Util.RadixTree;
import bfst22.vector.Util.WayType;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.Drawable;

/**
 * This class is responsible for keeping track of all model data including
 * nodes, coastlines, adresses, graph for route finding
 * and points of interest.
 */
public class ModelData {
    public static ArrayList<Drawable> coastLine;
    public static ArrayList<OSMAddress> addresses;
    public static Graph graph;
    public static ArrayList<OSMPointOfInterest> pointOfInterest;
    public static EnumMap<WayType, KdTree<XYPoint>> segmentsTree;
    public static RadixTree<String> trieAddress;

    public static void setNodes(EnumMap<WayType, ArrayList<XYPoint>> segments) {
        long startTime = System.currentTimeMillis();
        segmentsTree = new EnumMap<WayType, KdTree<XYPoint>>(WayType.class);
        for (WayType type : WayType.values()) {
            if (type != WayType.UNKNOWN && type != WayType.UNDEFINED) {
                System.out.println("Creating KdTrees for " + type.toString());
                segmentsTree.put(type, new KdTree<XYPoint>(segments.get(type)));
            }
        }
        System.out.println("kdTree done : " + (System.currentTimeMillis() - startTime));
    }

    public static void setCoastLine(ArrayList<Drawable> newCoastLine) {
        coastLine = new ArrayList<>(newCoastLine);
    }

    public static void setAddresses(ArrayList<OSMAddress> parsedAddresses) {
        ModelData.addresses = new ArrayList<>(parsedAddresses);
    }

    public static void setGraph(Graph newgraph) {
        graph = newgraph;
        long startTime = System.currentTimeMillis();
        System.out.println("optimizing graph");
        graph.optimizeGraph();
        System.out.println("optimize done : " + (System.currentTimeMillis() - startTime));
    }

    public static void addPointOfInterest(OSMPointOfInterest point) {
        if (pointOfInterest == null) {
            pointOfInterest = new ArrayList<>();
        }
        pointOfInterest.add(point);
    }

    public static void putPointOfInterestInRadix() {
        long startTime = System.currentTimeMillis();
        System.out.println("Making PointOfInterest Searchable");
        if (ModelData.pointOfInterest == null) {
            return;
        }
        for (OSMPointOfInterest point : ModelData.pointOfInterest) {
            String temp = point.toString().toLowerCase();
            String coordinate = point.getCoordinate();
            ModelData.trieAddress.put(temp, coordinate);
        }
        System.out.println("Making PointOfInterest Searchable done : " +
                (System.currentTimeMillis() - startTime));
    }

    /**
     * This method inserts the users point of interest in our radix tree so it is
     * searchable.
     */
    public static void addPointOfInterestToRadix(OSMPointOfInterest pointOfInterest) {
        String temp = pointOfInterest.toString().toLowerCase();
        String coordinate = pointOfInterest.getCoordinate();
        ModelData.trieAddress.put(temp, coordinate);
    }
}