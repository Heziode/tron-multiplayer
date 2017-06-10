package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.controller.Main;

/**
 * Class managing the JVM parameter of the size of the grid. Required setting.
 */
public final class GridSize extends Configurator {

    /**
     * Reference to next link
     */
    private static final Configurator SUCCESSOR = new ParanoidTraceback();

    @Override
    public Configurator getSuccessor() {
        return SUCCESSOR;
    }

    @Override
    public String getParameterName() {
        return "gridSize";
    }

    @Override
    public String getUsage() {
        return "number : size of the grid";
    }

    @Override
    protected boolean handler() {
        if (System.getProperty(getParameterFullName()) != null) {
            incrementNbInstruction();
            try {
                Main.GRID_SIZE = Integer.parseInt(System.getProperty(getParameterFullName()));
                return true;
            } catch (NumberFormatException err) {
                addMessage(MessageType.ERROR, "A value of grid size is not a number.");
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean isRequired() {
        return false;
    }
}
