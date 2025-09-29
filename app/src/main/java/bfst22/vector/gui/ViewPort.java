package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.XYPoint;

/**
 * This class is responsible for keeping track of the viewport which the user
 * can see.
 * It's used for the KdTree, to avoid drawing things outside the users viewport.
 */
public class ViewPort implements Drawable {
    double xmin, ymin, xmax, ymax;
    float extraSpace;

    /**
     * Creates the viewpoint box
     */
    public ViewPort(double xmin, double ymin, double xmax, double ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
        this.extraSpace = 0.02f;
        checkBounds(xmin, ymin, xmax, ymax);
    }

    public void setBounds(double xmin, double ymin, double xmax, double ymax) {
        checkBounds(xmin, ymin, xmax, ymax);
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    private void checkBounds(double xmin, double ymin, double xmax, double ymax) {
        if (Double.isNaN(xmin) || Double.isNaN(xmax)) {
            throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
        }
        if (Double.isNaN(ymin) || Double.isNaN(ymax)) {
            throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
        }
        if (xmax < xmin) {
            throw new IllegalArgumentException("xmax < xmin: " + toString());
        }
        if (ymax < ymin) {
            throw new IllegalArgumentException("ymax < ymin: " + toString());
        }
    }

    /**
     * This is used for drawing the box around the viewport. It's only used for
     * debugging, when we are simulating how the
     * KdTree works.
     */
    @Override
    public void trace(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(0.001);
        gc.moveTo(xmin, ymin);
        gc.lineTo(xmax, ymin);
        gc.lineTo(xmax, ymax);
        gc.lineTo(xmin, ymax);
        gc.lineTo(xmin, ymin);
        gc.stroke();
        gc.closePath();
        gc.setStroke(Color.RED);
        gc.moveTo(xmin - extraSpace, ymin - extraSpace);
        gc.lineTo(xmax + extraSpace, ymin - extraSpace);
        gc.lineTo(xmax + extraSpace, ymax + extraSpace);
        gc.lineTo(xmin - extraSpace, ymax + extraSpace);
        gc.lineTo(xmin - extraSpace, ymin - extraSpace);
    }

    public boolean contains(Point mousePoint) {
        return (mousePoint.getX() >= xmin) && (mousePoint.getX() <= xmax)
                && (mousePoint.getY() >= ymin) && (mousePoint.getY() <= ymax);
    }

    public boolean contains(XYPoint mousePoint) {
        return (mousePoint.getXPoint() >= xmin) && (mousePoint.getXPoint() <= xmax)
                && (mousePoint.getYPoint() >= ymin) && (mousePoint.getYPoint() <= ymax);
    }

    public float getExtraSpace() {
        return extraSpace;
    }

    public void move(double dx, double dy) {
        xmin += dx;
        xmax += dx;
        ymin += dy;
        ymax += dy;
    }

    /**
     * Scales the kdtree with the canvas
     * 
     * @param factor the zoom factor
     */
    public void scale(double factor) {
        double width = xmax - xmin;
        double height = ymax - ymin;
        double newWidth = (width * factor);
        double newHeight = (height * factor);
        if (newHeight < 0.007) {
            return;
        }
        double widthToAdd = (newWidth - width) / 2;
        double heightToAdd = (newHeight - height) / 2;
        xmax += widthToAdd;
        xmin -= widthToAdd;
        ymin -= heightToAdd;
        ymax += heightToAdd;
    }

    public double getWidth() {
        return xmax - xmin;
    }

    public double getHeigh() {
        return ymax - ymin;
    }

    public double xmax() {
        return xmax;
    }

    public double xmin() {
        return xmin;
    }

    public double ymax() {
        return ymax;
    }

    public double ymin() {
        return ymin;
    }

    // Find the center of the viewPort
    public Point getMidPoint() {
        return new Point((xmax + xmin) / 2.0, ((ymax + ymin) / 2.0));
    }
}