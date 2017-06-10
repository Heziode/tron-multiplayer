package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining whether or not to save the results of a game. Optional parameter.
 */
public final class SaveResult extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new RandomConf();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "result";
    }

    @Override
    public String getUsage() {
        return "boolean (default false) : true for save reslut of game in file (in CSV), false otherwise";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.SAVE_RESULT = Boolean.parseBoolean(System.getProperty(getParameterFullName()));
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING,
                    "A value of \"result\" is not a boolean (true or false). Fixed to false");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
