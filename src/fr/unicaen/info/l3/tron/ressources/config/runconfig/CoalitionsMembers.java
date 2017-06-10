package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of the number of players per coalition. Required setting.
 */
public final class CoalitionsMembers extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new CoalitionsCheck();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "nbCoal";
    }

    @Override
    public String getUsage() {
        return "number (separated by space) : number of member of each coalitions";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) == null) {
            addMessage(MessageType.ERROR, "A number of member of playerList is not defined.");
            return false;
        }
        else {
            incrementNbInstruction();
            Main.COALITIONS_PLAYER = getData(getParameterFullName(), MessageType.ERROR);
            return true;
        }
    }

    @Override
    protected boolean isRequired() {
        return true;
    }
}
