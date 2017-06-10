package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of defining the name of the result file. Optional parameter.
 */
public final class ResultName extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new PrintResult();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "result.name";
    }

    @Override
    public String getUsage() {
        return "string : name of result file. default value is an random UUID";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            Main.RESULT_NAME = System.getProperty(getParameterFullName());
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
