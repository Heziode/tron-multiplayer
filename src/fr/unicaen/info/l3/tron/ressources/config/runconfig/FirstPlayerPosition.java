package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining the first player to play. Optional parameter.
 */
public final class FirstPlayerPosition extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new PlayerShift();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "player.position";
    }

    @Override
    public String getUsage() {
        return "number (default 0) : position (in player list) of the first player to play";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                int tmpPlayerPosition = Integer.parseInt(System.getProperty(getParameterFullName()));

                if ((tmpPlayerPosition < 0) || (tmpPlayerPosition > Main.TOTAL_PLAYERS - 1)) {
                    addMessage(MessageType.WARNING, "Single player position need to be between 0 and " +
                        (Main.TOTAL_PLAYERS - 1) + ".");
                }
                else {
                    Main.FIRST_PLAYER_POSITION = tmpPlayerPosition;
                }
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING, "A value of position of the first player to play is not a " +
                    "number");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
