package bfst22.vector.gui;

import java.util.EnumMap;

import org.xml.sax.SAXException;

import bfst22.vector.OSMParsing.OSMReader;
import bfst22.vector.OSMParsing.SAXAdapter;
import bfst22.vector.Util.WayType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import org.xml.sax.Attributes;

/**
 * This class is responsible for parsing a FXML file into a usable theme for the
 * map
 * It keeps track of what waytype is it and all it's features like fill color,
 * stroke etc.
 */
public class ThemeParser extends SAXAdapter {
    EnumMap<WayType, WayStyle> theme;
    WayStyle temp;
    String wayKey;
    String wayValue;
    WayType waytype;
    public OSMReader reader;

    public ThemeParser(OSMReader reader) {
        this.reader = reader;
        this.theme = new EnumMap<WayType, WayStyle>(WayType.class);
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {

    }

    /**
     * Goes through all ways and sets the different parameters passed from the FXML
     * file like color, stroke etc.
     */
    @Override
    @SuppressWarnings("all")
    public void startElement(String uri, String localName, String qName, Attributes atts) {

        switch (qName) {
            case "WayType": {
                wayKey = atts.getValue("key").toLowerCase();
                wayValue = atts.getValue("value").toLowerCase();
                temp = new WayStyle();
                break;
            }
            case "innerColor": {
                String iColor = atts.getValue("value");
                temp.setInnerColor(iColor);
                break;
            }
            case "outerColor": {
                String oColor = atts.getValue("value");
                temp.setOuterColor(oColor);
                break;
            }
            case "linewidth": {
                double linewidth = Double.parseDouble(atts.getValue("value"));
                temp.setLinewidth(linewidth);
                break;
            }
            case "cap": {
                String value = atts.getValue("value").toLowerCase();
                StrokeLineCap cap = StrokeLineCap.BUTT;
                if (value.equals("butt")) {
                    cap = StrokeLineCap.BUTT;
                } else if (value.equals("round")) {
                    cap = StrokeLineCap.ROUND;
                } else if (value.equals("square")) {
                    cap = StrokeLineCap.SQUARE;
                }
                temp.setCap(cap);
                break;
            }
            case "join": {
                String value = atts.getValue("value").toLowerCase();
                StrokeLineJoin join = StrokeLineJoin.BEVEL;
                switch (value) {
                    case "bevel":
                        join = StrokeLineJoin.BEVEL;
                        break;
                    case "miter":
                        join = StrokeLineJoin.MITER;
                        break;
                    case "round":
                        join = StrokeLineJoin.ROUND;
                        break;
                }
                temp.setJoin(join);
                break;
            }
            case "linedashes": {
                double value = Double.parseDouble(atts.getValue("value"));
                temp.setLineDashes(new Double[] { value, value });
                break;
            }
            case "opacity": {
                double opacity = Double.parseDouble(atts.getValue("value"));
                temp.setOpacity(opacity);
                break;
            }
            case "depth": {
                double depth = Double.parseDouble(atts.getValue("value"));
                temp.setDepth(depth);
                break;
            }
            case "fill": {
                String value = atts.getValue("value").toLowerCase();
                if (value.equals("true"))
                    temp.setFill(true);
                break;
            }
            case "draw": {
                String value = atts.getValue("value");
                boolean draw = false;
                if (value.equals("true")) {
                    draw = true;
                }
                temp.setDraw(draw);
                break;
            }
            case "outline": {
                String value = atts.getValue("value");
                if (value.equals("true"))
                    temp.setOutline(true);
                break;
            }
            case "random": {
                String value = atts.getValue("value");
                if (value.equals("true"))
                    temp.setRandomColor(true);
                break;
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "WayType": {
                parseTagInformation(wayKey, wayValue);
                if (temp != null && waytype != null) {
                    theme.putIfAbsent(waytype, temp);
                }
                resetValues();
                break;
            }
        }
    }

    public EnumMap<WayType, WayStyle> getTheme() {
        return theme;
    }

    public void resetValues() {
        wayKey = null;
        wayValue = null;
        waytype = null;
    }

    /**
     * Parses the type of way
     * 
     * @param key   the main way type, like highway
     * @param value the value of the way type, for highway this could be motorway
     */
    public void parseTagInformation(String key, String value) {
        switch (key) {
            case "place": {
                switch (value) {
                    case "city":
                        waytype = WayType.PLACE_CITY;
                        break;
                    case "town":
                        waytype = WayType.PLACE_TOWN;
                        break;
                    case "suburb":
                        waytype = WayType.PLACE_SUBURB;
                        break;
                    case "neighbourhood":
                        waytype = WayType.PLACE_NEIGHBOURHOOD;
                        break;
                    case "village":
                        waytype = WayType.PLACE_VILLAGE;
                        break;
                }
            }
                break;
            case "highway": {
                switch (value) {
                    case "motorway_link":
                    case "motorway": {
                        waytype = WayType.HIGHWAY_MOTORWAY;
                        break;
                    }
                    case "trunk_link":
                    case "trunk": {
                        waytype = WayType.HIGHWAY_TRUNK;
                        break;
                    }
                    case "primary_link":
                    case "primary": {
                        waytype = WayType.HIGHWAY_PRIMARY;
                        break;
                    }
                    case "secondary_link":
                    case "secondary": {
                        waytype = WayType.HIGHWAY_SECONDARY;
                        break;
                    }
                    case "tertiary_link":
                    case "tertiary": {
                        waytype = WayType.HIGHWAY_TERTIARY;
                        break;
                    }
                    case "residential": {
                        waytype = WayType.HIGHWAY_RESIDENTIAL;
                        break;
                    }
                    case "pedestrian": {
                        waytype = WayType.HIGHWAY_PEDESTRIAN;
                        break;
                    }
                    case "unclassified": {
                        waytype = WayType.HIGHWAY_UNCLASSIFIED;
                        break;
                    }
                    case "service": {
                        waytype = WayType.HIGHWAY_SERVICE;
                        break;
                    }
                    case "cycleway": {
                        waytype = WayType.HIGHWAY_CYCLEWAY;
                        break;
                    }
                    case "traffic_signal": {
                        break;
                    }
                    case "path":
                    case "footway":
                    case "bridleway":
                    case "track":
                    case "steps": {
                        waytype = WayType.HIGHWAY_FOOTWAY;
                        break;
                    }
                    default: {
                        waytype = WayType.HIGHWAY_UNDEFINED;
                        break;
                    }
                }
                break;
            }
            case "tourism": {
                switch (value) {
                    case "picnic_site": {
                        waytype = WayType.PICNIC_SITE;
                        break;
                    }
                }
                break;
            }
            case "building": {
                switch (value) {
                    case "school": {
                        waytype = WayType.BUILDING_SCHOOL;
                        break;
                    }
                    case "train_station": {
                        waytype = WayType.TRAIN_STATION;
                        break;
                    }
                    case "roof": {
                        waytype = WayType.ROOF;
                        break;
                    }
                    case "detached": {
                        waytype = WayType.DETACHED;
                        break;
                    }
                    case "allotment_house":
                    case "hangar":
                    case "yes": {
                        waytype = WayType.BUILDING;
                        break;
                    }
                }
                break;
            }
            case "landuse": {
                switch (value) {
                    case "residential": {
                        waytype = WayType.RESIDENTIAL;
                        break;
                    }
                    case "quarry": {
                        waytype = WayType.QUARRY;
                        break;
                    }
                    case "farmland": {
                        waytype = WayType.FARMLAND;
                        break;
                    }
                    case "farmyard": {
                        waytype = WayType.FARMYARD;
                        break;
                    }
                    case "forest": {
                        waytype = WayType.FOREST;
                        break;
                    }
                    case "meadow": {
                        waytype = WayType.MEADOW;
                        break;
                    }
                    case "industrial": {
                        waytype = WayType.INDUSTRIAL;
                        break;
                    }
                    case "recreation_ground":
                    case "grass": {
                        waytype = WayType.GRASS;
                        break;
                    }
                    case "retail": {
                        waytype = WayType.RETAIL;
                        break;
                    }
                    case "military": {
                        waytype = WayType.MILITARY;
                        break;
                    }
                    case "cemetery": {
                        waytype = WayType.CEMETERY;
                        break;
                    }
                    case "orchard": {
                        waytype = WayType.ORCHARD;
                        break;
                    }
                    case "allotments": {
                        waytype = WayType.ALLOTMENTS;
                        break;
                    }
                    case "construction": {
                        waytype = WayType.CONSTRUCTION;
                        break;
                    }
                    case "railway": {
                        waytype = WayType.RAILWAY;
                        break;
                    }
                }
                break;
            }
            case "route": {
                switch (value) {
                    case "ferry": {
                        waytype = WayType.FERRY;
                        break;
                    }
                }
                break;
            }
            case "natural": {
                switch (value) {
                    case "water": {
                        waytype = WayType.WATER;
                        break;
                    }
                    case "coastline": {
                        waytype = WayType.COASTLINE;
                        break;
                    }
                    case "scrub": {
                        waytype = WayType.SCRUB;
                        break;
                    }
                    case "wood": {
                        waytype = WayType.WOOD;
                        break;
                    }
                    case "wetland": {
                        waytype = WayType.WETLAND;
                        break;
                    }
                    case "sand": {
                        waytype = WayType.SAND;
                        break;
                    }
                    case "beach": {
                        waytype = WayType.BEACH;
                        break;
                    }
                    case "grassland": {
                        waytype = WayType.GRASSLAND;
                        break;
                    }
                    case "heath": {
                        waytype = WayType.HEATH;
                        break;
                    }
                }
                break;
            }
            case "water": {
                switch (value) {
                    case "ditch": {
                        waytype = WayType.WATER_DITCH;
                        break;
                    }
                    case "ditch_tunnel": {
                        waytype = WayType.WATER_DITCH_TUNNEL;
                        break;
                    }
                }
                break;
            }
            case "railway": {
                switch (value) {
                    case "rail": {
                        waytype = WayType.RAIL;
                        break;
                    }
                    case "light_rail": {
                        waytype = WayType.LIGHT_RAIL;
                        break;
                    }
                    case "platform": {
                        waytype = WayType.PLATFORM;
                        break;
                    }
                }
                break;
            }
            case "man_made": {
                switch (value) {
                    case "pier": {
                        waytype = WayType.PIER;
                        break;
                    }
                    case "breakwater": {
                        waytype = WayType.BREAKWATER;
                        break;
                    }
                    case "bridge": {
                        waytype = WayType.BRIDGE;
                        break;
                    }
                    case "embankment": {
                        waytype = WayType.EMBANKMENT;
                        break;
                    }
                }
                break;
            }
            case "waterway": {
                switch (value) {
                    case "stream": {
                        waytype = WayType.WATER_STREAM;
                        break;
                    }
                    case "river": {
                        waytype = WayType.WATER_RIVER;
                        break;
                    }
                    case "ditch": {
                        waytype = WayType.WATER_DITCH;
                        break;
                    }
                }
                break;
            }
            case "leisure": {
                switch (value) {
                    case "stadium": {
                        waytype = WayType.STADIUM;
                        break;
                    }
                    case "common": {
                        waytype = WayType.MEADOW;
                        break;
                    }
                    case "garden":
                    case "park": {
                        waytype = WayType.PARK;
                        break;
                    }
                    case "playground": {
                        waytype = WayType.PLAYGROUND;
                        break;
                    }
                    case "track":
                    case "pitch":
                    case "miniature_golf":
                    case "golf_course": {
                        waytype = WayType.SPORT;
                        break;
                    }
                }
                break;
            }
            case "amenity": {
                switch (value) {
                    case "parking": {
                        waytype = WayType.PARKING;
                        break;
                    }
                    case "bicycle_parking": {
                        waytype = WayType.PARKING_BICYCLE;
                        break;
                    }
                }
                break;
            }
            case "aeroway": {
                switch (value) {
                    case "runway": {
                        waytype = WayType.RUNWAY;
                        break;
                    }
                    case "taxiway": {
                        waytype = WayType.TAXIWAY;
                        break;
                    }
                    case "apron": {
                        waytype = WayType.APRON;
                        break;
                    }
                }
                break;
            }
        }
    }
}
