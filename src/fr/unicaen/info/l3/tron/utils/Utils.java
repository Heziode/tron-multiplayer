package fr.unicaen.info.l3.tron.utils;

import fr.unicaen.info.l3.tron.controller.Main;
import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.Square;
import fr.unicaen.info.l3.tron.ressources.config.CoreConfigurator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of utility methods
 */
public final class Utils {

    /**
     * Private constructor to prevent instantiation
     */
    private Utils() {
    }

    /**
     * Gets all the squares at the edge of the grid
     *
     * @param game Reference to a game state
     * @return Returns a list containing all the squares
     */
    public static List<Square> getBorderSquare(Game game) {
        ArrayList<Square> result = new ArrayList<Square>();

        int column = 0;
        int line = 0;

        for (; column < Main.GRID_SIZE; column++) { result.add(game.getGrid().getGrid()[column][line]);}
        column--;
        line++;
        for (; line < Main.GRID_SIZE; line++) { result.add(game.getGrid().getGrid()[column][line]); }
        line--;
        column--;
        for (; column >= 0; column--) { result.add(game.getGrid().getGrid()[column][line]); }
        column++;
        line--;
        for (; line > 0; line--) { result.add(game.getGrid().getGrid()[column][line]); }

        return result;
    }

    /**
     * Returns the maximum number of players for a side grid of n.
     * <br>
     * The maximum number of players is calculated so that the minimum distance between each player is X squares (
     * defined by {@link CoreConfigurator#MINIMUM_DISTANCE_BETWEEN_PLAYERS}).
     * <br>
     * Given that we are working on a square-sized grid, the size passed as a parameter is multiplied by 4 to obtain
     * the
     * number of squares of the border of the grid to which or subtracts 4 so as to count the boxes of the angles only
     * once.
     *
     * @param size Length of the side of the grid (square grid)
     * @return Returns an integer greater or equal than 0
     */
    public static int maxPlayer(int size) {
        return ((Double) Math.floor((size * 4 - 4) / (CoreConfigurator.MINIMUM_DISTANCE_BETWEEN_PLAYERS + 1)))
            .intValue();
    }

    /**
     * Inverse of {@link #maxPlayer(int)}. From a player's number, it determines the minimum size that the board must
     * have.
     *
     * @param nbPlayer Number of player for get minimum grid size
     * @return Returns an integer corresponding to the minimum size that the board must have to accommodate
     * <b>nbPlayer</b> players
     */
    public static int minimumGridSize(int nbPlayer) {
        return ((Double) Math.ceil((double) (nbPlayer * CoreConfigurator.MINIMUM_DISTANCE_BETWEEN_PLAYERS + 4 +
            CoreConfigurator.MINIMUM_DISTANCE_BETWEEN_PLAYERS) / 4)).intValue();
    }

    /**
     * Calculates the optimal distance between each player.
     *
     * @param g Size of the grid
     * @param j Numbers of players
     * @return Returns the optimal distance between each player. Can not be less than 2.
     */
    public static int getOptimalDistance(int g, int j) {
        int surface = 4 * g - 4; // Circumference of the grid (number of blocks on the belt)
        int max = surface - j; // Maximum value not to be exceeded
        int optimalDistance = Math.round(max / j);

        if (optimalDistance < 2) {
            return 2;
        }
        if ((surface - max > optimalDistance) && (j * (optimalDistance + 1) <= (max))) {
            return optimalDistance + 1;
        }
        return optimalDistance + 1;
    }

    /**
     * Test the equality between two squares
     *
     * @param s1 Square to compare
     * @param s2 Square to compare
     * @return Returns TRUE if the cells have the same coordinates, FALSE otherwise
     */
    public static boolean isEquals(Square s1, Square s2) {
        return (s1.getColumn() == s2.getColumn()) && (s1.getLine() == s2.getLine());
    }

    /**
     * Removes all items from a list
     *
     * @param list List to empty
     * @param <T>  Generic parameter that can be anything
     */
    public static <T> void removeAll(List<T> list) {
        while (list.size() > 0) {
            list.remove(list.size() - 1);
        }
    }

    /**
     * Displays a message in the console only if the application is set to (if for example {@link Main#SAVE_RESULT}
     * is false)
     *
     * @param message Message to display
     */
    public static void println(String message) {
        if (!Main.SAVE_RESULT) {
            System.out.println(message);
        }
    }

    /**
     * Checks if a coordinate is in a list
     *
     * @param coordinates2D Coordinate to check
     * @param list          List in which the coordinates to be compared are located
     * @return Returns TRUE if the item is in the list, FALSE otherwise
     */
    public static boolean inList(Coordinates2D coordinates2D, Coordinates2D[] list) {
        for (Coordinates2D aList : list) {
            if ((aList != null) && (aList.getColumn() == coordinates2D.getColumn()) &&
                (aList.getLine() == coordinates2D.getLine())) { return true; }
        }

        return false;
    }
}
