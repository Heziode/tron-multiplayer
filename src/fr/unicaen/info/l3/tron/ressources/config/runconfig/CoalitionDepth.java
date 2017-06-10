package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of depth research of each coalition. Required setting.
 */
public final class CoalitionDepth extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new CoalitionsMembers();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "coal";
    }

    @Override
    public String getUsage() {
        return "number (separated by space) : depth research of each coalitions";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) == null) {
            addMessage(MessageType.ERROR, "A depth of playerList player is not defind.");
            return false;
        }
        else {
            incrementNbInstruction();
            Main.COALITIONS_DEPTH = getData(getParameterFullName(), MessageType.ERROR);
            return true;
        }
    }

    @Override
    protected boolean isRequired() {
        return true;
    }
}
