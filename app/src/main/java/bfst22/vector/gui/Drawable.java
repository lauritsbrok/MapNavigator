package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;

/**
 * This interface is responsible for having the default methods for drawing and
 * filling waytypes
 */
public interface Drawable {
    /**
     * This methods draws a path
     * 
     * @param gc the canvas being drawn on
     */
    default void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    /**
     * This method fills a path
     * 
     * @param gc the canvas being drawn on
     */
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }

    void trace(GraphicsContext gc);
}
