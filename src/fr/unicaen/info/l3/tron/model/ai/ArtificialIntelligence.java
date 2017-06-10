package fr.unicaen.info.l3.tron.model.ai;

import fr.unicaen.info.l3.tron.controller.Main;
import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.GameTree;
import fr.unicaen.info.l3.tron.utils.DotGame;

import java.util.List;

/**
 * AI class that implements various search algorithms
 */
public final class ArtificialIntelligence {

    /**
     * Reference of the game
     */
    private Game game;

    /**
     * Tree containing all states calculated by paranoid with a Boolean state defining if a state of play is max
     */
    private GameTree paranoidTree;

    /**
     * Index of the current element
     */
    private int gameTreeIndex;

    /**
     * Logical constructor
     *
     * @param game Reference to the current game.
     */
    public ArtificialIntelligence(Game game) {
        this.game = game;
        GameTree.resetNodeCounter();
    }

    /**
     * Calculate the best action to play for a player in relation to a game state
     *
     * @param game  Reference of a game state
     * @param d     Current Calculation Depth (must be greater or equal than 0)
     * @param alpha Alpha value of pruning
     * @param beta  Beta value of pruning
     * @return Returns a state of play with as last move, the best action to play
     */
    public Game paranoid(Game game, int d, int alpha, int beta) {
        GameTree gameTree;
        if (paranoidTree == null) {
            GameTree.State state =
                game.getCurrentPlayer().getCoalition() == this.game.getCurrentPlayer().getCoalition() ?
                    GameTree.State.MAX : GameTree.State.MIN;
            paranoidTree = new GameTree(game, state);
            gameTree = paranoidTree;
        }
        else {
            gameTree = paranoidTree.addChild(game, gameTreeIndex);
        }
        gameTreeIndex++;

        if ((d == 0) || (game.isEndOfGame())) {
            // Calculate the number of calls
            game.updatePlayersTerritories();
            gameTree.updateNodeValue(this.game.getCurrentPlayer().getCoalition());
            return game;
        }

        List<Game> nextStep = game.getNextStep();
        if (nextStep.size() > 0) {
            Integer m;
            Integer tmpI;
            Game gameTmp = null;

            if (game.getCurrentPlayer().getCoalition() == this.game.getCurrentPlayer().getCoalition()) {
                m = Integer.MIN_VALUE;

                for (Game g : nextStep) {
                    tmpI = m;
                    Game gameParanoid = paranoid(g, d - 1, m, beta);
                    gameTreeIndex--;
                    if (gameParanoid != null) {
                        m = Math.max(m, gameTree.getNodeValue(this.game.getCurrentPlayer().getCoalition()));

                        // New node
                        if (tmpI.intValue() != m.intValue()) {
                            if (m > beta) {
                                if (gameTmp != null) {
                                    gameTmp.updatePlayersTerritories();
                                }
                                return gameTmp;
                            }
                            gameTmp = g;
                        }
                    }
                }

            }
            else {
                m = Integer.MAX_VALUE;

                for (Game g : nextStep) {
                    tmpI = m;
                    Game gameParanoid = paranoid(g, d - 1, alpha, m);
                    gameTreeIndex--;
                    if (gameParanoid != null) {
                        m = Math.min(m, gameTree.getNodeValue(this.game.getCurrentPlayer().getCoalition()));

                        // New node
                        if (tmpI.intValue() != m.intValue()) {
                            if (m < alpha) {
                                if (gameTmp != null) {
                                    gameTmp.updatePlayersTerritories();
                                }
                                return gameTmp;
                            }
                            gameTmp = g;
                        }
                    }
                }

            }
            if (gameTmp != null) {
                gameTmp.updatePlayersTerritories();
                paranoidTree.addChildMax();
                return gameTmp;
            }
            else {
                return nextStep.get(0);
            }
        }
        else {
            game.getCurrentPlayer().setAlive(false);
            game.checkVictory();
            return game;
        }
    }

    /**
     * Save paranoid calculation in a file
     */
    public void save() {
        String fileName =
            Main.RESULT_NAME + "-" + game.getTurn() + "-" + game.getCurrentPlayer().getPlayerID() + "_paranoid" +
                ".dot";
        DotGame.saveInFile(paranoidTree, fileName);
    }
}
