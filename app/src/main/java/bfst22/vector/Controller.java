package bfst22.vector;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;

import javafx.event.EventHandler;
import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;

import bfst22.vector.OSMParsing.OSMAddress;
import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.OSMParsing.OSMParser;
import bfst22.vector.OSMParsing.OSMPointOfInterest;
import bfst22.vector.Graph.Edge;
import bfst22.vector.Graph.Graph;
import bfst22.vector.Graph.Route;
import bfst22.vector.Graph.RouteFinder;
import bfst22.vector.Graph.TransportType;
import bfst22.vector.Util.MapMath;
import bfst22.vector.Util.NumberFormatter;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.RadixTree;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.AlertPopUp;
import bfst22.vector.gui.MapCanvas;
import bfst22.vector.gui.PointOnMap;

import org.xml.sax.SAXException;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;

/**
 * This class is responsible for the controller. It controls the main components
 * of the GUI and initialize model
 */
@SuppressWarnings("unused")
public class Controller {
    private Point lastMouse;
    private Point currentMouse;
    private boolean primaryClick;
    private boolean viewPortDebug;
    private boolean nearestPointDebug;
    private boolean routeDebug;
    private boolean routeAstar;
    private boolean analyzedPath;
    private DebugController debugController;
    private PointOfInterestController PointOfInterestController;
    private Model model;
    private ModelData modelData;
    private String searchInput;
    private Route route;
    private TransportType transport;
    private boolean shortestRoute;
    private Point pointFrom;
    private Point pointTo;
    private boolean userDragged;

    @FXML
    private MapCanvas canvas;
    @FXML
    private TextField mouseInfo;
    @FXML
    private TextField mousePos;
    @FXML
    private ComboBox<String> dropDownMenu;

    @FXML
    private TextField scaleInfo;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Text scaleText;
    @FXML
    private Polyline scaleBar;
    @FXML
    private ContextMenu rightclick;
    @FXML
    private MenuItem pontOfInterestBtn;
    @FXML
    private MenuItem debuggerBtn;

    // Test expanded search
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchFieldStart;
    @FXML
    private AnchorPane expandedSearch;
    @FXML
    private ContextMenu suggestionsStart;
    @FXML
    private TextField searchFieldEnd;
    @FXML
    private ContextMenu suggestionsEnd;
    @FXML
    private ChoiceBox<String> vehicleMenu;
    @FXML
    private Button reverseButton;
    @FXML
    private Button expandButton;
    @FXML
    private TextArea directionText;
    @FXML
    private Text totalDistance;

