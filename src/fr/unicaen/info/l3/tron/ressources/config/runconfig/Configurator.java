package fr.unicaen.info.l3.tron.ressources.config.runconfig;

import fr.unicaen.info.l3.tron.utils.Coordinates2D;

/**
 * Class defining "boot configuration" services
 */
public abstract class Configurator {

    /**
     * Reference to the first link in the analysis chain
     */
    private static final Configurator DEFAULT_INSTANCE = new CoalitionDepth();

    /**
     * The default namespace where all JVM parameters are used by the program.
     * Example : -D{DEFAULT_JVM_NAMESPACE}.{parameter}
     */
    public static String DEFAULT_JVM_NAMESPACE = "tron";

    /**
     * Character strings containing the error messages encountered
     */
    private static StringBuffer error = new StringBuffer();

    /**
     * Character strings containing attention messages
     */
    private static StringBuffer warning = new StringBuffer();

    /**
     * Contains the number of errors encountered by reading the parameters
     */
    private static int nbError;

    /**
     * Contains the number of non-problematic (optional) errors encountered by reading the parameters
     */
    private static int nbWarning;

    /**
     * Counts the number of JVM statements found (used to display the usage)
     */
    private static int nbInstructions;

    /**
     * Configure the game. Displays usage, or errors if the game is misconfigured.
     *
     * @return Returns TRUE if the game is configured correctly, FALSE otherwise
     */
    public static boolean getConfiguration() {
        boolean config = configure();

        if (nbInstructions == 0) {
            showUsage();
            return false;
        }

        if (!config || nbError != 0) {
            System.err.println(nbError + " error" + (nbError > 1 ? "s" : "") + " :\n" + error.toString());
        }

        if (nbWarning != 0) {
            System.err.println(nbWarning + " error" + (nbWarning > 1 ? "s" : "") + " :\n" + warning.toString());
        }

        return nbError == 0;
    }

    /**
     * Increases by 1 the value of {@link #nbInstructions}.
     */
    protected static void incrementNbInstruction() {
        nbInstructions++;
    }

    /**
     * Configure the game by calling each handler
     *
     * @return Return TRUE if no critic configuration error, FALSE otherwise
     */
    private static boolean configure() {
        Configurator current = DEFAULT_INSTANCE;
        boolean result = true;

        while (current != null) {
            result &= current.handler();
            current = current.getSuccessor();
        }
        return result;
    }

    /**
     * Create an integer array from a system property
     *
     * @param property    System Properties
     * @param messageType Type of message that can be raised (error, attention)
     * @return Returns an integer array corresponding to the contents of the system property passed as a parameter
     */
    protected static int[] getData(String property, MessageType messageType) {
        String[] parts = System.getProperty(property).trim().split(" ");

        int[] tab = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                tab[i] = Integer.parseInt(parts[i]);
                if (tab[i] < 1) {
                    nbError++;
                    addMessage(messageType, "A value of " + property + " cannot contain a number less than 1.");
                    break;
                }
            } catch (NumberFormatException err) {
                nbError++;
                addMessage(messageType, "A value of " + property + " is not a number.");
                break;
            }
        }

        return tab;
    }

    /**
     * Retrieves the X and Y coordinates of a character string
     *
     * @param min         Minimum value that coordinates can have (assumes a square grid)
     * @param max         Maximum value that coordinates can have (assumes a square grid)
     * @param property    Name of the property to retrieve values (full name, with namespace)
     * @param messageType Type of message that can be raised (error, attention)
     * @return Returns an array containing all the coordinates read
     * @see #DEFAULT_JVM_NAMESPACE
     */
    protected static Coordinates2D[] getCoordinates2D(int min, int max, String property, MessageType messageType) {
        String[] parts = System.getProperty(property).trim().split(" ");
        Coordinates2D[] result = new Coordinates2D[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String[] coordinates = parts[i].trim().split(",");
            if (coordinates.length != 2) {
                addMessage(messageType, "Cannot read value \"" + parts[i] + "\" in " + property + ".");
            }
            else {
                try {
                    int column = Integer.parseInt(coordinates[0]);
                    int line = Integer.parseInt(coordinates[1]);
                    if ((column < min) || (line < min) || (column > max) || (line > max)) {
                        addMessage(messageType,
                            "A value of " + property + " cannot contain a number lower than " + min + " or " +
                                "greater than " + max + ".");
                        break;
                    }
                    else {
                        result[i] = new Coordinates2D(column, line);
                    }
                } catch (NumberFormatException err) {
                    addMessage(messageType, "Value \"" + parts[i].trim() + "\" in " + property + " is not a number.");
                    break;
                }
            }

        }
        return result;
    }

    /**
     * Add a message to the console following the message type (Error or Attention)
     *
     * @param messageType Type of message
     * @param message     Message to display
     */
    protected static void addMessage(MessageType messageType, String message) {
        switch (messageType) {
            case ERROR:
                nbError++;
                error.append(message).append("\n");
                break;
            case WARNING:
                nbWarning++;
                warning.append(message).append("\n");
                break;
        }
    }

    /**
     * Displays usage of the program in the terminal
     */
    private static void showUsage() {
        StringBuilder requiredParam = new StringBuilder("Param. required :\n");
        StringBuilder optionalParam = new StringBuilder("Param. optional :\n");

        Configurator current = DEFAULT_INSTANCE;

        while (current != null) {
            if (current.isRequired() && (current.getParameterName() != null)) {
                requiredParam.append("\t-D").append(current.getParameterFullName()).append(" : ")
                    .append(current.getUsage()).append("\n");
            }
            else if (current.getParameterName() != null) {
                optionalParam.append("\t-D").append(current.getParameterFullName()).append(" : ")
                    .append(current.getUsage()).append("\n");
            }
            current = current.getSuccessor();
        }

        System.out.println("Usage : java -jar <JVM param> <jar name>\n\n" + requiredParam.toString() + "\n" +
            optionalParam.toString());
    }

    /**
     * Returns the successor of an object (if it has one)
     *
     * @return Returns the successor of an object (if it has one)
     */
    public abstract Configurator getSuccessor();

    /**
     * Retrieve the JVM parameter
     *
     * @return Returns a string corresponding to the name of the parameter
     */
    public abstract String getParameterName();

    /**
     * Create usage display.
     *
     * @return Returns a string that will be displayed in the terminal (usage).
     */
    public abstract String getUsage();

    /**
     * Check parameter
     *
     * @return Returns TRUE if the parameter is correct, FALSE otherwise. In addition to this, it complements the
     * property {@link #error}
     */
    protected abstract boolean handler();

    /**
     * Define whether a parameter is mandatory or not
     *
     * @return Return TRUE if it is required, FALSE otherwise
     */
    protected abstract boolean isRequired();

    /**
     * Returns the full name of the parameter (with the JVM namespace)
     *
     * @return Returns a string corresponding to the name of a parameter
     */
    protected final String getParameterFullName() {
        return DEFAULT_JVM_NAMESPACE + "." + getParameterName();
    }

    /**
     * Define the types of messages that can be displayed in the terminal
     */
    protected enum MessageType {
        ERROR,
        WARNING
    }
}
