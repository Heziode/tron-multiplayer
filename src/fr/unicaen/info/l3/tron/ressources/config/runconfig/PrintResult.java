package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining whether to display the result of game in CSV format in the terminal
 */
public final class PrintResult extends Configurator {
    @Override
    public Configurator getSuccessor() {
        return null;
    }

    @Override
    public String getParameterName() {
        return "result.print";
    }

    @Override
    public String getUsage() {
        return "boolean (default false) : true for print reslut (in CSV) in terminal, false otherwise";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.PRINT_RESULT = Boolean.parseBoolean(System.getProperty(getParameterFullName()));
            } catch (NumberFormatException err) {
                addMessage(MessageType.WARNING,
                    "A value of \"" + getParameterName() + "\" is not a boolean (true or false). Fixed to false");
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
