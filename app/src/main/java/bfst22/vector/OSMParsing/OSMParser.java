package bfst22.vector.OSMParsing;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import bfst22.vector.ModelData;
import bfst22.vector.Graph.Edge;
import bfst22.vector.Graph.Graph;
import bfst22.vector.Util.Point;
import bfst22.vector.Util.WayType;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.Drawable;
import bfst22.vector.gui.Label;
import bfst22.vector.gui.MultiPolygon;
import bfst22.vector.gui.PolyLine;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * This class is responsible for parsing OSM XML file
 */
public class OSMParser extends SAXAdapter {
    public OSMReader reader;
    public float minLatBoundary;
    public float minLonBoundary;
    public float maxLatBoundary;
    public float maxLonBoundary;
    public EnumMap<WayType, ArrayList<XYPoint>> segments;
    public ArrayList<Drawable> coastLine;
    public HashMap<Point, OSMWay> coastLineMap;
    private NodeMap temporaryNodeReferences;
    private WayMap temporaryWayReferences;
    private RelationMap temporaryRelationReferences;
    private OSMNode tempNode;
    private OSMNode newNode;
    private OSMWay tempWay;
    private OSMRelation tempRelation;
    private OSMAddress tempAddress;
    private Label tempPlace;
    private ArrayList<OSMNode> currentNodes;
    private ArrayList<OSMWay> currentWays;
    public ArrayList<OSMAddress> addresser;
    public ArrayList<String> addressesStrings;
    private WayType waytype;
    private Graph graph;
    // RouteEdge data
    private String name;
    private boolean toGraph;
    private boolean oneway;
    private boolean roundabout;
    private boolean carAllowed, bikeAllowed, walkAllowed;
    private short speed;

    public OSMParser(OSMReader reader) {
        this.reader = reader;

    }

    public void startDocument() {
        temporaryNodeReferences = new NodeMap();
        temporaryWayReferences = new WayMap();
        temporaryRelationReferences = new RelationMap();
        segments = new EnumMap<WayType, ArrayList<XYPoint>>(WayType.class);
        coastLineMap = new HashMap<>();
        currentNodes = new ArrayList<>();
        currentWays = new ArrayList<>();
        addresser = new ArrayList<>();
        coastLine = new ArrayList<>();
        addressesStrings = new ArrayList<>();
        toGraph = false;
        oneway = false;
        carAllowed = true;
        bikeAllowed = true;
        walkAllowed = true;
        roundabout = false;
        graph = new Graph();
        for (WayType type : WayType.values()) {
            segments.put(type, new ArrayList<>());
        }
    }

    @Override
    public void endDocument() throws SAXException {
        temporaryClean();
        System.gc();
        for (Point key : coastLineMap.keySet()) {
            OSMWay temp = coastLineMap.get(key);
            if (!coastLine.contains(new PolyLine(temp.nodes))) {
                coastLine.add(new PolyLine(temp.nodes));
            }
        }
        ModelData.setCoastLine(coastLine);
        ModelData.setNodes(segments);
        ModelData.setAddresses(addresser);
        ModelData.setGraph(graph);
        resetAllValues();
        finalCleanUp();
    }

