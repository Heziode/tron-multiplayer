package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of tracing the calculation of paranoid. Optional parameter.
 */
public final class ParanoidTraceback extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new FirstPlayerPosition();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "ptrace";
    }

    @Override
    public String getUsage() {
        return "boolean (default false) : true for save paranoid tree in DOT language, false otherwise";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.PARANOID_TRACEBACK = Boolean.parseBoolean(System.getProperty(getParameterFullName()));
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING, "A value of ptrace is not a boolean (true or false). Fixed " +
                    "to false.");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
