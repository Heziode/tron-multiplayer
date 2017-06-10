package fr.unicaen.info.l3.tron.utils;

import fr.unicaen.info.l3.tron.model.Square;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by florian on 09/11/16.
 */
public class UtilsTest {

    private int gridSize, totalPlayers;

    @Before
    public void setUp() throws Exception {
        gridSize = 4;
        totalPlayers = 2;
    }

    @Test
    public void getOptimalDistance() throws Exception {

        int value = Utils.getOptimalDistance(gridSize, totalPlayers);
        assertNotNull(value);
        assertEquals(false, value < 2);
    }

    @Test
    public void isEqualsNormal() throws Exception {
        Square square1 = new Square(1, 1);
        Square square2 = new Square(1, 1);
        square1.nextState(null);

        assertEquals(true, Utils.isEquals(square1, square2));
    }

    @Test
    public void isEqualsDifferent() throws Exception {
        Square square1 = new Square(1, 1);
        Square square2 = new Square(1, 2);
        assertEquals(false, Utils.isEquals(square1, square2));
    }

    @Test
    public void removeAll() throws Exception {
        LinkedList list = new LinkedList();

        for (int i = 0; i < 50; i++) {
            list.add(i);
        }

        assertEquals(50, list.size());
        Utils.removeAll(list);
        assertEquals(0, list.size());
    }

}
