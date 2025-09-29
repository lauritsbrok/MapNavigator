package bfst22.vector;

import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import bfst22.vector.Graph.Graph;
import bfst22.vector.OSMParsing.OSMAddress;
import bfst22.vector.OSMParsing.OSMParser;
import bfst22.vector.OSMParsing.OSMPointOfInterest;
import bfst22.vector.OSMParsing.OSMReader;
import bfst22.vector.Util.KdTree;
import bfst22.vector.Util.RadixTree;
import bfst22.vector.Util.WayType;
import bfst22.vector.Util.XYPoint;
import bfst22.vector.gui.Drawable;

/**
 * This class is responsible for creating the model that shows the map, from a
 * file
 */

public class Model {
    public static float minLonBoundary;
    public static float minLatBoundary;
    public static float maxLonBoundary;
    public static float maxLatBoundary;
    public Double parsingProgress;
    public String filename;
    public OSMParser parse;

    /**
     * The constructor creates a new obejct from the class, with parameters filename
     * and controller.
     */
    @SuppressWarnings("all")
    public Model(String filename, LoginController controller)
            throws SAXException, IOException, ClassNotFoundException {
        this.filename = filename;
        if (filename.endsWith(".osm")) {
            this.filename = zipFile(this.filename, controller);
        }
        if (this.filename.endsWith(".zip")) {
            OSMReader reader = new OSMReader();
            parse = new OSMParser(reader);
            parse.reader.parseFile(filename, (ContentHandler) parse, controller);
            this.minLonBoundary = parse.minLonBoundary;
            this.minLatBoundary = parse.minLatBoundary;
            this.maxLonBoundary = parse.maxLonBoundary;
            this.maxLatBoundary = parse.maxLatBoundary;
            this.filename = filename + ".obj";
            initAddresses(controller);
            System.out.println("saving to binary");
            long startTime = System.currentTimeMillis();
            this.save(this.filename, controller);
            System.out.println("saving done : " + (System.currentTimeMillis() - startTime) + " ms");
        } else if (this.filename.endsWith(".obj")) {
            Path path = Paths.get(this.filename);
            try (InputStream inputStream = Files.newInputStream(path)) {
                loadOSM(inputStream, controller);
            }
        }
        createPointOfInterest(filename, controller);
    }

