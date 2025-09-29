package bfst22.vector;

import bfst22.vector.OSMParsing.OSMPointOfInterest;
import bfst22.vector.Util.Point;
import bfst22.vector.gui.MapCanvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class is the controller for all data connected to the point of interrest
 * function.
 */

public class PointOfInterestController extends Controller {

    private Point mousePos;
    private static String pointcolor;

    @FXML
    MapCanvas canvas;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> colorPicker;

    @SuppressWarnings("all")
    public void init(MapCanvas canvas, Point mousePos) {
        this.mousePos = mousePos;
        colorPicker.getItems().addAll("Yellow", "Blue", "Green", "Red", "Pink");
        this.canvas = canvas;
    }

    @FXML
    private void addPoint(ActionEvent e) {
        String name = nameField.getText();
        String color = (String) colorPicker.getSelectionModel().getSelectedItem();
        if (color == null) {
            color = "Red";
        }
        switch (color) {
            case "Yellow":
                pointcolor = "#FDB833";
                break;
            case "Blue":
                pointcolor = "#1789FC";
                break;
            case "Green":
                pointcolor = "#679436";
                break;
            case "Red":
                pointcolor = "#8C2F39";
                break;
            case "Pink":
                pointcolor = "#F61067";
                break;
        }
        OSMPointOfInterest favoriteNode = new OSMPointOfInterest((float) mousePos.getX(), (float) mousePos.getY(), name,
                pointcolor);
        ModelData.addPointOfInterest(favoriteNode);
        ModelData.addPointOfInterestToRadix(favoriteNode);
        canvas.repaint();

        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
