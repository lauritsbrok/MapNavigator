package bfst22.vector.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class is responsible for creating a point the gets drawn. It's used for
 * finding marking a searched address.
 */
public class PointOnMap {
    float lon, lat;

    public PointOnMap(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * Draws the point on the canvas.
     * 
     * @param gc
     */
    public void draw(GraphicsContext gc) {
        double radius = 0.00007;
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.000005);
        gc.strokeOval(lat - radius, lon - radius, radius, radius);
    }
}
