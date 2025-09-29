package bfst22.vector.gui;

import javafx.scene.transform.NonInvertibleTransformException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;

import org.xml.sax.SAXException;
import bfst22.vector.Model;
import bfst22.vector.ModelData;
import bfst22.vector.Graph.Route;
import bfst22.vector.OSMParsing.OSMPointOfInterest;
import bfst22.vector.Util.KdSplitLineList;
import bfst22.vector.Util.KdTree;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.WayType;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import bfst22.vector.Util.XYPoint;

/**
 * This class is responsible for the whole canvas where the map is being drawn
 * on
 * It extends the JavaFX Canvas which allows os to draw on it.
 */
public class MapCanvas extends Canvas {
    public final int MAXSCALE = 430000;
    public final int MINSCALE = 200;
    Model model;
    Affine trans;
    float zoomPercent;
    float zoomFactor;
    GraphicsContext gc;
    Styler styles;
    ViewPort viewPort;
    KdSplitLineList splits;
    EnumMap<WayType, Boolean> toDraw;
    EnumMap<WayType, ArrayList<XYPoint>> resultRange;
    EnumMap<WayType, KdTree<XYPoint>> tree;
    ArrayList<Drawable> coastLine;
    Point PointFrom;
    Point PointTo;
    boolean WayTypedebugger;
    boolean viewPortDebug;
    boolean useZoomFactor;
    boolean doRangeSearch;
    PolyLine nearestPointLine;
    boolean makeNewRangeSearch;
    boolean rangeSearchDone;
    double dynamicLinewidth;
    int fps;
    int kdsplits;
    int drawedObjects;
    Point minPoint, maxPoint;
    Route route;
    boolean includeAnalysedPath;

    /**
     * This constructor creates the canvas. It initializes the affine object which
     * controls the panning and zooming.
     * It also initializes zoom percent and zoomfactor which is used for controlling
     * max and min zoom.
     * It creates a Graphics Context2D, which allows us to draw.
     * We deffine a default theme that is loaded with the app.
     * 
     * @throws SAXException
     * @throws IOException
     */
    public MapCanvas() {
        this.trans = new Affine();
        this.zoomPercent = 1.0f;
        this.zoomFactor = 1.0f;
        this.gc = this.getGraphicsContext2D();
        try {
            this.styles = new Styler("theme/themeDefault.fxml");
        } catch (IOException e) {
            AlertPopUp.newAlert("Theme not found!", "Please make sure the themeDefaut.fxml is located in data/theme.");
        } catch (SAXException e) {
            AlertPopUp.newAlert("Theme has error!",
                    "There was an error in the theme. Please make sure file is not corrupt.");
        }
        this.viewPort = new ViewPort(0.56f * Model.minLonBoundary, -Model.minLatBoundary, 0.56f * Model.maxLonBoundary,
                -Model.maxLatBoundary);
        this.coastLine = new ArrayList<Drawable>(ModelData.coastLine);
        resultRange = new EnumMap<WayType, ArrayList<XYPoint>>(WayType.class);
        tree = ModelData.segmentsTree;
        RangeSearch();
        toDraw = new EnumMap<WayType, Boolean>(WayType.class);
        for (WayType type : WayType.values()) {
            toDraw.put(type, true);
            resultRange.put(type, new ArrayList<XYPoint>());
        }
        this.viewPortDebug = false;
        this.WayTypedebugger = false;
        this.useZoomFactor = true;
        this.makeNewRangeSearch = true;
        this.rangeSearchDone = false;
        this.doRangeSearch = true;
        kdsplits = 0;
    }

    public void init(Model model) {
        this.model = model;
        double x = -model.getMinLonBoundary();
        double y = -model.getMaxLatBoundary();
        pan(x, y);
        zoom(640.0f / (model.getMaxLonBoundary() - model.getMinLonBoundary()), 0.0, 0.0, true);
    }

    /**
     * Changes the theme that active theme
     * 
     * @param theme a string for the theme that is also the file name
     * @throws SAXException exception is thrown in case of FXML error
     * @throws IOException  exception is thrown if the theme doesn't exits.
     */
    public void changeTheme(String theme) throws SAXException {
        try {
            this.styles = new Styler("theme/theme" + theme + ".fxml");
        } catch (IOException e) {
            AlertPopUp.newAlert("Theme not found.", "Theme wasn't found, please try another one.");
        }
        repaint();
    }

