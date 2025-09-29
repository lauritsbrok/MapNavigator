package bfst22.vector.Util;

import java.util.ArrayList;

import bfst22.vector.Model;
import bfst22.vector.gui.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class is responsible for holding all KdSplitLines for a KDTree
 * RangeSearch
 */
public class KdSplitLineList implements Drawable {
    public ArrayList<KdSplitLine> lines;
    public int showcount;

    KdSplitLineList() {
        this.lines = new ArrayList<KdSplitLine>();
        this.showcount = 0;
    }

    public void add(KdSplitLine line) {
        lines.add(line);
    }

    public void showNext() {
        showcount++;
    }

    public void removeLast() {
        if (showcount == 0) {
            return;
        }
        showcount--;
    }

    public int getShowCount() {
        return showcount;
    }

    public void setShowCount(int showcount) {
        this.showcount = showcount;
    }

    /**
     * responsible for tracing the desired number of KDSplitLines
     */
    @Override
    public void trace(GraphicsContext gc) {
        if (showcount == 0) {
            return;
        }
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.005);
        double xmax = Model.maxLonBoundary;
        double xmin = Model.minLonBoundary;
        double ymax = Model.maxLatBoundary;
        double ymin = Model.minLatBoundary;
        for (int i = 0; i < lines.size() && i < showcount; i++) {
            gc.setStroke(Color.RED);
            if (lines.get(i).axis == KdTree.X_AXIS) {
                gc.moveTo(lines.get(i).xSplit, ymin);
                gc.lineTo(lines.get(i).xSplit, ymax);

            } else {
                gc.moveTo(xmin, lines.get(i).ySplit);
                gc.lineTo(xmax, lines.get(i).ySplit);
            }
        }
    }
}
