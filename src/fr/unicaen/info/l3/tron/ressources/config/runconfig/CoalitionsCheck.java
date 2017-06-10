package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class verifying whether there are as many coalitions as there are search depths.
 */
public final class CoalitionsCheck extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new PlayerOrdering();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    protected boolean handler() {
        if ((Main.COALITIONS_PLAYER == null) || (Main.COALITIONS_DEPTH == null)) {
            return false;
        }
        else {
            if (Main.COALITIONS_PLAYER.length != Main.COALITIONS_DEPTH.length) {
                addMessage(MessageType.ERROR, "Not same size for number of coalition and coalition depth.");
                return false;
            }
            else {
                incrementNbInstruction();
                Main.TOTAL_PLAYERS = 0;
                for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
                    Main.TOTAL_PLAYERS += Main.COALITIONS_PLAYER[i];
                }

                Main.PLAYER_ORDERING = new int[Main.TOTAL_PLAYERS];
                return true;
            }
        }
    }

    @Override
    protected boolean isRequired() {
        return true;
    }
}
