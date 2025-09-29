package bfst22.vector.OSMParsing;

import java.util.Comparator;
import java.util.ArrayList;

public class RelationMap extends ArrayList<OSMRelation> {
    boolean sorted;

    @Override
    public boolean add(OSMRelation item) {
        sorted = false;
        return super.add(item);
    }

    public OSMRelation get(long ref) {
        if (!sorted) {
            sort(Comparator.comparing(node -> node.getID()));
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
        OSMRelation node2 = get(lo);
        return (node2.getID() == ref) ? node2 : null;
    }
}