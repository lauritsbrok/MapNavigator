package Util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.KdTree;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.ViewPort;

public class KdTreeTest {

    @Test
    void createKdTreeFromListTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 40; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        for (int i = 0; i < points.size(); i++) {
            assertEquals(true, kdTree.contains(points.get(i)));
        }
        points.clear();
        for (int i = 1; i < 40; i++) {
            points.add(new OSMNode(i * 2, i));
        }
        kdTree = new KdTree<>(points);
        for (int i = 0; i < points.size(); i++) {
            assertEquals(true, kdTree.contains(points.get(i)));
        }
        points.clear();
        kdTree = new KdTree<>(null);
        kdTree = new KdTree<>(points);
    }

    @Test
    void addNodeToKdTreeTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        OSMNode nodeToAddOne = new OSMNode(100, 200);
        OSMNode nodeToAddTwo = new OSMNode(300, 100);
        assertEquals(true, kdTree.add(nodeToAddOne));
        assertEquals(true, kdTree.add(nodeToAddOne));
        assertEquals(true, kdTree.add(nodeToAddTwo));
        assertEquals(false, kdTree.add(null));
    }

    @Test
    void getTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i * 2, i));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        for (int i = 0; i < points.size(); i++) {
            assertEquals(points.get(i), kdTree.get(points.get(i)));
        }
        assertEquals(null, kdTree.get(null));
        assertEquals(null, kdTree.get(new OSMNode(100, 200)));
        assertEquals(null, kdTree.get(new OSMNode(-100, -200)));
    }

    @Test
    void containsTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        assertEquals(false, kdTree.contains(null));
    }

    @Test
    void removeTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i * 2, i));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i, -i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i * 2, -i));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        for (int i = 0; i < points.size(); i++) {
            assertEquals(true, kdTree.remove(points.get(i)));
        }
        assertEquals(false, kdTree.remove(points.get(0)));
        assertEquals(false, kdTree.remove(null));
        kdTree = new KdTree<>();
        assertEquals(false, kdTree.remove(points.get(0)));
        assertEquals(false, kdTree.remove(points.get(0)));
    }

    @Test
    void nearestNeighborSearchTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        OSMNode nodeToFind = new OSMNode(100, 200);
        points.add(nodeToFind);
        KdTree<OSMNode> kdTree = new KdTree<>(points);

        assertEquals(nodeToFind, kdTree.nearestNeighborSearch(1, new OSMNode(90, 190)).get(0));
    }

    @Test
    public void getRootTest() {
        KdTree<OSMNode> kdTree = new KdTree<>();
        OSMNode root = new OSMNode(100, 200);
        assertEquals(null, kdTree.getRoot());
        kdTree.add(root);
        assertEquals(root, kdTree.getRoot().getPoint());
    }

    @Test
    @SuppressWarnings("all")
    public void getTreeTest() {
        KdTree<OSMNode> kdTree = new KdTree<>();
        ArrayList<XYPoint> list = new ArrayList<>();
        assertEquals(list, kdTree.getTree(kdTree.getRoot()));
    }

    @Test
    public void getSplitsTest() {
        KdTree<OSMNode> kdTree = new KdTree<>();
        assertEquals(null, kdTree.getSplits());
    }

    @Test
    public void treeToListTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i * 2, i));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i, -i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i * 2, -i));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        ArrayList<XYPoint> tree = new ArrayList<XYPoint>(kdTree.TreeToList());
        for (int i = 0; i < tree.size(); i++) {
            assertEquals(points.contains(points.get(i)), tree.contains(tree.get(i)));
        }
    }

    @Test
    public void RangeSearchTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        points.add(new OSMNode(0, 0));
        points.add(new OSMNode(1, 1));
        points.add(new OSMNode(4, 3));
        points.add(new OSMNode(5, 4));
        points.add(new OSMNode(6, 6));
        points.add(new OSMNode(3, 7));
        points.add(new OSMNode(9, 9));
        points.add(new OSMNode(-3, -3));
        points.add(new OSMNode(-6, -3));
        points.add(new OSMNode(-3, 2));
        points.add(new OSMNode(-5, 7));
        KdTree<OSMNode> kdTree = new KdTree<>(points);
        ViewPort view = new ViewPort(3, 2, 7, 7);
        assertEquals(4, kdTree.RangeSearch(view, 0, false).size());
        assertEquals(null, kdTree.RangeSearch(null, 0, false));
        kdTree = new KdTree<>();
        assertEquals(null, kdTree.RangeSearch(view, 0, false));
    }

    @Test
    @SuppressWarnings("unused")
    public void equalsTest() {
        ArrayList<OSMNode> points = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i, i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(i * 2, i));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i, -i * 2));
        }
        for (int i = 1; i < 10; i++) {
            points.add(new OSMNode(-i * 2, -i));
        }
        KdTree<OSMNode> kdTree = new KdTree<>(points);
    }
}
