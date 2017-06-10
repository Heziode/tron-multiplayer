package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining the offset of the first player. Optional parameter.
 */
public final class PlayerShift extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new PlayerLocation();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "player.shift";
    }

    @Override
    public String getUsage() {
        return "number (default 0) : offset of first player on the border of the grid";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.FIRST_POSITION_SHIFT = Integer.parseInt(System.getProperty(getParameterFullName()));

                int nbBorderSquare = Main.GRID_SIZE * 2 + 2 * (Main.GRID_SIZE - 2);
                if ((Main.FIRST_POSITION_SHIFT < 0) || (Main.FIRST_POSITION_SHIFT > (nbBorderSquare))) {
                    addMessage(MessageType.WARNING, "Player position shift need to be between 0 and " +
                        nbBorderSquare);
                }
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING, "A value of position shift is not a number");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
