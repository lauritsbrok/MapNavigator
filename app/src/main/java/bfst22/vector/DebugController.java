package bfst22.vector;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.text.DecimalFormat;

import bfst22.vector.Graph.TransportType;
import bfst22.vector.OSMParsing.OSMParser;
import bfst22.vector.Util.WayType;
import bfst22.vector.gui.MapCanvas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;

/**
 * This class is responsible for controlling the debugger.
 */

@SuppressWarnings("unused")
public class DebugController extends Controller {
    private boolean primaryClick;
    private Point2D lastMouse;
    private Point2D currentMouse;
    private MapCanvas canvas;

    @FXML
    Pane WayType_Pane;
    @FXML
    CheckBox kdTreeDebug;
    @FXML
    CheckBox nearestPointDebug;
    @FXML
    CheckBox analysedPath;
    @FXML
    CheckBox routeDebug;
    @FXML
    Controller mainWindowController;
    @FXML
    TextField fpsFactorValue;
    @FXML
    ChoiceBox<String> routeAlgo;
    @FXML
    ChoiceBox<String> transport;
    @FXML
    ChoiceBox<String> routeType;
    @FXML
    TextField routeDistance;
    @FXML
    TextField pointToField;
    @FXML
    TextField pointFromField;

    public void setFPS(int fps) {
        fpsFactorValue.setText(fps + "");
    }

    /**
     * This method initializes the debugger on the Mapcanvas
     * 
     * @param canvas               Is the map canvas that the map is drawn on
     * @param mainWindowController
     * @param debugStage
     */
    public void init(MapCanvas canvas, Controller mainWindowController, Stage debugStage) {
        this.canvas = canvas;
        debugStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        this.mainWindowController = mainWindowController;
        String[] routeAlgoType = new String[] { "Djikstra", "Astar" };
        routeAlgo.getItems().addAll(routeAlgoType);
        routeAlgo.setValue(routeAlgoType[1]);
        String[] transportType = new String[] { "Car", "Bike", "Walk" };
        transport.getItems().addAll(transportType);
        transport.setValue(transportType[0]);
        String[] routeTypes = new String[] { "Shortest", "Fastest" };
        routeType.getItems().addAll(routeTypes);
        routeType.setValue(routeTypes[0]);
    }

    private void closeWindowEvent(WindowEvent event) {
        canvas.viewPortDebug(false);
        mainWindowController.toggleViewportDebug(false);
        mainWindowController.togglenearestPointDebug(false);
        changeAllWayType(0);
        canvas.repaint();
    }

    public void setPointFrom(String point) {
        pointFromField.setText(point);
    }

    public void setPointTo(String point) {
        pointToField.setText(point);
    }

    public void setDistance(String point) {
        routeDistance.setText(point);
    }

    @FXML
    private void ToggleNearestPointDebug(ActionEvent e) {
        mainWindowController.togglenearestPointDebug(nearestPointDebug.isSelected());
    }

    @FXML
    private void ToggleKdTreeDebugger(ActionEvent e) {
        canvas.viewPortDebug(kdTreeDebug.isSelected());
        mainWindowController.toggleViewportDebug();
    }

    @FXML
    private void toggleRouteDebugger(ActionEvent e) {
        mainWindowController.setRouteDebug(routeDebug.isSelected());
    }

    @FXML
    private void toggleAnalysedPath(ActionEvent e) {
        mainWindowController.setAnalysedPath(analysedPath.isSelected());
    }

    @FXML
    private void ToggleAstar(ActionEvent e) {
        if (routeAlgo.getValue().equals("Astar")) {
            mainWindowController.setAstar(true);
        } else {
            mainWindowController.setAstar(false);
        }
    }

    @FXML
    private void ToggleRouteType(ActionEvent e) {
        if (routeType.getValue().equals("Shortest")) {
            mainWindowController.setRouteType(true);
        } else {
            mainWindowController.setRouteType(false);
        }
    }

    @FXML
    private void ToggleZoomFactor(ActionEvent e) {
        canvas.toggleZoomFactor();
    }

    @FXML
    private void nextSplitLine(ActionEvent e) {
        canvas.addToSplitLine();
    }

    @FXML
    private void prevSplitLine(ActionEvent e) {
        canvas.removeSplitLine();
    }

    @FXML
    private void chooseTransport(ActionEvent e) {
        if (transport.getValue().equals("Car")) {
            mainWindowController.setTransport(TransportType.Car_Route);
        } else if (transport.getValue().equals("Bike")) {
            mainWindowController.setTransport(TransportType.Bike_Route);
        } else {
            mainWindowController.setTransport(TransportType.Walk_Route);
        }
    }

    @FXML
    private void onAction(ActionEvent e) {
        if (e.getSource() instanceof CheckBox) {
            CheckBox target = (CheckBox) e.getSource();
            WayType type = getWayTypeFromCheckBox(target);
            canvas.SelectWayTypeToDraw(type, target.isSelected());
        }
    }

    @FXML
    private void deSelectAllWayType() {
        changeAllWayType(1);
    }

    @FXML
    private void selectAllWayType() {
        changeAllWayType(0);
    }

    @FXML
    private void toggleAllWayType() {
        changeAllWayType(3);
    }