    @SuppressWarnings("all")
    private void loadOSM(InputStream ioStream, LoginController controller) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(ioStream))) {
            long startTime = System.currentTimeMillis();
            this.minLonBoundary = in.readFloat();
            this.minLatBoundary = in.readFloat();
            this.maxLonBoundary = in.readFloat();
            this.maxLatBoundary = in.readFloat();
            controller.setParsingProgress(0.1);
            controller.setActionString("Bound done : " + (System.currentTimeMillis() - startTime) + " ms");
            startTime = System.currentTimeMillis();
            ModelData.segmentsTree = (EnumMap<WayType, KdTree<XYPoint>>) in.readObject();
            controller.setParsingProgress(0.3);
            controller.setActionString("Kdtrees done : " + (System.currentTimeMillis() - startTime) + " ms");
            startTime = System.currentTimeMillis();
            ModelData.coastLine = (ArrayList<Drawable>) in.readObject();
            controller.setParsingProgress(0.5);
            controller.setActionString("CoastLine done : " + (System.currentTimeMillis() - startTime) + " ms");
            startTime = System.currentTimeMillis();
            ModelData.trieAddress = (RadixTree<String>) in.readObject();
            controller.setParsingProgress(0.7);
            controller.setActionString("RadixTree done : " + (System.currentTimeMillis() - startTime) + " ms");
            startTime = System.currentTimeMillis();
            ModelData.graph = (Graph) in.readObject();
            controller.setParsingProgress(1);
            controller.setActionString("graph done : " + (System.currentTimeMillis() - startTime) + " ms");
        }
    }

    @SuppressWarnings("all")
    private void createPointOfInterest(String filename, LoginController controller) {
        Path path = Paths.get(filename + "_PointOfInterest.obj");
        if (Files.exists(path)) {
            try (InputStream inputStream = Files.newInputStream(path);
                    ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(inputStream))) {
                long startTime = System.currentTimeMillis();
                ModelData.pointOfInterest = (ArrayList<OSMPointOfInterest>) in.readObject();
                controller.setActionString(
                        "PointOfInterestloaded done : " + (System.currentTimeMillis() - startTime) + " ms");
                ModelData.putPointOfInterestInRadix();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(String newFilename, LoginController controller)
            throws SAXException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(newFilename)));
        long startTime = System.currentTimeMillis();
        out.writeFloat(minLonBoundary);
        out.flush();
        out.writeFloat(minLatBoundary);
        out.flush();
        out.writeFloat(maxLonBoundary);
        out.flush();
        out.writeFloat(maxLatBoundary);
        out.flush();
        controller.setActionString("bounds saved : " + (System.currentTimeMillis() - startTime) + " ms");
        startTime = System.currentTimeMillis();
        out.writeObject(ModelData.segmentsTree);
        out.flush();
        controller.setActionString("kdtree saved : " + (System.currentTimeMillis() - startTime) + " ms");
        startTime = System.currentTimeMillis();
        out.writeObject(ModelData.coastLine);
        out.flush();
        controller.setActionString("coastline saved : " + (System.currentTimeMillis() - startTime) + " ms");
        startTime = System.currentTimeMillis();
        out.writeObject(ModelData.trieAddress);
        out.flush();
        controller.setActionString("trie saved : " + (System.currentTimeMillis() - startTime) + " ms");
        startTime = System.currentTimeMillis();
        out.writeObject(ModelData.graph);
        out.flush();
        controller.setActionString("graph saved : " + (System.currentTimeMillis() - startTime) + " ms");
        out.close();
    }

    public void savePointOfInterest(String newFilename)
            throws SAXException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(newFilename + "_PointOfInterest.obj")));
        long startTime = System.currentTimeMillis();
        out.writeObject(ModelData.pointOfInterest);
        System.out.println("PointOfInterest saved : " + (System.currentTimeMillis() - startTime) + " ms");
        out.close();

    }

    public String zipFile(String filename, LoginController controller) throws IOException {
        controller.setActionString("converting to zip file started");
        long startTime = System.currentTimeMillis();
        String newFilename = filename.replaceAll(".osm", ".zip");
        FileOutputStream fos = new FileOutputStream(newFilename);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(filename);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
        controller.setActionString("converting ended : " + (System.currentTimeMillis() - startTime) + " ms");
        return newFilename;
    }

    private void initAddresses(LoginController controller) {
        long startTime = System.currentTimeMillis();
        controller.setActionString("Initializing addresses");
        ModelData.trieAddress = new RadixTree<>();
        for (OSMAddress address : ModelData.addresses) {
            String temp = address.toString().toLowerCase();
            String coordinate = address.getCoordinate();
            ModelData.trieAddress.put(temp, coordinate);
        }
        controller.setActionString("Initializing addresses done : " +
                (System.currentTimeMillis() - startTime));

    }

    public float getMinLonBoundary() {
        return minLonBoundary;
    }

    @SuppressWarnings("all")
    public void setMinLonBoundary(float minLonBoundary) {
        this.minLonBoundary = minLonBoundary;
    }

    public float getMinLatBoundary() {
        return minLatBoundary;
    }

    @SuppressWarnings("all")
    public void setMinLatBoundary(float minLatBoundary) {
        this.minLatBoundary = minLatBoundary;
    }

    public float getMaxLonBoundary() {
        return maxLonBoundary;
    }

    @SuppressWarnings("all")
    public void setMaxLonBoundary(float maxLonBoundary) {
        this.maxLonBoundary = maxLonBoundary;
    }

    public float getMaxLatBoundary() {
        return maxLatBoundary;
    }

    @SuppressWarnings("all")
    public void setMaxLatBoundary(float maxLatBoundary) {
        this.maxLatBoundary = maxLatBoundary;
    }
}
