package fr.unicaen.info.l3.tron.model;

import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.model.player.PlayerBot;
import fr.unicaen.info.l3.tron.ressources.config.runconfig.Configurator;
import fr.unicaen.info.l3.tron.utils.Movement;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by florian on 09/11/16.
 */
public class GameTest {

    private Game game;

    private AbstractPlayer obstacle;


    @Before
    public void setUp() throws Exception {
        String propertyName = Configurator.DEFAULT_JVM_NAMESPACE + ".";
        System.setProperty(propertyName + "coal", "1 1");
        System.setProperty(propertyName + "nbCoal", "1 1");
        System.setProperty(propertyName + "gridSize", "4");
        Configurator.getConfiguration();

        game = new Game();

        obstacle = new PlayerBot(1, 0, 0);
    }

    @Test
    public void moveNormal() throws Exception {
        game.move(0, Movement.UP);
        assertEquals(true, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.RIGHT);
        assertEquals(true, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.DOWN);
        assertEquals(true, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.LEFT);
        assertEquals(false, game.getCurrentPlayer().aliveProperty().getValue());
    }

    @Test
    public void moveObstacle() throws Exception {
        game.getGrid().changeState(1, 0, obstacle);

        game.move(0, Movement.UP);
        assertEquals(true, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.RIGHT);
        assertEquals(true, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.DOWN);
        assertEquals(false, game.getCurrentPlayer().aliveProperty().getValue());

        game.move(0, Movement.LEFT);
        assertEquals(false, game.getCurrentPlayer().aliveProperty().getValue());
    }
}
