package bfst22.vector.Util;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import bfst22.vector.gui.PolyLine;
import bfst22.vector.gui.ViewPort;

/**
 * This class is responsible for our KdTree used for only drawing ways visible
 * in the viewport.
 */
@SuppressWarnings("all")
public class KdTree<T extends XYPoint> implements Iterable<T>, Serializable {
    public static final long serialVersionUID = 8548;
    private final int k = 2;
    private KdNode root = null;
    public KdSplitLineList splits;
    public PolyLine nearestPoint;
    private static final Comparator<XYPoint> X_COMPARATOR = new Comparator<XYPoint>() {
        @Override
        public int compare(XYPoint o1, XYPoint o2) {
            if (o1.getXPoint() < o2.getXPoint()) {
                return -1;
            }

            if (o1.getXPoint() > o2.getXPoint())
                return 1;
            return 0;
        }
    };
    private static final Comparator<XYPoint> Y_COMPARATOR = new Comparator<XYPoint>() {
        @Override
        public int compare(XYPoint o1, XYPoint o2) {
            if (o1.getYPoint() < o2.getYPoint())
                return -1;
            if (o1.getYPoint() > o2.getYPoint())
                return 1;
            return 0;
        }
    };

    protected static final int X_AXIS = 0;
    protected static final int Y_AXIS = 1;

    public KdTree() {
    }

    public KdTree(List<T> list) {
        super();
        root = createNode(list, 0);
    }

    /**
     * This method is used to recursively creating the KDTree from a list, is is
     * balanced by alway sorting the list on the current axis.
     * 
     * @param list  an ArrayList of T, to put into the kdtree.
     * @param depth the current depth of the next insert of nodes
     * @return the return value is used for recursively creating nodes
     */
    private KdNode createNode(List<T> list, int depth) {
        if (list == null || list.size() == 0)
            return null;

        int axis = depth % k;
        if (axis == X_AXIS)
            Collections.sort(list, X_COMPARATOR);
        else if (axis == Y_AXIS)
            Collections.sort(list, Y_COMPARATOR);

        KdNode node = null;
        List<T> less = new ArrayList<>(list.size());
        List<T> more = new ArrayList<T>(list.size());

        if (list.size() > 0) {
            int medianIndex = list.size() / 2;
            node = new KdNode(list.get(medianIndex), k, depth);
            // Process list to see where each non-median point lies
            for (int i = 0; i < list.size(); i++) {
                if (i == medianIndex)
                    continue;
                T p = list.get(i);
                // Cannot assume points before the median are less since they could be equal
                if (KdNode.compareTo(depth, k, p, node.getPoint()) <= 0) {
                    less.add(p);
                } else {
                    more.add(p);
                }
            }

            if ((medianIndex - 1 >= 0) && less.size() > 0) {
                node.lesser = createNode(less, depth + 1);
                node.lesser.parent = node;
            }

            if ((medianIndex <= list.size() - 1) && more.size() > 0) {
                node.greater = createNode(more, depth + 1);
                node.greater.parent = node;
            }
        }

        return node;
    }

