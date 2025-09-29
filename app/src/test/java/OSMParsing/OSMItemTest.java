package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMNode;

public class OSMItemTest {
    @Test
    void setIdTest() {
        OSMNode node = new OSMNode(1, 1);
        node.setID(100);
        assertTrue(node.getID() == 100);
    }
}
