package bfst22.vector.gui;

import java.io.InputStream;
import bfst22.vector.View;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * This class is responsible for drawing a icon / image on the map.
 * It's used for points of interest and navigation.
 */
public class Icon {
    double x, y;
    IconEnum icon;

    /**
     * this constructor creates a new icon
     * 
     * @param x    x position of where to draw
     * @param y    y position of where to draw
     * @param icon describes the type of icon depending on the task
     */
    public Icon(double x, double y, IconEnum icon) {
        this.x = x;
        this.y = y;
        this.icon = icon;
    }

    /**
     * This method returns the corresponding icon depending on the task.
     * 
     * @return returns the file for a icon corresponding to the specific task.
     */
    public InputStream getImageIconFile() {
        InputStream mapPin;
        switch (icon) {
            case POINT_FROM:
                mapPin = View.class.getResourceAsStream("icons/Map_pin_red.png");
                break;
            case POINT_INTEREST:
                mapPin = View.class.getResourceAsStream("icons/Map_pin_grey.png");
                break;
            case POINT_TO:
                mapPin = View.class.getResourceAsStream("icons/Map_pin_green.png");
                break;
            default:
                mapPin = null;
                break;
        }
        return mapPin;
    }

    /**
     * This method draws the image on a canvas
     * 
     * @param gc     the main canvas being drawn on
     * @param radius the size of the image defined in radius
     */
    public void draw(GraphicsContext gc, double radius) {
        InputStream mapPin = getImageIconFile();
        Image img = new Image(mapPin);
        double heightScale = img.getHeight() / img.getHeight();
        gc.drawImage(img, x - radius, y - radius,
                radius, radius * heightScale);
    }
}