package bfst22.vector.gui;

import java.io.Serializable;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.XYPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Label implements XYPoint, Serializable {
    public static final long serialVersionUID = 689546546;
    String name;
    float x, y;

    public Label() {
    }

    public void drawLabel(GraphicsContext gc, double dynamicLinewidth) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Comic sans MS", dynamicLinewidth * 4));
        gc.fillText(name, x, y);
    }

    @Override
    public float getXPoint() {
        return x;
    }

    @Override
    public float getYPoint() {
        return y;
    }

    public String getName() {
        return name;
    }

    public void setLocation(OSMNode tempNode) {
        this.x = tempNode.getXPoint();
        this.y = tempNode.getYPoint();
    }

    public void setName(String name) {
        this.name = name;
    }
}