    /**
     * This method makes all the waytypes to the opposite of what they where before.
     * 
     * @param select
     */
    private void changeAllWayType(int select) {
        for (Node node : WayType_Pane.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox temp = (CheckBox) node;
                if (select == 0) {
                    temp.setSelected(true);
                } else if (select == 1) {
                    temp.setSelected(false);
                } else {
                    temp.setSelected(!temp.isSelected());
                }

                WayType type = getWayTypeFromCheckBox(temp);
                canvas.SelectWayTypeToDraw(type, temp.isSelected());
            }
        }
        canvas.repaint();
    }

    @FXML
    public void exitApplication(ActionEvent event) {
    }

    private WayType getWayTypeFromCheckBox(CheckBox target) {
        switch (target.getText().trim().toUpperCase()) {
            case "COASTLINE":
                return WayType.COASTLINE;
            case "HEATH":
                return WayType.HEATH;
            case "GRASSLAND":
                return WayType.GRASSLAND;
            case "LAKE":
                return WayType.LAKE;
            case "BEACH":
                return WayType.BEACH;
            case "WOOD":
                return WayType.WOOD;
            case "WETLAND":
                return WayType.WETLAND;
            case "SAND":
                return WayType.SAND;
            case "PICKNIC_SITE":
                return WayType.PICNIC_SITE;
            case "STADIUM":
                return WayType.STADIUM;
            case "PARK":
                return WayType.PARK;
            case "SPORT":
                return WayType.SPORT;
            case "PLAYGROUND":
                return WayType.PLAYGROUND;
            case "PIER":
                return WayType.PIER;
            case "BREAKWATER":
                return WayType.BREAKWATER;
            case "EMBANKMENT":
                return WayType.EMBANKMENT;
            case "WATER":
                return WayType.WATER;
            case "SCRUB":
                return WayType.SCRUB;
            case "FOREST":
                return WayType.FOREST;
            case "GRASS":
                return WayType.GRASS;
            case "RESIDENTIAL":
                return WayType.RESIDENTIAL;
            case "MEADOW":
                return WayType.MEADOW;
            case "INDUSTRIAL":
                return WayType.INDUSTRIAL;
            case "RETAIL":
                return WayType.RETAIL;
            case "MILITARY":
                return WayType.MILITARY;
            case "CEMETERY":
                return WayType.CEMETERY;
            case "ORCHARD":
                return WayType.ORCHARD;
            case "ALLOTMENTS":
                return WayType.ALLOTMENTS;
            case "QUARRY":
                return WayType.QUARRY;
            case "RAILWAY":
                return WayType.RAILWAY;
            case "CONSTRUCTION":
                return WayType.CONSTRUCTION;
            case "RAIL":
                return WayType.RAIL;
            case "LIGHT_RAIL":
                return WayType.LIGHT_RAIL;
            case "PLATFORM":
                return WayType.PLATFORM;
            case "BUILDING":
                return WayType.BUILDING;
            case "SCHOOL":
                return WayType.BUILDING_SCHOOL;
            case "TRAIN_STATION":
                return WayType.TRAIN_STATION;
            case "ROOF":
                return WayType.ROOF;
            case "MOTORWAY":
                return WayType.HIGHWAY_MOTORWAY;
            case "TRUNK":
                return WayType.HIGHWAY_TRUNK;
            case "PRIMARY":
                return WayType.HIGHWAY_PRIMARY;
            case "SECONDARY":
                return WayType.HIGHWAY_SECONDARY;
            case "TERTIARY":
                return WayType.HIGHWAY_TERTIARY;
            case "RESIDENTIALWAY":
                return WayType.HIGHWAY_RESIDENTIAL;
            case "CYCLEWAY":
                return WayType.HIGHWAY_CYCLEWAY;
            case "FOOTWAY":
                return WayType.HIGHWAY_FOOTWAY;
            case "SERVICEWAY":
                return WayType.HIGHWAY_SERVICE;
            case "UNCLASSIFIED":
                return WayType.HIGHWAY_UNCLASSIFIED;
            case "PEDESTRIAN AREA":
                return WayType.PEDESTRIAN_AREA;
            case "PEDESTRIAN":
                return WayType.HIGHWAY_PEDESTRIAN;
            case "STEPS":
                return WayType.HIGHWAY_STEPS;
            case "PATH":
                return WayType.HIGHWAY_PATH;
            case "UNDEFINEDWAY":
                return WayType.HIGHWAY_UNDEFINED;
            case "APRON":
                return WayType.APRON;
            case "TAXIWAY":
                return WayType.TAXIWAY;
            case "RUNWAY":
                return WayType.RUNWAY;
            case "PARKING":
                return WayType.PARKING;
            case "PARKING_BICYCLE":
                return WayType.PARKING_BICYCLE;
            case "WATER_STREAM":
                return WayType.WATER_STREAM;
            case "WATER_RIVER":
                return WayType.WATER_RIVER;
            case "FARMLAND":
                return WayType.FARMLAND;
            case "FARMYARD":
                return WayType.FARMYARD;
            case "ROUTE_FERRY":
                return WayType.ROUTE_FERRY;
            case "WATER_DITCH":
                return WayType.WATER_DITCH;
            case "UNDEFINED":
                return WayType.UNDEFINED;
            case "PITCH":
                return WayType.PITCH;
            case "UNKNOWN":
                return WayType.UNKNOWN;
            case "BRIDGE":
                return WayType.BRIDGE;
            case "DITCH_TUNNEL":
                return WayType.WATER_DITCH_TUNNEL;
            default:
                return WayType.UNKNOWN;
        }
    }
}
