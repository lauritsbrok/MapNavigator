package bfst22.vector.OSMParsing;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import bfst22.vector.LoginController;
import bfst22.vector.Model;
import bfst22.vector.View;
import bfst22.vector.gui.AlertPopUp;

import java.util.zip.ZipInputStream;
import java.io.BufferedInputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;

@SuppressWarnings("all")
public class OSMReader {
    private String fileName;
    private static float progress;

    public OSMReader() {
    }

    public OSMReader(String fileName, ContentHandler contentHandler) throws SAXException, IOException {
        this.parseFXML(fileName, contentHandler);
    }

    public OSMReader(String fileName, ContentHandler contentHandler, LoginController controller)
            throws SAXException, IOException {
            this.parseFile(fileName, contentHandler, controller);
    }

    public String getFileName() {
        return fileName;
    }

    public void parseFile(String fileName, ContentHandler contentHandler, LoginController controller)
            throws SAXException, IOException {
        this.fileName = fileName;
        if (fileName.endsWith(".osm")) {
            FileInputStream inputStream = new FileInputStream(fileName);
            SizeInputStream stream = new SizeInputStream(inputStream, inputStream.available(), controller);
            loadOSM(new InputSource(stream), contentHandler);
        } else if (fileName.endsWith(".zip")) {
            FileInputStream inputStream = new FileInputStream(fileName);
            SizeInputStream stream = new SizeInputStream(inputStream, inputStream.available(), controller);
            ZipInputStream zip = new ZipInputStream(new BufferedInputStream(stream));
            zip.getNextEntry();
            loadOSM(new InputSource(zip), contentHandler);
        } else if (fileName.endsWith(".fxml")) {
            FileInputStream inputStream = new FileInputStream(fileName);
            loadTheme(new InputSource(inputStream), contentHandler);
        }
    }

    public void parseFXML(String fileName, ContentHandler contentHandler)
            throws SAXException, IOException {
        this.fileName = fileName;
        try (InputStream ioStream = Model.class.getResourceAsStream(fileName)) {
            loadTheme(new InputSource(ioStream), contentHandler);
        }
    }

    private void loadOSM(InputSource source, ContentHandler contentHandler)
            throws SAXException, IOException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(contentHandler);
        reader.parse(source);
    }

    private void loadTheme(InputSource source, ContentHandler contentHandler) throws IOException, SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(contentHandler);
        reader.parse(source);
    }
}
