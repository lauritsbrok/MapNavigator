package Util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.OSMParsing.OSMNode;
import bfst22.vector.Util.RadixTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class RadixTreeTest {

    private static RadixTree rTree;
    private static List<Map.Entry<String, String>> result;

    @BeforeAll()
    public static void setup() {
        rTree = new RadixTree<>();
    }

    @AfterAll
    public static void cleanUp() {
        rTree.clear();
    }

    @Test
    void assertNotEmptyBecauseOfRoot() {
        assertEquals(false, rTree.isEmpty());
    }

    @Test
    void assertClear() {
        rTree.put("CilitBang", new OSMNode(1.0f, 2.0f));
        rTree.clear();
        assertEquals(true, rTree.isEmpty());
    }

    // Tests for RadixTree size - 1 (root).
    @Test
    void assertSizeWorks() {
        rTree.clear();
        assertNotNull(rTree);
        assertTrue(rTree.isEmpty());

        rTree.put("He", new OSMNode(1.0f, 2.0f));
        assertEquals(2, rTree.size());

        rTree.put("Hej", new OSMNode(2.0f, 3.0f));
        assertEquals(3, rTree.size());

        rTree.put("Hejsa", new OSMNode(4.0f, 5.0f));
        assertEquals(4, rTree.size());

        rTree.put("me", new OSMNode(4.0f, 5.0f));
        assertEquals(5, rTree.size());

        rTree.put("med", new OSMNode(4.0f, 5.0f));
        assertEquals(6, rTree.size());

        rTree.put("Hello", new OSMNode(2.0f, 3.0f));
        assertEquals(7, rTree.size());

    }

    // ********************* Put operation *********************
    @Test
    void assertPutsAndGetsKeysCorrect() {
        rTree.put("Hel", new OSMNode(1.0f, 2.0f));
        rTree.put("Hello", new OSMNode(1.0f, 2.0f));
        assertEquals("Hello", rTree.getKeysWithPrefix("Hello").get(0));

    }

    @Test
    void putTwoEqualKeys() {
        rTree.put("Hello", new OSMNode(1.0f, 2.0f));
        rTree.put("Hello", new OSMNode(1.0f, 2.0f));
        assertEquals("Hello", rTree.getKeysWithPrefix("Hello").get(0));
    }

    @Test
    void assertPutsAndSplitsPrefixCorrect() {
        rTree.put("He", new OSMNode(1.0f, 2.0f));
        rTree.put("Hello", new OSMNode(1.0f, 2.0f));
        rTree.put("Hellfire", new OSMNode(1.0f, .0f));
        rTree.put("H", new OSMNode(1.0f, 6.0f));

        String[] expected = { "Hello", "Hellfire" };
        assertEquals(expected[1], rTree.getKeysWithPrefix("Hel").get(1));
    }

    @Test
    void assertPutNullKey() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.put(null, new OSMNode(1.0f, 2.0f));
        });
    }

    // ********************* Remove *********************
    @Test
    void assertRemoveNullKeyThrowsException() {
        rTree.put("", new OSMNode(1.0f, 2.0f));

        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.remove(null);
        });
    }

    @Test
    void assertRemoveThrowsClassException() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            rTree.remove(1);
        });
    }

    @Test
    void assertRemoveEmptyStringReturnNullValue() {
        rTree.put("", null);
        assertEquals(null, rTree.remove(""));
    }

    @Test
    void assertRemovesSuccessfull() {
        rTree.put("He", new OSMNode(1.0f, 2.0f));
        rTree.put("Hello", new OSMNode(1.0f, 2.0f));
        rTree.put("Hellof", new OSMNode(2.0f, 4.0f));
        rTree.put("H", new OSMNode(1.0f, 3.0f));

        rTree.remove("He");

        assertEquals(null, rTree.remove("He"));
        assertEquals(null, rTree.remove("f"));
    }

    // ********************* Contains Key *********************

    @Test
    void assertContainsNullThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.containsKey(null);
        });
    }

    @Test
    void assertContiansThrowsClassCastExeption() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            rTree.containsKey(1);
        });
    }

    @Test
    void assertContainsEmptyString() {
        assertTrue(rTree.containsKey(""));
    }

    @Test
    void assertContainsTrue() {
        rTree.put("key", new OSMNode(1.0f, 2.0f));
        assertTrue(rTree.containsKey("key"));
    }

    // ********************* Contains Value *********************

    @Test
    void assertContainsValueFalse() {
        assertEquals(false, rTree.containsValue("key"));

    }

    @Test
    void assertContainsValueTrue() {
        rTree.putIfAbsent("Key", 69);
        assertEquals(true, rTree.containsValue(69));
    }

    // ********************* Get method ********************

    @Test
    void assertGetThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.get(null);
        });
    }

    @Test
    void assertGetThrowsClassCastException() {
        Assertions.assertThrows(ClassCastException.class, () -> {
            rTree.get(1);
        });
    }

    @Test
    void assertGetResult() {
        rTree.putIfAbsent("Key", 69);
        assertEquals(69, rTree.get("Key"));
    }

    @Test
    void assertGetKeyesWithPrefixThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.getKeysWithPrefix(null);
        });
    }

    @Test
    void assertGetEntriesList() {
        result = new ArrayList<Map.Entry<String, String>>();
        result.add(Map.entry("Frodo", "*Stares in EMO*"));
        result.add(Map.entry("Gandalf", "YouShallNotPass!"));
        result.add(Map.entry("Aragon", "isildur's heir'"));

        for (Map.Entry entry : result) {
            rTree.putIfAbsent(entry.getKey(), entry.getValue());
        }

        assertTrue(rTree.getEntriesWithPrefix("Fr").size() == 1);
    }

    // ********************* Map methods ********************

    @Test
    void assertGetValuesWithPrefixThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            rTree.getValuesWithPrefix(null);
        });
    }

    @Test
    void assertGetValuesWithPrefix_List() {
        result = new ArrayList<Map.Entry<String, String>>();
        result.add(Map.entry("Frodo", "*Stares in EMO*"));
        result.add(Map.entry("Gandalf", "YouShallNotPass!"));
        result.add(Map.entry("Aragon", "isildur's heir'"));

        for (Map.Entry entry : result) {
            rTree.putIfAbsent(entry.getKey(), entry.getValue());
        }

        assertEquals("*Stares in EMO*", rTree.getValuesWithPrefix("Fr").get(0));
    }

}