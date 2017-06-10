package fr.unicaen.info.l3.tron.model.player;

import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.ressources.config.runconfig.Configurator;
import fr.unicaen.info.l3.tron.utils.Movement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by florian on 09/11/16.
 */
public class PlayerBotTest {

    private Game game;

    private AbstractPlayer player, obstacle;

    @Before
    public void setUp() throws Exception {
        String propertyName = Configurator.DEFAULT_JVM_NAMESPACE + ".";
        System.setProperty(propertyName + "coal", "1");
        System.setProperty(propertyName + "nbCoal", "1");
        System.setProperty(propertyName + "gridSize", "4");
        Configurator.getConfiguration();

        game = new Game();

        obstacle = new PlayerBot(3, 3, 0);

        player = new PlayerBot(1, 1, 1);
    }

    @Test
    public void canMoveToClean() throws Exception {

        boolean canMoveTo1 = player.canMoveTo(Movement.UP, game.getGrid());
        boolean canMoveTo2 = player.canMoveTo(Movement.RIGHT, game.getGrid());
        boolean canMoveTo3 = player.canMoveTo(Movement.DOWN, game.getGrid());
        boolean canMoveTo4 = player.canMoveTo(Movement.LEFT, game.getGrid());

        assertNotNull(canMoveTo1);
        assertNotNull(canMoveTo2);
        assertNotNull(canMoveTo3);
        assertNotNull(canMoveTo4);

        assertEquals(true, canMoveTo1);
        assertEquals(true, canMoveTo2);
        assertEquals(true, canMoveTo3);
        assertEquals(true, canMoveTo4);
    }

    @Test
    public void canMoveToWithObstacle() throws Exception {

        game.getGrid().changeState(2, 1, obstacle);
        game.getGrid().changeState(1, 2, obstacle);

        boolean canMoveTo1 = player.canMoveTo(Movement.UP, game.getGrid());
        boolean canMoveTo2 = player.canMoveTo(Movement.RIGHT, game.getGrid());
        boolean canMoveTo3 = player.canMoveTo(Movement.DOWN, game.getGrid());
        boolean canMoveTo4 = player.canMoveTo(Movement.LEFT, game.getGrid());

        assertNotNull(canMoveTo1);
        assertNotNull(canMoveTo2);
        assertNotNull(canMoveTo3);
        assertNotNull(canMoveTo4);

        assertEquals(false, canMoveTo1);
        assertEquals(false, canMoveTo2);
        assertEquals(true, canMoveTo3);
        assertEquals(true, canMoveTo4);
    }

    @Test
    public void moveNormal() throws Exception {

        player.move(Movement.UP, game.getGrid());

        assertEquals(true, player.aliveProperty().getValue());

        player.move(Movement.RIGHT, game.getGrid());

        assertEquals(true, player.aliveProperty().getValue());

        player.move(Movement.DOWN, game.getGrid());

        assertEquals(true, player.aliveProperty().getValue());

        player.move(Movement.LEFT, game.getGrid());

        assertEquals(false, player.aliveProperty().getValue());

    }

    @Test
    public void moveObstacle() throws Exception {

        game.getGrid().changeState(2, 1, obstacle);

        player.move(Movement.UP, game.getGrid());

        assertEquals(true, player.aliveProperty().getValue());

        player.move(Movement.RIGHT, game.getGrid());

        assertEquals(true, player.aliveProperty().getValue());

        player.move(Movement.DOWN, game.getGrid());

        assertEquals(false, player.aliveProperty().getValue());

        player.move(Movement.LEFT, game.getGrid());

        assertEquals(false, player.aliveProperty().getValue());

    }
}
