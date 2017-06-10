package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of the order of the players. Optional parameter.
 */
public final class PlayerOrdering extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new GridSize();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "player.order";
    }

    @Override
    public String getUsage() {
        return "number (separated by space) : order of players by coalition (ex : 0 2 1 2 =  place one player of " +
            "coalition 0 first, next is one player of coalition 2, etc.)";
    }

    @Override
    protected boolean handler() {
        if ((Main.COALITIONS_PLAYER != null) && (Main.COALITIONS_DEPTH != null) &&
            (Main.COALITIONS_PLAYER.length == Main.COALITIONS_DEPTH.length)) {
            incrementNbInstruction();
            if (System.getProperty(getParameterFullName()) != null) {
                Main.PLAYER_ORDERING = getData(getParameterFullName(), MessageType.WARNING);
            }
            else {
                int index = 0;
                for (int i = 0; i < Main.COALITIONS_PLAYER.length; i++) {
                    for (int j = 0; j < Main.COALITIONS_PLAYER[i]; j++) {
                        Main.PLAYER_ORDERING[index] = i;
                        index++;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
