package Util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import bfst22.vector.Util.IndexMinPQ;

public class IndexMinPQTest {

    IndexMinPQ<Integer> index = new IndexMinPQ<>(10);

    @BeforeAll
    public static void setup() {

    }

    @Test
    @SuppressWarnings("unused")
    void testIndexMinPQ() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IndexMinPQ<Integer> indexTemp = new IndexMinPQ<>(-1);
        });
        assertNotNull(index);

    }

    @Test
    void testIsEmpty() {
        assertTrue(index.isEmpty());
        index.insert(5, 5);
        assertFalse(index.isEmpty());
    }

    @Test
    void testSize() {
        index.size();
    }

    @Test
    void testInsert() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(5, 5);
            index.insert(5, 5);
        });
    }

    @Test
    void testMinIndex() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            IndexMinPQ<Integer> indexTemp = new IndexMinPQ<>(0);
            indexTemp.minIndex();
        });
        index.insert(5, 5);
        index.minIndex();

    }

    @Test
    void testMinKey() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            IndexMinPQ<Integer> indexTemp = new IndexMinPQ<>(0);
            indexTemp.minKey();
        });
        index.insert(5, 5);
        index.minKey();

    }

    @Test
    void testDelMin() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            IndexMinPQ<Integer> indexTemp = new IndexMinPQ<>(0);
            indexTemp.delMin();
        });
        index.insert(5, 5);
        index.delMin();

    }

    @Test
    void testKeyOf() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            index.insert(5, 5);
            index.keyOf(3);
        });
        index.keyOf(5);
    }

    @Test
    void testChangeKey() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            index.insert(5, 5);
            index.changeKey(3, 3);
        });
        index.changeKey(5, null);
    }

    @Test
    void testDecreaseKey() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            index.insert(5, 5);
            index.decreaseKey(3, 3);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(0, 0);
            index.decreaseKey(0, 0);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(1, 5);
            index.decreaseKey(1, 6);
        });
        index.decreaseKey(5, 3);
    }

    @Test
    void testIncreaseKey() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            index.insert(5, 5);
            index.increaseKey(3, 3);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(0, 0);
            index.increaseKey(0, 0);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(1, 6);
            index.increaseKey(1, 5);
        });
        index.increaseKey(5, 6);
    }

    @Test
    void testdelete() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            index.insert(5, 5);
            index.delete(4);
        });
        index.delete(5);
    }

    @Test
    void testValidateIndex() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(-5, 1);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            index.insert(11, 11);
        });
    }

    @Test
    void testIterator() {
        index.iterator();
    }

}
