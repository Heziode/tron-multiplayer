package fr.unicaen.info.l3.tron.model.player;

import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.Grid;
import fr.unicaen.info.l3.tron.utils.Movement;

/**
 * Interface of the player that requires the implementation of certain methods.
 */
public interface Player {

    /**
     * Method for checking whether a move is feasible or not
     *
     * @param movement The movement to be made
     * @param board    The game board
     * @return A boolean that indicates whether the move is feasible or not
     */
    boolean canMoveTo(Movement movement, Grid board);

    /**
     * A method that allows the player to move in a given movement
     *
     * @param movement Movement
     * @param board    Reference of game board
     */
    void move(Movement movement, Grid board);

    /**
     * Method allowing the player to play his turn
     *
     * @param game Reference to a game to which the player is attached
     */
    void play(Game game);
}
