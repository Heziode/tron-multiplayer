package fr.unicaen.info.l3.tron.model.ai;

import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.Square;
import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.model.player.PlayerBot;
import fr.unicaen.info.l3.tron.ressources.config.runconfig.Configurator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Classe de test de la classe AiUtils
 */
public class AIUtilsTest {

    private Game game;

    private AbstractPlayer obstacle;

    private boolean includeOccupedSquare;

    @Before
    public void setUp() throws Exception {
        String propertyName = Configurator.DEFAULT_JVM_NAMESPACE + ".";
        System.setProperty(propertyName + "coal", "1");
        System.setProperty(propertyName + "nbCoal", "1");
        System.setProperty(propertyName + "gridSize", "4");
        Configurator.getConfiguration();

        game = new Game();

        obstacle = new PlayerBot(0, 3, 0);

        includeOccupedSquare = false;

        // on met des obstacles afin d'avoir le chemin souhaité
        for (int i = 0; i < game.getGrid().getSize(); i++) {
            for (int j = 0; j < game.getGrid().getSize(); j++) {

                if (i == 0 || i == 1 || i == 2) {

                    if (j == 1 || j == 2 || j == 3) {
                        game.getGrid().getGrid()[i][j].getValue()
                            .disableSquare(); //.changeState(singlePlayerDepth, j, obstacle);
                    }
                }
            }
        }
    }

    @Test
    public void uniformCostSearchNormalWay() throws Exception {

        List<Square> path = AIUtils.uniformCostSearch(game.getGrid().getGrid()[0][0].getValue(),
            game.getGrid().getGrid()[3][3].getValue(), game.getGrid(), includeOccupedSquare);

        assertNotNull(path);
        assertEquals(7, path.size());
    }

    @Test
    public void uniformCostSearchWithObstacle() throws Exception {
        game.getGrid().changeState(2, 1, null);
        game.getGrid().changeState(3, 0, obstacle);

        List<Square> path = AIUtils.uniformCostSearch(game.getGrid().getGrid()[0][0].getValue(),
            game.getGrid().getGrid()[3][3].getValue(), game.getGrid(), includeOccupedSquare);

        assertNotNull(path);
        assertEquals(7, path.size());
    }

    @Test
    public void territory() throws Exception {

    }

    @Test
    public void territory1() throws Exception {

    }

    @Test
    public void getElementFromPriorityQueue() throws Exception {

    }

}