    public void init(Model model) {
        canvas.init(model);
        this.model = model;
        // File[] themeFolder = new File(FilePathConstant.FILE_PATH_DATA +
        // "/theme/").listFiles();
        // for (File file : themeFolder) {
        // dropDownMenu.getItems().add(file.toString().substring(16).replaceAll(".fxml",
        // ""));
        // }
        dropDownMenu.getItems().add("Default");
        dropDownMenu.getItems().add("Dark");
        dropDownMenu.getItems().add("LSD");
        dropDownMenu.getItems().add("NoColor");
        dropDownMenu.getItems().add("Simple");

        dropDownMenu.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            try {
                canvas.changeTheme((String) dropDownMenu.getValue());
            } catch (SAXException e) {
                AlertPopUp.newAlert("Parsing failed", "Try another theme");
            }
        });
        this.viewPortDebug = false;
        this.nearestPointDebug = false;
        this.shortestRoute = false;
        this.transport = TransportType.Car_Route;
        this.routeAstar = true;
        updateZoomInfo();
        populateVehicleMenu();
        makeButtonIcons();
        hideExpandedSearch();
        directionText.setEditable(false);
    }

    public void togglenearestPointDebug(boolean on) {
        this.nearestPointDebug = on;
    }

    /**
     * Sets the textual navigation for route finding
     * 
     * @param directions
     */
    public void setDirectionsText(ArrayList<String> directions) {
        if (directions == null) {
            directionText.setText("");
            return;
        }
        for (int i = 0; i < directions.size(); i++) {
            directionText.appendText(directions.get(i));
            if (i != directions.size() - 1) {
                directionText.appendText("\n");
            }
        }
    }

    public void setRouteDebug(boolean on) {
        this.routeDebug = on;
    }

    public void setAstar(boolean on) {
        this.routeAstar = on;
        if (routeDebug) {
            FindRoute();
        }
    }

    public void setAnalysedPath(boolean on) {
        this.analyzedPath = on;
        if (routeDebug) {
            FindRoute();
        }
    }

    public void toggleViewportDebug() {
        if (this.viewPortDebug == false) {
            this.viewPortDebug = true;
        } else {
            this.viewPortDebug = false;
        }
    }

    public void toggleViewportDebug(boolean on) {
        this.viewPortDebug = on;
    }

    /**
     * Triggers whenever the user scrolls. It zooms in on mapcanvas.
     * 
     * @param e contains all paremeters from the scroll (speed and such)
     */
    @FXML
    private void onScroll(ScrollEvent e) {
        double factor = Math.pow(1.05, e.getDeltaY());
        if (viewPortDebug && canvas.getViewPort().contains(canvas.mouseToModel(new Point(e.getX(), e.getY())))
                && e.isControlDown()) {
            canvas.resizeViewport(factor, false);
        } else {
            canvas.zoom(factor, e.getX(), e.getY(), false);
            updateZoomInfo();
        }
        if (debugController != null) {
            debugController.setFPS(canvas.getFps());
        }
    }

    private void updateZoomInfo() {
        updateZoomLevelText();
        updateZoomSlider();
        scaleText.setText(NumberFormatter.formatDistance(calculateScaleLevel()));
    }

    @FXML
    private void onScrollEnd(ScrollEvent e) {
        canvas.repaint();
    }

    /**
     * This triggers when the user has dragged canvas with mouse. It's used for
     * panning .
     * 
     * @param e
     */
    @FXML
    private void onMouseDragged(MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();
        if (canvas.getViewPort().contains(canvas.mouseToModel(new Point(lastMouse.getX(), lastMouse.getY())))
                && viewPortDebug && e.isControlDown()) {
            canvas.moveViewport(dx, dy);
        } else {
            canvas.pan(dx, dy);
        }
        lastMouse = new Point(e.getX(), e.getY());
        if (debugController != null) {
            debugController.setFPS(canvas.getFps());
        }
    }

    /**
     * This triggers when user uses the UI slider to scale in and out.
     * 
     * @param before the percentage of the slider before user interaction
     * @param after  the percentage of the slider after user interaction
     */
    @FXML
    private void onDrag(ObservableValue<Number> ovn, Number before, Number after) {
        if (userDragged) {
            canvas.zoomToPercentage(after.doubleValue(), canvas.getWidth() / 2,
                    canvas.getHeight() / 2);
            updateZoomLevelText();
            scaleText.setText(NumberFormatter.formatDistance(calculateScaleLevel()));
        }
        userDragged = true;
    }

    /**
     * This triggers when user clicks anywhere on the map. It's used for debugging
     * and our right click menu.
     * 
     * @param e
     */
    @FXML
    private void onMousePressed(MouseEvent e) {
        lastMouse = new Point(e.getX(), e.getY());
        if (e.isControlDown() && routeDebug) {
            pointFrom = canvas.mouseToModel(lastMouse);
            debugController.setPointFrom(NumberFormatter.formatDecimal(3, pointFrom.getX()) + " " +
                    NumberFormatter.formatDecimal(3, Math.abs(pointFrom.getY())));

        } else {
            if (e.getButton() == MouseButton.PRIMARY) {
                primaryClick = true;
            } else {
                primaryClick = false;
            }
        }

        if (e.getButton() == MouseButton.SECONDARY) {
            rightclick.show(canvas, e.getScreenX(), e.getScreenY());
        } else {
            rightclick.hide();
        }
        if (e.isShiftDown()) {
            pointTo = canvas.mouseToModel(lastMouse);
            if (routeDebug) {
                if (pointFrom != null) {
                    debugController.setPointTo(NumberFormatter.formatDecimal(3, pointTo.getX()) + " " +
                            NumberFormatter.formatDecimal(3, Math.abs(pointTo.getY())));
                    FindRoute();
                }
            }

        }
    }

    @FXML
    public void windowResized() {
        canvas.repaint();
    }

    /**
     * Is triggered when user moves cursor.
     * It's used for displaying the coordinates of the mouse and the closest address
     * to cursor.
     * 
     * @param e
     */
    @FXML
    private void onMouseMoved(MouseEvent e) {
        Point point = new Point(e.getX(), e.getY());
        point = canvas.mouseToModel(point);
        if (point == null) {
            return;
        }
        String street = ModelData.graph.getNearestRoadName(new float[] { (float) point.getX(),
                (float) point.getY() });
        mousePos.setText("lat: " + NumberFormatter.formatDecimal(3, point.getX()) + ", lon: " +
                NumberFormatter.formatDecimal(3, Math.abs(point.getY())));
        mouseInfo.setText(street);
        if (nearestPointDebug) {
            canvas.repaintNearestPointLine();
        }
    }

    /**
     * When the user right clicks and wants to add a point of interest, a sepperate
     * window opens with more settings.
     * Right click menu has it's own controller.
     * 
     * @param actionEvent
     */
    @FXML
    public void handlePointOfInterest(ActionEvent actionEvent) {
        try {
            Stage addPointOfInterest = new Stage();
            addPointOfInterest.show();
            FXMLLoader loader = new FXMLLoader(
                    View.class.getResource(FilePathConstant.FILE_PATH_RESOURCES + "AddPointOfInterest.fxml"));
            addPointOfInterest.setScene(loader.load());
            addPointOfInterest.setTitle("Add Point Of Interest");
            addPointOfInterest.centerOnScreen();
            PointOfInterestController = loader.getController();
            lastMouse = new Point(rightclick.getX() - 15, rightclick.getY() - 70);
            lastMouse = canvas.mouseToModel(lastMouse);
            PointOfInterestController.init(canvas, lastMouse);
        } catch (IOException e1) {
            AlertPopUp.newAlert("No file with PointOfInterest", "Try adding a new PointOfInterest or restart program");
        }
    }

    /**
     * Creates a new window with the debugger which has it's own controller.
     * 
     * @param actionEvent
     */
    @FXML
    public void handleDebugger(ActionEvent actionEvent) {
        if (debugController != null) {
            return;
        }
        try {
            Stage debug = new Stage();
            debug.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Debug.fxml"));
            debug.setScene(loader.load());
            debug.setResizable(false);
            debugController = loader.getController();
            debugController.init(canvas, this, debug);
            debugController.setFPS(canvas.getFps());
            canvas.setDebugger(true);
            debug.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    debugController = null;
                    analyzedPath = false;
                    viewPortDebug = false;
                    nearestPointDebug = false;
                    routeDebug = false;
                    routeAstar = true;
                }
            });
        } catch (IOException e) {
            AlertPopUp.newAlert("Couldn't find debugger",
                    "Couldn't find debug.fxml, please check file is located under resources.");
        }

    }

    /**
     * Finds the fastest route if user has selected two points. It also takes into
     * account is user has pressed car, bike or walk.
     */
    private void FindRoute() {
        if (pointFrom != null && pointTo != null) {
            RouteFinder routeFinder = new RouteFinder(ModelData.graph);
            if (transport == TransportType.Car_Route) {
                route = routeFinder.getRoute(pointFrom, pointTo, routeAstar, transport, shortestRoute);
            } else {
                route = routeFinder.getRoute(pointFrom, pointTo, routeAstar, transport, shortestRoute);
            }
            canvas.setRoute(route, analyzedPath);
            canvas.repaint();
            if (route != null) {
                totalDistance.setText("Total distance: " + NumberFormatter.formatDistance(route.getTotalDistance())
                        + ", Travel Time: " + route.getTravelTime());
                setDirectionsText(route.getDirectionsString());
            }
            if (debugController != null) {
                debugController.setDistance(NumberFormatter.formatDistance(route.getTotalDistance()));
            }
            if (route == null) {
                AlertPopUp.newAlert("Route not found", "No available route was found, try changing the transport type");
                setDirectionsText(null);
                totalDistance.setText("Total distance:");
            }
        }
    }

    /**
     * Resets the route finding on the map and the settings for it.
     * 
     * @param event
     * @throws IOException
     */
    @FXML
    private void resetButtonPressed(ActionEvent event) throws IOException {
        event.consume();
        searchFieldStart.setText("");
        searchFieldEnd.setText("");
        pointFrom = null;
        pointTo = null;
        totalDistance.setText("Total Distance:");
        setDirectionsText(null);
        canvas.setPointFrom(pointFrom);
        canvas.setPointTo(pointTo);
        canvas.setRoute(null, false);
        canvas.repaint();
    }

    /**
     * This method triggers whenever a something is typed in the search box.
     * It finds suggestions similiar to searched string.
     * 
     * @param e has the string for the search term
     */
    @FXML
    private void getSearchInput(KeyEvent e) {
        if (e.getTarget() == searchFieldStart) {
            if (searchFieldStart.getText().length() > 0) {
                autoComplete(searchFieldStart.getText().toLowerCase(), searchFieldStart, suggestionsStart);
            }
        } else {
            if (searchFieldEnd.getText().length() > 0) {
                autoComplete(searchFieldEnd.getText().toLowerCase(), searchFieldEnd, suggestionsEnd);
            }
        }
    }

    /**
     * This method simply allows the user to use arrow keys to guide through
     * suggestions.
     * 
     * @param e
     */
    @FXML
    private void navigateSuggestionMenu(KeyEvent e) {
        ContextMenu temp;
        if (e.getTarget() == searchFieldStart) {
            temp = suggestionsStart;
        } else {
            temp = suggestionsEnd;
        }
        if (e.getCode() == KeyCode.DOWN || e.getCode() == KeyCode.UP) {
            temp.getSkin().getNode().lookup(".menu-item").requestFocus();
        } else if (e.getCode() == KeyCode.ENTER) {
            temp.hide();
        }
    }

    /**
     * Switches the from and to in navigation
     * 
     * @param event
     * @throws SAXException
     */
    @FXML
    private void reverseButtonPressed(ActionEvent event) throws SAXException {
        exchangeAdresses();
    }

    /**
     * Instead of entering an address, the user can right click and choose a point
     * to navigate from
     * 
     * @param event
     */
    @FXML
    private void setPointFrom(ActionEvent event) {
        pointFrom = canvas.mouseToModel(lastMouse);
        searchFieldStart.setText("Coordinate : " + NumberFormatter.formatDecimal(2, pointFrom.getX()) + ", "
                + NumberFormatter.formatDecimal(2, pointFrom.getY()));
        canvas.setPointFrom(pointFrom);
        FindRoute();
        canvas.repaint();
    }

    /**
     * Instead of entering an address, the user can right click and choose a point
     * to navigate to
     * 
     * @param event
     */
    @FXML
    private void setPointTo(ActionEvent event) {
        pointTo = canvas.mouseToModel(lastMouse);
        searchFieldEnd.setText("Coordinate : " + NumberFormatter.formatDecimal(2, pointTo.getX()) + ", "
                + NumberFormatter.formatDecimal(2, pointTo.getY()));
        canvas.setPointTo(pointTo);
        FindRoute();
        canvas.repaint();
    }

    /**
     * Simply triggers when the user presses the naviation button. The navigation is
     * hidden by default.
     * 
     * @param event
     */
    @FXML
    private void expandButtonPressed(ActionEvent event) {
        if (expandedSearch.isVisible()) {
            expandedSearch.setVisible(false);
        } else {
            expandedSearch.setVisible(true);
        }
    }

    /**
     * Handles the menu for car/walk/bike navigation
     * 
     * @param event
     */
    @FXML
    private void changeVehicle(ActionEvent event) {
        switch (vehicleMenu.getValue()) {
            case "Car":
                transport = TransportType.Car_Route;
                shortestRoute = false;
                vehicleMenu.setValue("Car");
                break;
            case "Walk":
                transport = TransportType.Walk_Route;
                shortestRoute = true;
                vehicleMenu.setValue("Walk");
                break;
            case "Bike":
                transport = TransportType.Bike_Route;
                shortestRoute = true;
                vehicleMenu.setValue("Bike");
                break;
        }
        FindRoute();
        canvas.repaint();
    }

    /**
     * Hides navigation.
     */
    private void hideExpandedSearch() {
        expandedSearch.setVisible(false);
    }

    /**
     * Method for auto suggestion similar addresses to the users input string, it
     * gets called whenever a searchField is updated.
     * 
     * @param string      search term
     * @param searchField
     * @param suggestions
     */
    private void autoComplete(String string, TextField searchField, ContextMenu suggestions) {
        List<CustomMenuItem> menuItems = new ArrayList<>();
        RadixTree<String> tree = ModelData.trieAddress;
        List<Map.Entry<String, String>> result = ModelData.trieAddress.getEntriesWithPrefix(string);
        // Creating all the result-labels for the Menu-items list.
        for (Map.Entry<String, String> entry : result) {
            Label entryLabel = new Label(entry.getKey());
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            menuItems.add(item);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    searchField.setText(entry.getKey());
                    searchInput = entry.getKey();
                    // Drawing the point for addresses.
                    String[] coordinate = entry.getValue().split(",");
                    Point point = new Point(Double.parseDouble(coordinate[0].trim()),
                            Double.parseDouble(coordinate[1].trim()));
                    if (searchField == searchFieldStart && point != null) {
                        pointFrom = point;
                        canvas.setPointFrom(pointFrom);
                        canvas.panToPoint(pointFrom.getX(), pointFrom.getY(), 5);
                        FindRoute();
                        updateZoomLevelText();
                        updateZoomSlider();
                        scaleText.setText(NumberFormatter.formatDistance(calculateScaleLevel()));
                    } else {
                        pointTo = point;
                        canvas.setPointTo(pointTo);
                        canvas.panToPoint(pointTo.getX(), pointTo.getY(), 5);
                        FindRoute();
                        updateZoomLevelText();
                        updateZoomSlider();
                        scaleText.setText(NumberFormatter.formatDistance(calculateScaleLevel()));
                    }
                    suggestions.hide();
                }
            });
        }
        // Clearing the list, then adding the first 20 menu-item labels to the context
        // menu.
        suggestions.getItems().clear();
        for (int i = 0; i < 20 && i < menuItems.size(); i++) {
            suggestions.getItems().add(menuItems.get(i));
        }
        // positioning of the context dropdown menu.
        if (!suggestions.isShowing()) {
            suggestions.show(searchField, Side.BOTTOM, 0, 0);
        }
    }

    public void setTransport(TransportType carRoute) {
        this.transport = carRoute;
        if (routeDebug) {
            FindRoute();
        }
    }

    public void setRouteType(boolean b) {
        this.shortestRoute = b;
        if (routeDebug) {
            FindRoute();
        }
    }

    private void updateZoomLevelText() {
        scaleInfo.setText(NumberFormatter.formatDecimal(1, canvas.getZoomPercent()) + "%");
    }

    private void updateZoomSlider() {
        userDragged = false;
        zoomSlider.setValue(canvas.getZoomPercent());
    }

    /**
     * Uses haversine method to calculate distance between the UI element scalebar
     * using lat and lon.
     * 
     * @return
     */
    private double calculateScaleLevel() {
        Bounds boundsInScene = scaleBar.localToScene(scaleBar.getBoundsInLocal());
        ;
        Point start = new Point(boundsInScene.getMinX(), boundsInScene.getMinY());
        Point end = new Point(boundsInScene.getMaxX(), boundsInScene.getMinY());
        start = canvas.mouseToModel(start);
        end = canvas.mouseToModel(end);
        return MapMath.haversineDistance(start, end);
    }

    /**
     * Converts the scale level to meter if it's under 1 km to be more precise.
     * 
     * @param distance
     * @return
     */
    private String distanceToStringFormatted(double distance) {
        if (distance < 1) {
            distance *= 1000;
            return Math.round(distance) + "m";
        }
        return Math.round(distance) + " km";
    }

    /**
     * Sets the icon to navigation buttons using a file.
     */
    private void makeButtonIcons() {
        InputStream directionDark = getClass().getResourceAsStream("icons/directionDark.png");
        Image expandButtonImage = new Image(directionDark);
        ImageView expandButtonIcon = new ImageView(expandButtonImage);
        expandButton.setGraphic(expandButtonIcon);
        InputStream reverseArrows = getClass().getResourceAsStream("icons/reverseArrows.png");
        Image reverseButtonImage = new Image(reverseArrows);
        ImageView reverseButtonIcon = new ImageView(reverseButtonImage);
        reverseButton.setGraphic(reverseButtonIcon);
    }

    private void populateVehicleMenu() {
        String[] options = { "Car", "Bike", "Walk" };
        vehicleMenu.getItems().addAll(options);
        vehicleMenu.setValue(options[0]);
    }

    /**
     * Switches the to and from in navigation.
     */
    private void exchangeAdresses() {
        String start = searchFieldStart.getText();
        String end = searchFieldEnd.getText();
        Point temp = pointFrom;
        pointFrom = pointTo;
        pointTo = temp;
        canvas.setPointTo(pointTo);
        canvas.setPointFrom(pointFrom);
        FindRoute();
        searchFieldStart.setText(end);
        searchFieldEnd.setText(start);
        canvas.repaint();
    }
}