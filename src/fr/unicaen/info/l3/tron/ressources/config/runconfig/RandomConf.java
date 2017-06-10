package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining the placement of players randomly. Optional parameter.
 */
public final class RandomConf extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new ResultName();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "random";
    }

    @Override
    public String getUsage() {
        return "boolean (default false) : true for enable random player placement, first player and grid size, false" +
            " " +
            "otherwise";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.RANDOM = Boolean.parseBoolean(System.getProperty(getParameterFullName()));
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING, "A value of random is not a boolean (true or false). Fixed to true");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
