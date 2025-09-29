package bfst22.vector.OSMParsing;

import java.util.ArrayList;
import java.util.HashMap;

import bfst22.vector.Util.Point;
import bfst22.vector.Util.WayType;

public class OSMRelation extends OSMItem {
    public ArrayList<OSMWay> inners;
    public ArrayList<OSMWay> outers;
    public WayType waytype;

    public OSMRelation() {
        this.waytype = WayType.UNKNOWN;
        this.inners = new ArrayList<OSMWay>();
        this.outers = new ArrayList<OSMWay>();
    }

    public OSMRelation(long id) {
        super(id);
        this.waytype = WayType.UNKNOWN;
        this.inners = new ArrayList<OSMWay>();
        this.outers = new ArrayList<OSMWay>();
    }

    public void addMember(OSMNode member, String role) {
        switch (role) {
            case "outer": {
                outers.get(outers.size() - 1).addNode(member);
                break;
            }
            case "inner": {
                inners.get(inners.size() - 1).addNode(member);
                break;
            }
        }
    }

    public void addMember(OSMWay member, String role) {
        switch (role) {
            case "outer": {
                outers.add(member);
                break;
            }
            case "inner": {
                inners.add(member);
                break;
            }
        }
    }

    public void addMember(OSMRelation member, String role) {
        switch (role) {
            case "outer": {
                this.outers.addAll(member.inners);
                this.outers.addAll(member.outers);
                break;
            }
            case "inner": {
                this.inners.addAll(member.inners);
                this.inners.addAll(member.outers);
                break;
            }
        }
    }

    public void correctOuters() {
        HashMap<Point, OSMWay> outersMap = new HashMap<Point, OSMWay>();
        for (OSMWay way : outers) {
            OSMWay prev = outersMap.remove(way.getFirstPoint());
            OSMWay next = outersMap.remove(way.getLastPoint());
            OSMWay merged = new OSMWay();
            if (prev != null) {
                if (!way.getFirstPoint().equals(prev.getLastPoint())) {
                    prev.ReversedNodes();
                }
                merged.addNodes(prev.nodes);
            }
            merged.addNodes(way.nodes);
            if (prev != next && next != null) {
                if (!way.getLastPoint().equals(next.getFirstPoint())) {
                    next.ReversedNodes();
                }
                merged.addNodes(next.nodes);
            }
            outersMap.put(merged.getFirstPoint(), merged);
            outersMap.put(merged.getLastPoint(), merged);
        }
        outers.clear();
        for (

        Point key : outersMap.keySet()) {
            OSMWay temp = outersMap.get(key);
            if (!outers.contains(temp)) {
                outers.add(temp);
            }
        }
    }

    public void setWaytype(WayType waytype) {
        this.waytype = waytype;
    }

    public WayType getWaytype() {
        return waytype;
    }

}