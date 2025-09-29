package bfst22.vector.OSMParsing;

import java.io.IOException;
import java.io.InputStream;

import bfst22.vector.LoginController;

/**
 * This class helps us return data from how long it takes to load the map. It's
 * used to make a progressbar that will load when
 * opening the map.
 */
class SizeInputStream extends InputStream {
    LoginController controller;
    // The InputStream to read bytes from
    private InputStream in = null;

    // The number of bytes that can be read from the InputStream
    private int size = 0;

    // The number of bytes that have been read from the InputStream
    private int bytesRead = 0;

    public SizeInputStream(InputStream in, int size, LoginController controller) {
        this.in = in;
        this.size = size;
        this.controller = controller;
    }

    public int available() {
        return (size - bytesRead);
    }

    public int read() throws IOException {
        int b = in.read();
        if (b != -1) {
            bytesRead++;
        }
        setProgress();
        return b;
    }

    public int read(byte[] b) throws IOException {
        int read = in.read(b);
        bytesRead += read;
        return read;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int read = in.read(b, off, len);
        bytesRead += read;
        setProgress();
        return read;
    }

    public double getPercentage() {
        return bytesRead * 1.0 / size;
    }

    public void setProgress() {
        if (controller.getParsingProgress() < getPercentage()) {
            controller.setParsingProgress(getPercentage());
        }
    }
}
