package fr.unicaen.info.l3.tron.model.ai;

import fr.unicaen.info.l3.tron.model.Game;
import fr.unicaen.info.l3.tron.model.Grid;
import fr.unicaen.info.l3.tron.model.Square;
import fr.unicaen.info.l3.tron.model.State;
import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.utils.Utils;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Artificial Intelligence Utility Class (Calculation of Shortest Path, Territories, etc.)
 */
public final class AIUtils {

    /**
     * Private constructor for prevent instantiation
     */
    private AIUtils() {
    }

    /**
     * Implementation of the "Uniform Cost Search" algorithm, a Dijkstra with priority queue.
     *
     * @param start                Start square
     * @param goal                 End square
     * @param grid                 Reference of the grid
     * @param includeOccupedSquare TRUE for finding in occuped square, FALSE otherwise
     * @param maxDistance          Maximum distance to calculate between two boxes (this is an optimization for this
     *                             game)
     * @return Returns a list of square to go from the starting square to the finish square or null if no path finds
     * @see
     * <a href="https://en.wikipedia.org/wiki/Dijkstra's_algorithm#Practical_optimizations_and_infinite_graphs">Wikipedia
     * : Practical optimizations and infinite graphs</a>
     */
    public static List<Square> uniformCostSearch(Square start, Square goal, Grid grid, boolean includeOccupedSquare,
                                                 int maxDistance) {

        // Current node
        SquareComparator node;

        // Queue of no visited squares
        PriorityQueue<SquareComparator> frontier = new PriorityQueue<SquareComparator>();
        frontier.add(new SquareComparator(start));

        //  Queue of visited squares
        LinkedList<SquareComparator> explored = new LinkedList<SquareComparator>();

        // Key = current node, value = parent
        Map<Square, Square> map = new HashMap<Square, Square>();

        while (!frontier.isEmpty()) {
            node = frontier.poll();

            if (node.getCost() >= maxDistance) {
                return null;
            }

            if (Utils.isEquals(node.getSquare(), goal)) {
                return reconstructPath(map, node.getSquare());
            }
            explored.add(node);

            List<Square> blockWorldsNeighbor =
                node.getSquare().getNeighbor(grid, includeOccupedSquare); // Neighbor of node
            int currentCost = node.getCost() + 1;

            for (Square neighbor : blockWorldsNeighbor) {
                if (!inList(explored, neighbor)) {
                    if (!inList(frontier, neighbor)) {
                        frontier.add(new SquareComparator(neighbor, currentCost));
                        map.put(neighbor, node.getSquare());
                    }
                    else {
                        SquareComparator element = getElementFromPriorityQueue(frontier, neighbor);
                        if (element.getCost() > currentCost) {
                            element.setCost(currentCost);
                            map.put(neighbor, node.getSquare());
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Implementation of the "Uniform Cost Search" algorithm, a Dijkstra with priority queue.<br>
     * Call {@link #uniformCostSearch(Square, Square, Grid, boolean, int)} with maxDistance set to #Integer.MAX_VALUE.
     * {#Integer.MAX_VALUE}.
     *
     * @param start                Start square
     * @param goal                 End square
     * @param grid                 Reference of the grid
     * @param includeOccupedSquare TRUE for finding in occuped square, FALSE otherwise
     * @return Returns a list of square to go from the starting square to the finish square or null if no path finds
     */
    public static List<Square> uniformCostSearch(Square start, Square goal, Grid grid, boolean includeOccupedSquare) {
        return uniformCostSearch(start, goal, grid, includeOccupedSquare, Integer.MAX_VALUE);
    }

    /**
     * Calculates the territory of each player with the player of reference, the current player
     *
     * @param game Reference of current game
     * @return Returns a map with a player key and a value of a list of the boxes of its territory
     */
    public static Map<AbstractPlayer, List<Square>> territory(Game game) {
        return territory(game, game.getCurrentPlayer());
    }

    /**
     * Calculate the territory of each player
     *
     * @param game          Reference of current game
     * @param currentPlayer Reference of current player
     * @return Returns a map with a player key and a value of a list of the boxes of its territory
     */
    public static Map<AbstractPlayer, List<Square>> territory(Game game, AbstractPlayer currentPlayer) {
        Grid grid = game.getGrid();

        Map<AbstractPlayer, List<Square>> map = new HashMap<AbstractPlayer, List<Square>>();

        // initialile map
        for (AbstractPlayer player : game.getPlayerList()) {
            map.put(player, new LinkedList<Square>());
        }

        // Calculates the nearest player for each square of the grid
        for (Square[] line : grid.getGrid()) {
            for (Square square : line) {

                if (square.getState().equals(State.NOTVISITED)) {
                    DistancePlayerSquare distancePlayerSquare = new DistancePlayerSquare(null, Integer.MAX_VALUE);
                    // Get the player closest to the square
                    for (int i = 0; i < game.getPlayerList().size(); i++) {
                        if (game.getPlayerList().get(i).isAlive()) {
                            Square playerSquare =
                                grid.getGrid()[game.getPlayerList().get(i).getColumn()][game.getPlayerList().get(i)
                                    .getLine()];

                            List<Square> currentPlayerPath =
                                uniformCostSearch(playerSquare, square, grid, false);

                            if (currentPlayerPath != null) {
                                distancePlayerSquare = distancePlayerSquare.distance > currentPlayerPath.size() ?
                                    new DistancePlayerSquare(game.getPlayerList().get(i),
                                        currentPlayerPath.size()) : distancePlayerSquare;
                            }
                        }
                    }

                    // Then it is added to the map if it is accessible
                    if (distancePlayerSquare.playerIndex != null) {
                        map.get(distancePlayerSquare.playerIndex).add(square);
                    }
                    else {
                        // The square is no longer accessible by any player
                        square.disableSquare();
                    }
                }
            }
        }
        return map;
    }

    /**
     * Checks if the square passed as a parameter exists in the list
     *
     * @param list   Square list
     * @param square Case to check
     * @return Return TRUE if square is in list, FALSE otherwise
     */
    private static boolean inList(AbstractCollection<SquareComparator> list, Square square) {
        Iterator<SquareComparator> iterator = list.iterator();

        while (iterator.hasNext()) {
            Square element = iterator.next().getSquare();
            if (Utils.isEquals(element, square)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rebuilds the path between a start square and end square
     *
     * @param cameFrom Map containing a square as a key and a reference to the parent square
     * @param current  Current square
     * @return Returns the way of the departure to the arrival
     */
    private static List<Square> reconstructPath(Map<Square, Square> cameFrom, Square current) {
        LinkedList<Square> totalPath = new LinkedList<Square>();
        totalPath.add(current);

        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }

        // Reverse the list
        for (int i = 0, j = totalPath.size() - 1; i < j; i++) {
            totalPath.add(i, totalPath.remove(j));
        }

        return totalPath;
    }

    /**
     * Retrieves an item from a priority queue
     *
     * @param list   Reference of a priority queue
     * @param square Element to recover
     * @return Returns the item in the list or null if it does not exist
     */
    public static SquareComparator getElementFromPriorityQueue(PriorityQueue<SquareComparator> list, Square square) {
        Iterator<SquareComparator> iterator = list.iterator();

        while (iterator.hasNext()) {
            SquareComparator element = iterator.next();
            if (Utils.isEquals(element.getSquare(), square)) {
                return element;
            }
        }
        return null;
    }


    /**
     * Square comparator
     */
    public static final class SquareComparator implements Comparable<SquareComparator> {

        /**
         * Reference of one square
         */
        private Square square;

        /**
         * Cost to access this square
         */
        private int cost;

        /**
         * Logical constructor. Initialize the cost to 0
         *
         * @param square Reference to the square associated with the comparator
         */
        public SquareComparator(Square square) {
            this(square, 0);
        }

        /**
         * Logical constructor.
         *
         * @param square Reference to the square associated with the comparator
         * @param cost   Square cost
         */
        public SquareComparator(Square square, int cost) {
            this.square = square;
            this.cost = cost;
        }

        /**
         * Accessor of square
         *
         * @return Returns the square associated with the comparator
         */
        public Square getSquare() {
            return square;
        }

        /**
         * Accessor of cost
         *
         * @return Returns the cost to go from one square to this one
         */
        public int getCost() {
            return cost;
        }

        /**
         * Mutator of cost
         *
         * @param cost New cost
         */
        public void setCost(int cost) {
            this.cost = cost;
        }

        @Override
        public int compareTo(SquareComparator squareComparator) {
            if (this.cost < squareComparator.cost) {
                return -1;
            }
            else if (this.cost > squareComparator.cost) {
                return 1;
            }
            return 0;
        }
    }

    /**
     * Class to store a distance between a player and a square
     */
    private static class DistancePlayerSquare {
        AbstractPlayer playerIndex;
        int distance;

        DistancePlayerSquare(AbstractPlayer playerIndex, int distance) {
            this.playerIndex = playerIndex;
            this.distance = distance;
        }
    }
}
