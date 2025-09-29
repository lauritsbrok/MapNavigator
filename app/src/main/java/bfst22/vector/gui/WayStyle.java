package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * This class is used for keeping track of all the features for a way such as
 * color, linewidth etc.
 */
public class WayStyle {

    Color outerColor;
    Color innerColor;
    Color outLineColor;
    Double linewidth;
    StrokeLineCap cap;
    StrokeLineJoin join;
    Double[] lineDashes;
    double opacity;
    boolean fill;
    boolean draw;
    boolean outline;
    double depth;
    boolean randomColor;

    /**
     * Default settings for a way
     */
    public WayStyle() {
        outerColor = Color.BLACK;
        innerColor = Color.LIGHTGRAY;
        outLineColor = Color.GRAY;
        linewidth = 1.0;
        cap = StrokeLineCap.BUTT;
        join = StrokeLineJoin.BEVEL;
        lineDashes = null;
        fill = false;
        draw = false;
        outline = false;
        opacity = 1;
        depth = 0;
        randomColor = false;
    }

    /**
     * is used for drawing the way
     * 
     * @param gc
     * @return
     */
    public GraphicsContext setStyle(GraphicsContext gc) {
        gc.setLineWidth(linewidth);
        gc.setLineCap(cap);
        gc.setLineJoin(join);
        gc.setGlobalAlpha(opacity);
        if (lineDashes != null) {
            gc.setLineDashes(lineDashes[0], lineDashes[0]);
        } else {
            gc.setLineDashes(null);
        }
        return gc;
    }

    public void setOuterColor(String color) {
        this.outerColor = Color.valueOf(color);
    }

    public void setOuterColor(Color color) {
        this.outerColor = color;
    }

    public void setInnerColor(String color) {
        this.innerColor = Color.valueOf(color);
    }

    public void setInnerColor(Color color) {
        this.innerColor = color;
    }

    public void setCap(StrokeLineCap cap) {
        this.cap = cap;
    }

    public void setLinewidth(Double linewidth) {
        this.linewidth = linewidth;
    }

    public void setJoin(StrokeLineJoin join) {
        this.join = join;
    }

    public void setLineDashes(Double[] lineDashes) {
        this.lineDashes = lineDashes;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    public Color getInnerColor() {
        return this.innerColor;
    }

    public Paint getOuterColor() {
        return this.outerColor;
    }

    public boolean getFill() {
        return fill;
    }

    public boolean getDraw() {
        return draw;
    }

    public boolean getOutline() {
        return outline;
    }

    public double getLinewidth() {
        return linewidth;
    }

    public double getDepth() {
        return depth;
    }

    public Double[] getLinedashes() {
        return lineDashes;
    }

    public double getOpacity() {
        return opacity;
    }

    public Color getOutLineColor() {
        return outLineColor;
    }

    public void setOutLineColor(Color outLineColor) {
        this.outLineColor = outLineColor;
    }

    public StrokeLineCap getCap() {
        return cap;
    }

    public StrokeLineJoin getJoin() {
        return join;
    }

    public Double[] getLineDashes() {
        return lineDashes;
    }

    public boolean isRandomColor() {
        return randomColor;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
    }
}