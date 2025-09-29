package bfst22.vector.OSMParsing;

import java.util.Comparator;
import java.util.ArrayList;

public class WayMap extends ArrayList<OSMWay> {
    boolean sorted;

    @Override
    public boolean add(OSMWay item) {
        sorted = false;
        return super.add(item);
    }

    public OSMWay get(final long ref) {
        if (!sorted) {
            sort(Comparator.comparing(way -> way.getID()));
            sorted = true;
        }
        int lo = 0;
        int hi = size();
        while (hi - lo > 1) {
            int mi = (lo + hi) / 2;
            if (get(mi).getID() <= ref) {
                lo = mi;
            } else {
                hi = mi;
            }
        }
        OSMWay node2 = get(lo);
        return (node2.getID() == ref) ? node2 : null;
    }
}