    /**
     * This method repaints the whole map canvas by drawing all ways. It only draws
     * the ways that is inside the
     * KdTree using the rangesearch method. It also draws the navigation if the user
     * has that active.
     * Points of interest also gets drawn.
     * The method is called whenever the canvas is updated. This happens whenever
     * the user zooms, pans, changes theme etc.
     */
    public void repaint() {
        long startTime = System.nanoTime();
        drawedObjects = 0;
        gc.setTransform(new Affine());
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.valueOf("#A3BFF4"));
        gc.fillRect(0.0, 0.0, getWidth(), getHeight());
        gc.setTransform(trans);
        if (0.00008 < 2 / Math.sqrt(trans.determinant())) {
            dynamicLinewidth = 2 / Math.sqrt(trans.determinant());
        } else {
            dynamicLinewidth = 0.00008;
        }
        if (toDraw.get(WayType.COASTLINE)) {
            for (Drawable line : coastLine) {
                styles.drawer(gc, line, WayType.COASTLINE, getZoomFactorDivided(), dynamicLinewidth);
            }
        }
        if (!viewPortDebug) {
            minPoint = new Point(0.0f, 0.0f);
            maxPoint = new Point(getWidth(), getHeight());
            minPoint = mouseToModel(minPoint);
            maxPoint = mouseToModel(maxPoint);
            viewPort.setBounds(minPoint.getX(), minPoint.getY(), maxPoint.getX(), maxPoint.getY());
        }
        for (WayType type : resultRange.keySet()) {
            if (useZoomFactor) {
                if (toDraw.get(type) != null) {
                    if (!toDraw.get(type)
                            || !styles.getDraw(type, getZoomFactorDivided())) {
                        continue;
                    }
                }

            } else {
                if (toDraw.get(type) != null) {
                    if (!toDraw.get(type)) {
                        continue;
                    }
                }
            }
            RangeSearch(type);
            if (resultRange.get(type) == null) {
                continue;
            }
            for (XYPoint line : resultRange.get(type)) {
                if (line instanceof Drawable) {
                    Drawable temp = (Drawable) line;
                    styles.drawer(gc, temp, type, zoomPercent, dynamicLinewidth);
                    drawedObjects++;
                    if (viewPortDebug) {
                        gc.setStroke(Color.LIGHTGREEN);
                        gc.setLineWidth(dynamicLinewidth);
                    }
                } else if (line instanceof Label) {
                    Label temp = (Label) line;
                    temp.drawLabel(gc, dynamicLinewidth);
                }
            }
        }
        if (ModelData.pointOfInterest != null) {
            for (OSMPointOfInterest favoritePoint : ModelData.pointOfInterest) {
                double radius = dynamicLinewidth * 3;
                gc.setFill(Color.valueOf(favoritePoint.getColor()));
                gc.fillOval(favoritePoint.getXPoint(), favoritePoint.getYPoint(), radius, radius);
                gc.setFont(new Font("Helvetica", dynamicLinewidth * 4));
                gc.fillText(favoritePoint.getName(), favoritePoint.getXPoint(), favoritePoint.getYPoint() + 0.0007);
            }
        }
        if (splits != null && viewPortDebug) {
            splits.setShowCount(kdsplits - 1);
            splits.draw(gc);
        }
        if (viewPortDebug) {
            viewPort.draw(gc);
        }
        repaintRoute();
        drawPoint(PointTo, IconEnum.POINT_TO);
        drawPoint(PointFrom, IconEnum.POINT_FROM);
        if (PointFrom != null) {
            double radius = dynamicLinewidth * 2;
            Icon icon = new Icon(PointFrom.getX(), PointFrom.getY(), IconEnum.POINT_FROM);
            icon.draw(gc, radius);
        }
        this.fps = (int) (1000000000.0 / (System.nanoTime() - startTime));
        // if (viewPortDebug) {
        // KdSplitLineList split = ModelData.segmentsTree.splits;
        // split.setShowCount(kdsplits);
        // split.draw(gc);
        // }
    }

    /**
     * Draws a line that goes from the cursor to the nearest point on a road. It's
     * only used for debugging.
     */
    public void repaintNearestPointLine() {
        repaint();
        gc.setTransform(new Affine());
        GraphicsContext gc = getGraphicsContext2D();
        gc.setTransform(trans);
        gc.setStroke(Color.RED);
        double linewidth = 2.0 / Math.sqrt(trans.determinant());
        gc.setLineWidth(linewidth);
        nearestPointLine = ModelData.graph.getNearestRoadNameLine();
        if (nearestPointLine != null) {
            nearestPointLine.draw(gc);
        }
    }

    /**
     * Draws a point on the map depending on the task. Could be for navigation,
     * point of interest etc.
     * 
     * @param point a simple 2d point
     * @param type  descrbibes what task it is which corresponds to a icon
     */
    public void drawPoint(Point point, IconEnum type) {
        if (point != null) {
            double radius = dynamicLinewidth * 2;
            Icon icon = new Icon(point.getX(), point.getY(), type);
            icon.draw(gc, radius);
        }
    }

    /**
     * Repaints the navigation route if user has that active
     */
    public void repaintRoute() {
        if (route == null) {
            return;
        }
        gc.setTransform(new Affine());
        GraphicsContext gc = getGraphicsContext2D();
        gc.setTransform(trans);
        double linewidth = 2.0 / Math.sqrt(trans.determinant());
        gc.setLineWidth(linewidth);
        if (includeAnalysedPath) {
            gc.setStroke(Color.BLACK);
            route.drawAnalysedArea(gc);
        }
        gc.setStroke(Color.RED);
        route.draw(gc);
    }

    public void setRoute(Route route, boolean includeAnalysedPath) {
        this.route = route;
        this.includeAnalysedPath = includeAnalysedPath;
    }

    public void viewPortDebug(boolean on) {
        viewPortDebug = on;
        viewPort.scale(0.2f);
        repaint();
    }

    public void moveViewport(double dx, double dy) {
        viewPort.move(dx / trans.getMxx(), dy / trans.getMyy());
        repaint();
    }

    public ViewPort getMaxViewPort() {
        return new ViewPort(0.56f * Model.minLonBoundary, -Model.minLatBoundary, 0.56f * Model.maxLonBoundary,
                -Model.maxLatBoundary);
    }

    public void SelectWayTypeToDraw(WayType type, boolean draw) {
        toDraw.put(type, draw);
        repaint();
    }

    public double getZoomFactorDivided() {
        return zoomFactor / 10000;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public double getZoomPercent() {
        return zoomPercent;
    }

    public void setDebugger(boolean on) {
        this.WayTypedebugger = on;
    }

    /**
     * Does a range search and updates all the ways inside the KdTree tree / the
     * users viewport.
     */
    public void RangeSearch() {
        for (WayType wayType : WayType.values()) {
            if (tree.get(wayType) != null) {
                resultRange.put(wayType, tree.get(wayType).RangeSearch(viewPort, kdsplits, viewPortDebug));
            }
        }
        splits = tree.get(WayType.BUILDING).getSplits();
    }

    public void RangeSearch(WayType wayType) {
        if (tree.get(wayType) != null) {
            resultRange.put(wayType, tree.get(wayType).RangeSearch(viewPort, kdsplits, viewPortDebug));
        }
        splits = tree.get(WayType.BUILDING).getSplits();
    }

    public void resizeViewport(double factor, boolean doRangeSearch) {
        factor = 1 - factor + 1;
        viewPort.scale(factor);
        if (!doRangeSearch) {
            turnOffRangeSearch();
        }
        repaint();
    }

    public void showNextSplit() {
        splits.showNext();
        repaint();
    }

    public void removeLastSplit() {
        splits.removeLast();
        repaint();
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public float getPercentageZoom(double scale) {
        double onepercent = 100d / (MAXSCALE - MINSCALE);
        float percentage = (float) ((scale - MINSCALE) * onepercent);
        return percentage;
    }

    /**
     * This method pans the canvas to the searched address.
     */
    public void panToPoint(double x, double y, int toZoom) {
        Point midPoint = viewPort.getMidPoint();

        double coordinateX = x;
        double coordinateY = y;

        double dx = midPoint.getX() - coordinateX;
        double dy = midPoint.getY() - coordinateY;

        trans.appendTranslation(dx, dy);
        zoomToPercentage(toZoom, getWidth() / 2, getHeight() / 2);
        repaint();
    }

    /**
     * Zoom method scales in and out. It gets called when the user either scrolls or
     * uses the UI slider
     * 
     * @param factor        describes the factor of the zoom. The faster the user
     *                      scrolls, the higher the factor will be.
     * @param x
     * @param y
     * @param doRangeSearch
     */
    public void zoom(double factor, double x, double y, boolean doRangeSearch) {
        double newScale = trans.getMxx() * factor;
        if (newScale <= MINSCALE) {
            factor = MINSCALE / trans.getMxx();
        } else if (newScale >= MAXSCALE) {
            factor = MAXSCALE / trans.getMxx();
        }
        zoomFactor *= factor;
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        zoomPercent = getPercentageZoom(trans.getMxx());
        if (!doRangeSearch) {
            turnOffRangeSearch();
        }
        repaint();
    }

    private void turnOffRangeSearch() {
        this.doRangeSearch = false;
    }

    public void zoomToPercentage(double percentage, double x, double y) {
        percentage = percentage / 100;
        double targetScale = (MAXSCALE - MINSCALE) * percentage;
        double factor = targetScale / trans.getMxx();
        zoom(factor, x, y, true);
    }

    /**
     * Does a simple conversion of a the XY position of a point to a geographical
     * point on the map.
     * 
     * @param point
     * @return
     */
    public Point mouseToModel(Point point) {
        try {
            Point2D temp = new Point2D(point.getX(), point.getY());
            temp = trans.inverseTransform(temp);
            return new Point(temp.getX(), temp.getY());
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException((Throwable) e);
        }
    }

    public Model getModel() {
        return this.model;
    }

    public ViewPort getViewPort() {
        return this.viewPort;
    }

    public int getFps() {
        return fps;
    }

    public void toggleZoomFactor() {
        if (this.useZoomFactor == false) {
            this.useZoomFactor = true;
        } else {
            useZoomFactor = false;
        }
        repaint();
    }

    public void addToSplitLine() {
        kdsplits++;
        repaint();
    }

    public void removeSplitLine() {
        if (kdsplits - 1 >= 0) {
            kdsplits--;
        }
        repaint();
    }

    public void setPointFrom(Point point) {
        this.PointFrom = point;
    }

    public void setPointTo(Point point) {
        this.PointTo = point;
    }
}