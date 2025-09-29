package bfst22.vector.OSMParsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bfst22.vector.Util.Point;
import bfst22.vector.Util.WayType;

/**
 * This class is responsible for the all OSMWays
 */
public class OSMWay extends OSMItem {
    public List<OSMNode> nodes;
    public String name;
    public WayType type;

    public OSMWay(List<OSMNode> nodes, WayType type) {
        this.type = WayType.UNKNOWN;
        this.nodes = new ArrayList<OSMNode>(nodes);
        this.type = type;
    }

    public OSMWay() {
        super();
        this.type = WayType.UNKNOWN;
        this.nodes = new ArrayList<OSMNode>();
    }

    public OSMWay(long id) {
        super(id);
        this.type = WayType.UNKNOWN;
        this.nodes = new ArrayList<OSMNode>();
    }

    public void addNodes(List<OSMNode> nodes) {
        this.nodes.addAll(nodes);
    }

    public void addNode(OSMNode node) {
        this.nodes.add(node);
    }

    public void setType(WayType type) {
        this.type = type;
    }

    public void setName(String value) {
        name = value;
    }

    public Point getFirstPoint() {
        return new Point(nodes.get(0).getXPoint(), nodes.get(0).getYPoint());
    }

    public Point getLastPoint() {
        return new Point(nodes.get(nodes.size() - 1).getXPoint(), nodes.get(nodes.size() - 1).getYPoint());
    }

    public OSMNode getFirstNode() {
        return nodes.get(0);
    }

    public OSMNode getLastNode() {
        return nodes.get(nodes.size() - 1);
    }

    public void ReversedNodes() {
        Collections.reverse(nodes);
    }

    public List<OSMNode> getNodes() {
        return nodes;
    }

    public WayType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
