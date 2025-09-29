package bfst22.vector.gui;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Random;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import bfst22.vector.OSMParsing.OSMReader;
import bfst22.vector.Util.WayType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * This class is responsible of invoking when a new theme is being loaded.
 */
public class Styler {
    EnumMap<WayType, WayStyle> theme;

    /**
     * Creates a new object of a theme chosen
     * 
     * @param themeFileName the name of the theme
     * @throws SAXException when parsing the FXML file it will catch any error
     * @throws IOException  exception is thrown if theme doesn't exist in the
     *                      directory.
     */
    Styler(String themeFileName) throws SAXException, IOException {
        OSMReader reader = new OSMReader();
        ThemeParser parse = new ThemeParser(reader);
        parse.reader.parseFXML(themeFileName, (ContentHandler) parse);
        this.theme = parse.getTheme();
    }

    public boolean getDraw(WayType type, double zoomFactor) {
        if (theme.get(type) == null) {
            return false;
        }
        return theme.get(type).getDepth() < zoomFactor;
    }

    /**
     * Draws the all the ways depending on what was parsed from the FXML file like,
     * color, stroke etc.
     * 
     * @param gc               the canvas
     * @param line
     * @param type             the waytype (wood, building etc.)
     * @param zoomFactor       The zoomfactor which defines at what scale it is
     *                         being drawn
     * @param dynamicLinewidth line width
     */
    public void drawer(GraphicsContext gc, Drawable line, WayType type, double zoomFactor, double dynamicLinewidth) {
        try {
            WayStyle style = theme.get(type);
            if (style.getFill()) {
                gc.setGlobalAlpha(style.getOpacity());
                gc.setFill(style.getInnerColor());
                line.fill(gc);
            }
            if (style.isRandomColor()) {
                style.setOutLineColor(Color.valueOf(randomHexColorGenerator()));
                style.setInnerColor(Color.valueOf(randomHexColorGenerator()));
            }
            if (style.getOutline()) {
                gc.setStroke(style.getOutLineColor());
                gc.setLineWidth(style.getLinewidth() * 1.01);
                gc.setLineCap(style.getCap());
                gc.setLineJoin(style.getJoin());
                line.draw(gc);
            }
            if (style.getDraw()) {
                gc.setStroke(style.getOuterColor());
                if (type.toString().startsWith("HIGHWAY_")) {
                    if (type == WayType.HIGHWAY_CYCLEWAY || type == WayType.HIGHWAY_FOOTWAY) {
                        gc.setLineWidth(style.getLinewidth());
                    } else {
                        gc.setLineWidth(dynamicLinewidth);
                    }
                } else {
                    gc.setLineWidth(style.getLinewidth());
                }
                if (style.getLineDashes() != null) {
                    gc.setLineDashes(style.getLineDashes()[0], style.getLineDashes()[1]);
                } else {
                    gc.setLineDashes(0, 0);
                }
                line.draw(gc);
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * Method is used for our party theme which generates a random color
     * 
     * @return a randomized color
     */
    public String randomHexColorGenerator() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        String colorCode = String.format("#%06x", rand_num);
        return colorCode;
    }
}