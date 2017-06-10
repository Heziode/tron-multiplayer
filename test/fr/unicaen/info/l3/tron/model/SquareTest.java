package fr.unicaen.info.l3.tron.model;

import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.model.player.PlayerBot;
import fr.unicaen.info.l3.tron.ressources.config.runconfig.Configurator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by florian on 09/11/16.
 */
public class SquareTest {

    private Game game;

    private AbstractPlayer player, obstacle;

    private boolean includeOccupeidSquare;


    @Before
    public void setUp() throws Exception {
        String propertyName = Configurator.DEFAULT_JVM_NAMESPACE + ".";
        System.setProperty(propertyName + "coal", "1");
        System.setProperty(propertyName + "nbCoal", "1");
        System.setProperty(propertyName + "gridSize", "4");
        Configurator.getConfiguration();

        game = new Game();

        player = new PlayerBot(1, 1, 1);

        obstacle = new PlayerBot(3, 3, 0);

        includeOccupeidSquare = false;
    }

    @Test
    public void getNeighborNormal() throws Exception {
        List<Square> neighbours =
            game.getGrid().getGrid()[player.getCoordinates().getColumn()][player.getCoordinates().getLine()]
                .getValue().getNeighbor(game.getGrid(), includeOccupeidSquare);

        assertNotNull(neighbours);
        assertEquals(4, neighbours.size());
    }

    @Test
    public void getNeighborObstacle() throws Exception {
        game.getGrid().changeState(2, 1, obstacle);

        List<Square> neighbours =
            game.getGrid().getGrid()[player.getCoordinates().getColumn()][player.getCoordinates().getLine()]
                .getValue().getNeighbor(game.getGrid(), includeOccupeidSquare);

        assertNotNull(neighbours);
        assertEquals(3, neighbours.size());
    }

    @Test
    public void getNeighborObstacleBis() throws Exception {
        game.getGrid().changeState(2, 1, obstacle);
        includeOccupeidSquare = true;

        List<Square> neighbours =
            game.getGrid().getGrid()[player.getCoordinates().getColumn()][player.getCoordinates().getLine()]
                .getValue().getNeighbor(game.getGrid(), includeOccupeidSquare);

        assertNotNull(neighbours);
        assertEquals(4, neighbours.size());
    }
}
