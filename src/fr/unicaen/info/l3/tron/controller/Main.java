package fr.unicaen.info.l3.tron.controller;

import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.ressources.config.CoreConfigurator;
import fr.unicaen.info.l3.tron.ressources.config.runconfig.Configurator;
import fr.unicaen.info.l3.tron.utils.Coordinates2D;
import fr.unicaen.info.l3.tron.utils.SaveData;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of the app
 */
public final class Main {

    /**
     * Reference of current game
     */
    public static Game GAME;

    /**
     * Depth of research of each coalitions
     */
    public static int[] COALITIONS_DEPTH;

    /**
     * Number of players for each coalitions
     */
    public static int[] COALITIONS_PLAYER;

    /**
     * Total number of players
     */
    public static int TOTAL_PLAYERS;

    /**
     * Size of the grid
     */
    public static int GRID_SIZE;

    /**
     * TRUE for save paranoid calcul in language DOT for graph representation, FALSE otherwise (default: FALSE)
     */
    public static boolean PARANOID_TRACEBACK = false;

    /**
     * Location of first player to play (default: 0)
     */
    public static int FIRST_PLAYER_POSITION;

    /**
     * Order of each players. Values are id of coalitions
     */
    public static int[] PLAYER_ORDERING;

    /**
     * Location of first player (default: 0)
     */
    public static int FIRST_POSITION_SHIFT;

    /**
     * Location (coordinate [column;line]) of each players
     */
    public static Coordinates2D[] PLAYER_LOCATION;

    /**
     * TRUE for save result of game in file, FALSE otherwise (default: FALSE)
     */
    public static boolean SAVE_RESULT = false;

    /**
     * TRUE for placing players randomly, FALSE otherwise (default: FALSE)
     */
    public static boolean RANDOM = false;

    /**
     * If you want to save result of game, you can specify an name (default: random UUID)
     *
     * @see UUID#randomUUID()
     */
    public static String RESULT_NAME = UUID.randomUUID().toString();

    /**
     * TRUE for print result in terminal, FALSE otherwise
     */
    public static boolean PRINT_RESULT = false;

    /**
     * Private constructor for prevent instantiation
     */
    private Main() {
    }

    /**
     * Entry point of JVM
     *
     * @param args Command line args
     */
    public static void main(String[] args) {
        if (Configurator.getConfiguration()) {
            GAME = new Game();

            long startTime = 0;
            if (!SAVE_RESULT) {
                // Start calcul
                startTime = System.currentTimeMillis(); // Start time
            }

            // CALCUL
            GAME.running();

            if (!SAVE_RESULT) {
                // End of calcul
                long endTime = System.currentTimeMillis(); // End time
                long totalTime = endTime - startTime; // Total calcul time

                String time = String.format("%02d:%02d:%02d:%03d",
                    TimeUnit.MILLISECONDS.toHours(totalTime),
                    TimeUnit.MILLISECONDS.toMinutes(totalTime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totalTime)),
                    TimeUnit.MILLISECONDS.toSeconds(totalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalTime)),
                    TimeUnit.MILLISECONDS.toMillis(totalTime) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(totalTime))
                );
                System.out.println("Time : " + time);
            }
            else {
                String data = GAME.toCSV();
                if (PRINT_RESULT) { System.out.print(data); }
                else { SaveData.saveInFile(GAME.getCSVName(CoreConfigurator.RESULT_LOCATION), data); }
            }
        }
    }
}
