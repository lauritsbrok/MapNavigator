package bfst22.vector.OSMParsing;

import java.util.Comparator;
import java.util.ArrayList;

/**
 * This class is responsible for keeping track of the nodes by id
 */
public class NodeMap extends ArrayList<OSMNode> {
    boolean sorted;

    @Override
    public boolean add(final OSMNode node) {
        sorted = false;
        return super.add(node);
    }

    public OSMNode get(final long ref) {
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
        OSMNode node2 = get(lo);
        return (node2.getID() == ref) ? node2 : null;
    }
}