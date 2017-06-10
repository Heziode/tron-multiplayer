package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;
import fr.unicaen.info.l3.tron.utils.Coordinates2D;

/**
 * Class managing the JVM parameter of defining the placement of each player on the grid. Optional parameter.
 */
public final class PlayerLocation extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new SaveResult();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "player.location";
    }

    @Override
    public String getUsage() {
        return "pair of number (separated by space) : coordonates \"x,y\" of each players";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            Coordinates2D[] tmpCoordinates2D = getCoordinates2D(0, Main.GRID_SIZE - 1, getParameterFullName(),
                MessageType.WARNING);
            if (tmpCoordinates2D.length != Main.TOTAL_PLAYERS) {
                addMessage(MessageType.WARNING, "Not same size for number of players and players locations. " +
                    "Relpace by default location");
            }
            else {
                Main.PLAYER_LOCATION = tmpCoordinates2D;
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
