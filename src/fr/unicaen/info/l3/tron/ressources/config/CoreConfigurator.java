package fr.unicaen.info.l3.tron.ressources.config;

import fr.unicaen.info.l3.tron.utils.Utils;

/**
 * Contains basic configuration items
 */
public final class CoreConfigurator {

    /**
     * Sets the minimum number of players to play a game
     */
    public static final int MINIMUM_PLAYER = 2;

    /**
     * Sets minimum player spacing for player placement
     */
    public static final int MINIMUM_DISTANCE_BETWEEN_PLAYERS = 2;

    /**
     * Defines the minimum size of the grid based on the minimum number of players and the minimum space between each.
     */
    public static final int MINIMUM_GRID_SIZE = Utils.minimumGridSize(MINIMUM_PLAYER);

    /**
     * Cost for go from square to another
     */
    public static final int MOVEMENT_COST = 1;

    /**
     * Location of the results of games
     */
    public static final String RESULT_LOCATION = "result";

    /**
     * Private constructor to prevent instantiation
     */
    private CoreConfigurator() {
    }
}
