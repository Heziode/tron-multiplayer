package fr.unicaen.info.l3.tron.exceptions;

import fr.unicaen.info.l3.tron.ressources.config.CoreConfigurator;

/**
 * Exception triggered when the number of players is not enough or too large for a defined grid size
 */
public final class InvalidNumberOfPlayerException extends Exception {

    /**
     * Defining a version number for serialization
     */
    static final long serialVersionUID = 0;

    /**
     * Exception triggered when the number of players is invalid
     *
     * @param maxPlayer Maximum number of players
     * @param gridSize  Grid size
     */
    public InvalidNumberOfPlayerException(int maxPlayer, int gridSize) {
        super("Number of player must be between " + CoreConfigurator.MINIMUM_PLAYER + " and " + maxPlayer +
            " for a grid size " + gridSize);
    }

}
