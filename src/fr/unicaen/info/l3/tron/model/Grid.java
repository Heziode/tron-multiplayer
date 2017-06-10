package fr.unicaen.info.l3.tron.model;

import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.utils.Coordinates2D;
import fr.unicaen.info.l3.tron.utils.Movement;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Board of this game
 */
public final class Grid implements Cloneable {

    /**
     * Size of the grid
     */
    private final int size;

    /**
     * Conatains all square of this grid
     *
     * @see State
     */
    private Square[][] grid;

    /**
     * Logical constructor
     *
     * @param size Size of the grid
     */
    public Grid(int size) {
        this.size = size;
        this.grid = new Square[size][size];

        for (int column = 0; column < size; column++) {
            for (int line = 0; line < size; line++) {
                grid[column][line] = new Square(column, line);
            }
        }
    }

    /**
     * Change state of square
     *
     * @param column Column of square
     * @param line   Line of square
     * @param player Reference of the player
     */
    public void changeState(int column, int line, AbstractPlayer player) {
        grid[column][line] = grid[column][line].nextState(player);
    }

    /**
     * Change state of square
     *
     * @param coordinates Square coordinate
     * @param player      Reference of the player
     */
    public void changeState(Coordinates2D coordinates, AbstractPlayer player) {
        grid[coordinates.getColumn()][coordinates.getLine()]
            = grid[coordinates.getColumn()][coordinates.getLine()].nextState(player);
    }

    /**
     * Observable accessor of grid
     *
     * @return Return an observable grid
     */
    public Square[][] getGrid() {
        return grid;
    }

    /**
     * Accessor of size
     *
     * @return Retourne la taille d'un coté du plateau (plateau carrée)
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Get square with coordinate
     *
     * @param coordinates Square coordinate
     * @return Return the square in coordinate column-line
     */
    public Square getSquareAt(Coordinates2D coordinates) {
        return grid[coordinates.getColumn()][coordinates.getLine()];
    }

    /**
     * Retrieves the square next to another
     *
     * @param square   Case dont on shouaite avoir les voisines
     * @param movement Movement from the squaer to the other
     * @return Return square if exist or null
     */
    public Square getSquareAt(Square square, Movement movement) {
        int column = square.getColumn() + movement.getDeltaColumn();
        int line = square.getLine() + movement.getDeltaLine();

        if ((column < 0) || (column > size - 1) || (line < 0) || (line > size - 1)) {
            return null;
        }
        return grid[column][line];
    }

    /**
     * Retrieve the list of squares next to a square that are not yet visited
     *
     * @param square Reference square
     * @return Return a list of square (can be empty)
     */
    public List<Square> getAvailableNeighbour(Square square) {
        List<Square> list = new LinkedList<Square>();

        for (Movement movement : Movement.values()) {
            Square neighbour = getSquareAt(square, movement);

            if ((neighbour != null) && (neighbour.getState().equals(State.NOTVISITED))) {
                list.add(neighbour);
            }
        }

        return list;
    }

    /**
     * Initialize states of squares at NOTVISITED
     */
    public void initialize() {
        for (int column = 0; column < size; column++) {
            for (int line = 0; line < size; line++) {
                grid[column][line].resetState();
            }
        }
    }

    @Override
    public Grid clone() {
        Grid grid = null;

        try {
            grid = (Grid) super.clone();


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return grid;
    }

    public Grid clone(Map<AbstractPlayer, AbstractPlayer> playerCloneReference) {
        Grid grid = this.clone();

        grid.grid = new Square[size][size];
        for (int column = 0; column < size; column++) {
            for (int line = 0; line < size; line++) {
                grid.grid[column][line] = this.grid[column][line].clone(playerCloneReference);
            }
        }
        return grid;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int line = grid.length - 1; line >= 0; line--) {
            result.append("|\t");
            for (int column = 0; column < grid.length; column++) {
                result.append(grid[column][line].getState()).append("\t|\t");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
