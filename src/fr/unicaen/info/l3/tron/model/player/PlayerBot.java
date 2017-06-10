package fr.unicaen.info.l3.tron.model.player;

import fr.unicaen.info.l3.tron.controller.Main;
import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.Grid;
import fr.unicaen.info.l3.tron.model.ai.ArtificialIntelligence;
import fr.unicaen.info.l3.tron.utils.Movement;

/**
 * Classe du joueur machine
 */
public final class PlayerBot extends AbstractPlayer implements Cloneable {

    /**
     * Logical constructor with default coalition 0
     *
     * @param column   Player column coordinate
     * @param line     Player line coordinate
     * @param playerID ID of player
     */
    public PlayerBot(int column, int line, int playerID) {
        super(column, line, playerID);
    }

    /**
     * Logical constructor
     *
     * @param column    Player column coordinate
     * @param line      Player line coordinate
     * @param coalition ID of coalition
     * @param playerID  ID of player
     */
    public PlayerBot(int column, int line, int coalition, int playerID) {
        super(column, line, coalition, playerID);
    }

    @Override
    public boolean canMoveTo(Movement movement, Grid board) {

        int newColumn = this.coordinates.getColumn() + movement.getDeltaColumn();
        int newLine = this.coordinates.getLine() + movement.getDeltaLine();

        // Checks that the move does not exceed the size of the game grid
        if ((newColumn < 0) || (newColumn > board.getSize() - 1) || (newLine < 0) || (newLine > board.getSize() - 1)) {
            return false;
        }

        // We look at the states of the neighboring squares
        switch (board.getGrid()[newColumn][newLine].getState()) {
            case NOTVISITED:
                return true;

            case OCCUPIED:
                return false;

            case VISITED:
                return false;
        }

        return false;
    }

    @Override
    public void move(Movement movement, Grid board) {
        if (canMoveTo(movement, board)) {
            board.changeState(coordinates, this);
            this.lastMovement = movement;
            this.coordinates.updateCoordonate(movement);
            board.changeState(coordinates, this);
        }
        else {
            this.alive = false;
        }
    }

    @Override
    public void play(Game game) {
        Game gameClone = game.clone();
        gameClone.updatePlayersTerritories();

        ArtificialIntelligence ai = new ArtificialIntelligence(gameClone);
        Game g = ai.paranoid(gameClone, Main.COALITIONS_DEPTH[coalition], Integer.MIN_VALUE, Integer.MAX_VALUE);

        // Saving paranoid calculation in DOT format
        if (Main.PARANOID_TRACEBACK) {
            ai.save();
        }

        if (g != null) {
            game.move(g.getLastPlayer().lastMovement);
        }


    }

    @Override
    public PlayerBot clone() {
        return (PlayerBot) super.clone();
    }
}
