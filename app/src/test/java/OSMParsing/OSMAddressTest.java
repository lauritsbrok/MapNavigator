package OSMParsing;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMAddress;
import bfst22.vector.OSMParsing.OSMNode;

public class OSMAddressTest {
    @Test
    void constructorTest() {
        OSMAddress addr = new OSMAddress();
        assertNotNull(addr);
        addr = new OSMAddress(1f, 2f);
        assertTrue(addr.getCoordinate().equals("1.0, 2.0"));
        addr = new OSMAddress(1f, 2f, "by", "gade", "99", "9999");
        assertTrue(addr.toString().equals("gade 99, 9999 by"));
    }

    @Test
    void getterSetterTest() {
        OSMAddress addr = new OSMAddress();
        addr.setCoords(new OSMNode(99, 99));
        assertTrue(addr.getCoordinate().equals("99.0, 99.0"));
        addr.setCity("test");
        assertTrue(addr.getCity().equals("test"));
        addr.setStreet("test");
        assertTrue(addr.getStreet().equals("test"));
        addr.setHousenumber("test");
        assertTrue(addr.getHousenumber().equals("test"));
        addr.setPostcode("test");
        assertTrue(addr.getPostcode().equals("test"));
    }
}