    /**
     * Adds value to the tree. Tree can contain multiple equal values.
     * This will create a more unbalanced tree, than making the tree with a complete
     * list
     * 
     * @param value
     *              T to add to the tree.
     * @return True if successfully added to tree.
     */
    public boolean add(T value) {
        if (value == null)
            return false;

        if (root == null) {
            root = new KdNode(value);
            return true;
        }

        KdNode node = root;
        while (true) {
            if (KdNode.compareTo(node.depth, node.k, value, node.getPoint()) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    KdNode newNode = new KdNode(value, k, node.depth + 1);
                    newNode.parent = node;
                    node.lesser = newNode;
                    break;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    KdNode newNode = new KdNode(value, k, node.depth + 1);
                    newNode.parent = node;
                    node.greater = newNode;
                    break;
                }
                node = node.greater;
            }
        }
        return true;
    }

    /**
     * Does the tree contain the value.
     *
     * @param value
     *              T to locate in the tree.
     * @return True if tree contains value.
     */
    public boolean contains(T value) {
        if (value == null || root == null)
            return false;

        KdNode node = getNode(this, value);
        return (node != null);
    }

    /**
     * Locates T in the tree.
     *
     * @param tree
     *              to search.
     * @param value
     *              to search for.
     * @return KdNode or NULL if not found
     */
    private static final <T extends XYPoint> KdNode<T> getNode(KdTree<T> tree, T value) {
        if (tree == null || tree.root == null || value == null)
            return null;

        KdNode<T> node = tree.root;
        while (true) {
            if (node.getPoint().equals(value)) {
                return node;
            } else if (KdNode.compareTo(node.depth, node.k, value, node.getPoint()) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    return null;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    return null;
                }
                node = node.greater;
            }
        }
    }

    /**
     * 
     * @param value the T value to get from the kdtree
     * @return if the value exists in the kdtree, it is returned otherwise null
     */

    public T get(T value) {
        if (this == null || this.root == null || value == null)
            return null;

        KdNode node = this.root;
        while (true) {
            if (node.getPoint().getCoordinate().equals(value.getCoordinate())) {
                return (T) node.getPoint();
            } else if (KdNode.compareTo(node.depth, node.k, value, node.getPoint()) <= 0) {
                // Lesser
                if (node.lesser == null) {
                    return null;
                }
                node = node.lesser;
            } else {
                // Greater
                if (node.greater == null) {
                    return null;
                }
                node = node.greater;
            }
        }
    }

    /**
     * 
     * @param value the value T to remove from the tree
     * @return true if the value was removed from the tree
     */
    public boolean remove(T value) {
        if (value == null || root == null)
            return false;

        KdNode node = getNode(this, value);
        if (node == null)
            return false;

        KdNode parent = node.parent;
        if (parent != null) {
            if (parent.lesser != null && node.equals(parent.lesser)) {
                List<T> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.lesser = createNode(nodes, node.depth);
                    if (parent.lesser != null) {
                        parent.lesser.parent = parent;
                    }
                } else {
                    parent.lesser = null;
                }
            } else {
                List<T> nodes = getTree(node);
                if (nodes.size() > 0) {
                    parent.greater = createNode(nodes, node.depth);
                    if (parent.greater != null) {
                        parent.greater.parent = parent;
                    }
                } else {
                    parent.greater = null;
                }
            }
        } else {
            // root
            List<T> nodes = getTree(node);
            if (nodes.size() > 0)
                root = createNode(nodes, node.depth);
            else
                root = null;
        }

        return true;
    }

    public KdNode getRoot() {
        return root;
    }

    /**
     * 
     * @return The kdtree as a list
     */
    public List<T> TreeToList() {
        List<T> list = new ArrayList<T>();
        list.add((T) root.getPoint());
        list.addAll(getTree(root));
        return list;
    }

    public static final <T extends XYPoint> List<T> getTree(KdNode root) {
        List<T> list = new ArrayList<T>();
        if (root == null)
            return list;

        if (root.lesser != null) {
            list.add((T) root.lesser.getPoint());
            list.addAll(getTree(root.lesser));
        }
        if (root.greater != null) {
            list.add((T) root.greater.getPoint());
            list.addAll(getTree(root.greater));
        }

        return list;
    }

    /**
     * 
     * @return the split from the lattest rangesearch
     */

    public KdSplitLineList getSplits() {
        return splits;
    }

    public ArrayList<T> RangeSearch(ViewPort zone, int splittimes, boolean debug) {
        splits = new KdSplitLineList();
        HashSet<XYPoint> results = new HashSet<XYPoint>();
        if (root == null || zone == null) {
            return null;
        }
        zone = new ViewPort(zone.xmin() - zone.getExtraSpace(), zone.ymin() -
                zone.getExtraSpace(),
                zone.xmax() + zone.getExtraSpace(), zone.ymax() + zone.getExtraSpace());
        RangeSearch(results, root, zone, splittimes, debug);
        ArrayList<T> collection = new ArrayList<T>();
        for (XYPoint node : results)
            collection.add((T) node);
        return collection;
    }

    /**
     * 
     * @param results    the result list to insert containing XYPoints
     * @param kdNode
     * @param zone
     * @param splittimes
     * @param debug
     */

    private void RangeSearch(HashSet<XYPoint> results, KdNode kdNode, ViewPort zone, int splittimes, boolean debug) {
        if (kdNode == null) {
            return;
        }
        Stack<KdNode> stack = new Stack<KdNode>();
        stack.push(kdNode);
        int times = 0;
        while (!stack.isEmpty()) {
            KdNode temp = stack.pop();
            times++;
            int axis = temp.depth % temp.k;
            if (times == splittimes) {
                results.addAll(getTree(temp));
            }
            if (zone.contains(temp.getPoint())) {
                results.add(temp.getPoint());
            }
            splits.add(
                    new KdSplitLine(temp.getPoint().getXPoint(), temp.getPoint().getYPoint(), axis));
            if (axis == X_AXIS) {
                if (temp.getPoint().getXPoint() < zone.xmax()) {
                    if (temp.greater != null) {
                        stack.push(temp.greater);
                    }
                }
                if (temp.getPoint().getXPoint() > zone.xmin()) {
                    if (temp.lesser != null) {
                        stack.push(temp.lesser);
                    }
                }
            } else {
                if (temp.getPoint().getYPoint() < zone.ymax()) {
                    if (temp.greater != null) {
                        stack.push(temp.greater);
                    }
                }
                if (temp.getPoint().getYPoint() > zone.ymin()) {
                    if (temp.lesser != null) {
                        stack.push(temp.lesser);
                    }
                }
            }
        }
    }

    /**
     * Searches the K nearest neighbor.
     *
     * @param K
     *              Number of neighbors to retrieve. Can return more than K, if
     *              last nodes are equal distances.
     * @param value
     *              to find neighbors of.
     * @return Collection of T neighbors.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<T> nearestNeighborSearch(int K, T value) {
        if (value == null || root == null)
            return null;

        // Map used for results
        TreeSet<KdNode> results = new TreeSet<KdNode>(new EuclideanComparator(value));

        // Find the closest leaf node
        KdNode prev = null;
        KdNode node = root;
        while (node != null) {
            if (KdNode.compareTo(node.depth, node.k, value, node.getPoint()) <= 0) {
                // Lesser
                prev = node;
                node = node.lesser;
            } else {
                // Greater
                prev = node;
                node = node.greater;
            }
        }
        KdNode leaf = prev;

        if (leaf != null) {
            // Used to not re-examine nodes
            Set<KdNode> examined = new HashSet<KdNode>();

            // Go up the tree, looking for better solutions
            node = leaf;
            while (node != null) {
                // Search node
                searchNode(value, node, K, results, examined);
                node = node.parent;
            }
        }

        // Load up the collection of the results
        ArrayList<T> collection = new ArrayList<T>(K);
        for (KdNode kdNode : results) {
            collection.add((T) kdNode.getPoint());
        }

        if (collection.size() != 0 || collection != null) {
            this.nearestPoint = new PolyLine(value.getXPoint(), value.getYPoint(), collection.get(0).getXPoint(),
                    collection.get(0).getYPoint());
        }
        return collection;
    }

    private static final <T extends XYPoint> void searchNode(T value,
            KdNode node, int K,
            TreeSet<KdNode> results, Set<KdNode> examined) {
        examined.add(node);

        // Search node
        KdNode lastNode = null;
        Double lastDistance = Double.MAX_VALUE;
        if (results.size() > 0) {
            lastNode = results.last();
            lastDistance = lastNode.getPoint().haversineDistanceTo(value);
        }
        Double nodeDistance = node.getPoint().haversineDistanceTo(value);
        if (nodeDistance.compareTo(lastDistance) < 0) {
            if (results.size() == K && lastNode != null)
                results.remove(lastNode);
            results.add(node);
        } else if (nodeDistance.equals(lastDistance)) {
            results.add(node);
        } else if (results.size() < K) {
            results.add(node);
        }
        lastNode = results.last();
        lastDistance = lastNode.getPoint().haversineDistanceTo(value);

        int axis = node.depth % node.k;
        KdNode lesser = node.lesser;
        KdNode greater = node.greater;

        // Search children branches, if axis aligned distance is less than
        // current distance
        if (lesser != null && !examined.contains(lesser)) {
            examined.add(lesser);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.getPoint().getXPoint();
                valuePlusDistance = value.getXPoint() - lastDistance;
            } else {
                nodePoint = node.getPoint().getYPoint();
                valuePlusDistance = value.getYPoint() - lastDistance;
            }
            boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

            // Continue down lesser branch
            if (lineIntersectsCube)
                searchNode(value, lesser, K, results, examined);
        }
        if (greater != null && !examined.contains(greater)) {
            examined.add(greater);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.getPoint().getXPoint();
                valuePlusDistance = value.getXPoint() + lastDistance;
            } else {
                nodePoint = node.getPoint().getYPoint();
                valuePlusDistance = value.getYPoint() + lastDistance;

                boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

                // Continue down greater branch
                if (lineIntersectsCube)
                    searchNode(value, greater, K, results, examined);
            }
        }
    }

    /**
     * Adds, in a specified queue, a given node and its related nodes (lesser,
     * greater).
     *
     * @param node
     *                Node to check. May be null.
     *
     * @param results
     *                Queue containing all found entries. Must not be null.
     */
    @SuppressWarnings("unchecked")
    private static <T extends XYPoint> void search(final KdNode node, final Deque<T> results) {
        if (node != null) {
            results.add((T) node.getPoint());
            search(node.greater, results);
            search(node.lesser, results);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }

    protected static class EuclideanComparator implements Comparator<KdNode> {

        private final XYPoint point;

        public EuclideanComparator(XYPoint point) {
            this.point = point;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(KdNode o1, KdNode o2) {
            Double d1 = point.haversineDistanceTo(o1.getPoint());
            Double d2 = point.haversineDistanceTo(o2.getPoint());
            if (d1.compareTo(d2) < 0)
                return -1;
            else if (d2.compareTo(d1) < 0)
                return 1;
            return o1.compareTo(o2);
        }
    }

    /**
     * Searches all entries from the first to the last entry.
     *
     * @return Iterator
     *         allowing to iterate through a collection containing all found
     *         entries.
     */
    public Iterator<T> iterator() {
        final Deque<T> results = new ArrayDeque<T>();
        search(root, results);
        return results.iterator();
    }

    /**
     * Searches all entries from the last to the first entry.
     *
     * @return Iterator
     *         allowing to iterate through a collection containing all found
     *         entries.
     */
    public Iterator<T> reverse_iterator() {
        final Deque<T> results = new ArrayDeque<T>();
        search(root, results);
        return results.descendingIterator();
    }

    public static class KdNode<T extends XYPoint> implements Comparable<KdNode>, Serializable {
        public static final long serialVersionUID = 54546;
        private final T point;
        private final int k;
        private final int depth;

        private KdNode parent = null;
        private KdNode lesser = null;
        private KdNode greater = null;

        public KdNode(T point) {
            this.point = point;
            this.k = 2;
            this.depth = 0;
        }

        public KdNode(T point, int k, int depth) {
            this.point = point;
            this.k = k;
            this.depth = depth;
        }

        public static int compareTo(int depth, int k, XYPoint node, XYPoint node2) {
            int axis = depth % k;
            if (axis == X_AXIS) {
                return X_COMPARATOR.compare(node, node2);
            } else {
                return Y_COMPARATOR.compare(node, node2);
            }
        }

        public T getPoint() {
            return point;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return 31 * (this.k + this.depth + Float.floatToIntBits(this.getPoint().getYPoint())
                    + Float.floatToIntBits(this.getPoint().getYPoint()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof KdNode))
                return false;

            KdNode kdNode = (KdNode) obj;
            if (this.hashCode() == kdNode.hashCode())
                return true;
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(KdNode o) {
            return compareTo(depth, k, this.getPoint(), o.getPoint());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("k=").append(k);
            builder.append(" depth=").append(depth);
            builder.append(" id=").append(getPoint().getCoordinate());
            return builder.toString();
        }
    }

    protected static class TreePrinter {

        public static <T extends XYPoint> String getString(KdTree<T> tree) {
            if (tree.root == null)
                return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static String getString(KdNode node, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            if (node.parent != null) {
                String side = "left";
                if (node.parent.greater != null && node.getPoint().equals(node.parent.greater.getPoint()))
                    side = "right";
                builder.append(prefix + (isTail ? "└── " : "├── ") + "[" + side + "] " +
                        "depth=" + node.depth + " id="
                        + node.getPoint().getCoordinate() + "\n");
            } else {
                builder.append(prefix + (isTail ? "└── " : "├── ") + "depth=" + node.depth +
                        " id=" + node.getPoint().getCoordinate() + "\n");
            }
            List<KdNode> children = null;
            if (node.lesser != null || node.greater != null) {
                children = new ArrayList<KdNode>(2);
                if (node.lesser != null)
                    children.add(node.lesser);
                if (node.greater != null)
                    children.add(node.greater);
            }
            if (children != null) {
                for (int i = 0; i < children.size() - 1; i++) {
                    builder.append(getString(children.get(i), prefix + (isTail ? " " : "│ "),
                            false));
                }
                if (children.size() >= 1) {
                    builder.append(getString(children.get(children.size() - 1), prefix + (isTail
                            ? " "
                            : "│ "),
                            true));
                }
            }

            return builder.toString();
        }
    }
}