    @SuppressWarnings("all")
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        switch (qName) {
            case "bounds": {
                minLatBoundary = -Float.parseFloat(atts.getValue("minlat"));
                minLonBoundary = 0.56f * Float.parseFloat(atts.getValue("minlon"));
                maxLatBoundary = -Float.parseFloat(atts.getValue("maxlat"));
                maxLonBoundary = 0.56f * Float.parseFloat(atts.getValue("maxlon"));
                break;
            }
            case "node": {
                long id = Long.parseLong(atts.getValue("id"));
                float lon = Float.parseFloat(atts.getValue("lon"));
                float lat = Float.parseFloat(atts.getValue("lat"));
                newNode = new OSMNode(id, 0.56f * lon, -lat);
                tempNode = newNode;
                temporaryNodeReferences.add(tempNode);
                break;
            }
            case "nd": {
                currentNodes.add(temporaryNodeReferences.get(Long.parseLong(atts.getValue("ref"))));
                break;
            }
            case "way": {
                long id = Long.parseLong(atts.getValue("id"));
                tempWay = new OSMWay(id);
                break;
            }
            case "tag": {
                String k = atts.getValue("k").trim();
                String v = atts.getValue("v").trim();
                parseTagInformation(k, v);
                break;
            }
            case "relation": {
                tempRelation = new OSMRelation(Long.parseLong(atts.getValue("id")));
                temporaryRelationReferences.add(tempRelation);
                break;
            }
            case "member": {
                long ref = Long.parseLong(atts.getValue("ref"));
                String type = atts.getValue("type");
                String role = atts.getValue("role");
                switch (type) {
                    case "node":
                        if (temporaryWayReferences.get(ref) != null) {
                            tempRelation.addMember(temporaryNodeReferences.get(ref), role);
                        }
                        break;
                    case "way": {
                        if (temporaryWayReferences.get(ref) != null) {
                            tempRelation.addMember(temporaryWayReferences.get(ref), role);
                        }
                        break;
                    }
                    case "relation": {
                        if (temporaryRelationReferences.get(ref) != null) {
                            tempRelation.addMember(temporaryRelationReferences.get(ref), role);
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "relation":
            case "way":
            case "node": {
                addToGraph();
                addCurrent();
                resetValues();
                break;
            }
        }
    }

    private void addCurrent() {
        if (tempAddress != null && (tempNode != null)) {
            tempAddress.setCoords(tempNode);
            addresser.add(tempAddress);
            // addressesStrings.add(tempAddress.toString());
        }
        if (tempPlace != null) {
            if (tempNode != null) {
                tempPlace.setLocation(tempNode);
            }
            if (name != null) {
                if (name.toLowerCase().equals("bornholm")) {
                    System.out.println(name);
                    System.out.println(waytype.toString());
                }
                tempPlace.setName(name);
            }
            segments.get(waytype).add(tempPlace);
        }
        if (tempWay != null) {
            tempWay.addNodes(currentNodes);
            tempWay.setType(waytype);
            if (name != null) {
                tempWay.setName(name);
                if (waytype.toString().startsWith("HIGHWAY_")) {
                    currentWays.add(tempWay);
                }
            }
            temporaryWayReferences.add(tempWay);
            if (waytype == WayType.COASTLINE) {
                CoastLineTool.connectCoastLine(coastLineMap, tempWay);
            } else {
                if (waytype != WayType.UNKNOWN && waytype != WayType.UNDEFINED) {
                    if (segments.get(tempWay.type) != null) {
                        segments.get(tempWay.type).add(new PolyLine(currentNodes));
                    }
                }
            }
        }
        if (tempRelation != null) {
            tempRelation.setWaytype(waytype);
            if (waytype != WayType.UNKNOWN && waytype != WayType.UNDEFINED) {
                if (segments.get(tempRelation.getWaytype()) != null) {
                    segments.get(tempRelation.getWaytype()).add(new MultiPolygon(tempRelation));
                }
            }
        }
    }

    private void addToGraph() {
        if (!toGraph) {
            return;
        }
        setDefaultHighwayValues();
        for (int i = 0; i < currentNodes.size() - 1; i++) {
            graph.addEdge(
                    new Edge(currentNodes.get(i), currentNodes.get(i + 1), name, speed, carAllowed, bikeAllowed,
                            walkAllowed, roundabout),
                    oneway);
        }
    }

    @SuppressWarnings("all")
    private void setDefaultHighwayValues() {
        if (waytype.toString().startsWith("HIGHWAY_")) {
            switch (waytype) {
                case HIGHWAY_PATH:
                    break;
                case HIGHWAY_TRACK:
                    break;
                case HIGHWAY_STEPS:
                    break;
                case HIGHWAY_MOTORWAY:
                    if (speed == 0) {
                        speed = 130;
                    }
                    break;
                case HIGHWAY_TRUNK:
                case HIGHWAY_PRIMARY:
                    if (speed == 0) {
                        speed = 90;
                    }
                    break;
                case HIGHWAY_SECONDARY:
                    if (speed == 0) {
                        speed = 80;
                    }
                    break;
                case HIGHWAY_UNDEFINED:
                case HIGHWAY_SERVICE:
                case HIGHWAY_TERTIARY:
                    if (speed == 0) {
                        speed = 50;
                    }
                    break;
                case HIGHWAY_RESIDENTIAL:
                    if (speed == 0) {
                        speed = 40;
                    }
                    break;
                case HIGHWAY_CYCLEWAY:
                    break;
                case HIGHWAY_FOOTWAY:
                    break;
                case HIGHWAY_UNCLASSIFIED:
                    break;
                case HIGHWAY_PEDESTRIAN:
                    break;

            }
        }
    }

    private void resetValues() {
        currentNodes.clear();
        tempAddress = null;
        tempWay = null;
        tempRelation = null;
        name = null;
        oneway = false;
        toGraph = false;
        carAllowed = true;
        bikeAllowed = true;
        walkAllowed = true;
        roundabout = false;
        tempPlace = null;
        speed = 0;
        waytype = WayType.UNKNOWN;
    }

    private void temporaryClean() {
        temporaryNodeReferences = null;
        temporaryWayReferences = null;
        temporaryRelationReferences = null;
    }

    private void resetAllValues() {
        reader = null;
        coastLineMap = null;
        temporaryNodeReferences = null;
        temporaryWayReferences = null;
        temporaryRelationReferences = null;
        name = null;
        tempNode = null;
        tempWay = null;
        tempRelation = null;
        tempAddress = null;
        tempPlace = null;
        currentNodes = null;
        waytype = null;
        oneway = false;
        toGraph = false;
        carAllowed = true;
        bikeAllowed = false;
        walkAllowed = false;
        speed = 0;
    }

    private void finalCleanUp() {
        System.gc();
    }

    public void parseTagInformation(String key, String value) {
        if (tempNode != null) {
            switch (key) {
                case "addr:city": {
                    if (tempAddress == null) {
                        tempAddress = new OSMAddress();
                    }
                    tempAddress.setCity(value.trim());
                    return;
                }
                case "addr:postcode": {
                    if (tempAddress == null) {
                        tempAddress = new OSMAddress();
                    }
                    tempAddress.setPostcode(value.trim());
                    return;
                }
                case "addr:housenumber": {
                    if (tempAddress == null) {
                        tempAddress = new OSMAddress();
                    }
                    tempAddress.setHousenumber(value.trim());
                    return;
                }
                case "addr:street": {
                    if (tempAddress == null) {
                        tempAddress = new OSMAddress();
                    }
                    tempAddress.setStreet(value.trim());
                    return;
                }
                case "name": {
                    name = value;
                    break;
                }
                case "place": {
                    switch (value) {
                        case "city": {
                            if (tempPlace == null) {
                                tempPlace = new Label();
                            }
                            waytype = WayType.PLACE_CITY;
                            return;
                        }
                        case "town": {
                            if (tempPlace == null) {
                                tempPlace = new Label();
                            }
                            waytype = WayType.PLACE_TOWN;
                            return;
                        }
                        case "suburb": {
                            if (tempPlace == null) {
                                tempPlace = new Label();
                            }
                            waytype = WayType.PLACE_SUBURB;
                            return;
                        }
                        case "neighbourhood": {
                            if (tempPlace == null) {
                                tempPlace = new Label();
                            }
                            waytype = WayType.PLACE_NEIGHBOURHOOD;
                            return;
                        }
                        case "village": {
                            if (tempPlace == null) {
                                tempPlace = new Label();
                            }
                            waytype = WayType.PLACE_VILLAGE;
                            return;
                        }
                    }
                    break;
                }
            }
        }
        switch (key) {
            case "name":
                name = value;
                if (tempWay != null) {
                    tempWay.setName(value);
                    break;
                }
                break;
            case "maxspeed":
                try {
                    speed = Short.parseShort(value);
                } catch (NumberFormatException e) {

                }
                break;
            case "bicycle":
                if (value.equals("yes")) {
                    bikeAllowed = true;
                } else {
                    bikeAllowed = false;
                }
                break;
            case "sidewalk":
                walkAllowed = true;
                break;
            case "oneway":
                if (value.equals("yes")) {
                    oneway = true;
                }
                break;
            case "junction":
                if (value.equals("roundabout")) {
                    oneway = true;
                    roundabout = true;
                }
                break;
            case "highway": {
                toGraph = true;
                switch (value) {
                    case "motorway_link":
                    case "motorway": {
                        waytype = WayType.HIGHWAY_MOTORWAY;
                        walkAllowed = false;
                        bikeAllowed = false;
                        break;
                    }
                    case "trunk_link":
                    case "trunk": {
                        waytype = WayType.HIGHWAY_TRUNK;
                        break;
                    }
                    case "primary_link":
                    case "primary": {
                        walkAllowed = false;
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
                        carAllowed = false;
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
                        carAllowed = false;
                        waytype = WayType.HIGHWAY_CYCLEWAY;
                        break;
                    }
                    case "traffic_signal": {
                        break;
                    }
                    case "path":
                        carAllowed = false;
                        waytype = WayType.HIGHWAY_PATH;
                        break;
                    case "footway":
                    case "bridleway":
                    case "track":
                    case "steps": {
                        carAllowed = false;
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
                    default: {
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
                waytype = WayType.WATER;
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
                    case "breakwater":
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