package fr.unicaen.info.l3.tron.exceptions;

import fr.unicaen.info.l3.tron.ressources.config.CoreConfigurator;

/**
 * Exception triggered when the user sets a too small grid size
 */
public final class TooSmallGridSizeException extends Exception {

    /**
     * Defining a version number for serialization
     */
    static final long serialVersionUID = 0;

    /**
     * Exception triggered when the grid is too small.
     *
     * @see CoreConfigurator#MINIMUM_GRID_SIZE
     */
    public TooSmallGridSizeException() {
        super("Grid size is too small. Minimum : " + CoreConfigurator.MINIMUM_GRID_SIZE);
    }
}
