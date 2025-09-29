package bfst22.vector.OSMParsing;

public abstract class OSMItem {
    long id;

    public OSMItem() {
    }

    public OSMItem(long id) {
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }
}