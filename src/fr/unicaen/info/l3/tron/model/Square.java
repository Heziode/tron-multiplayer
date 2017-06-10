package fr.unicaen.info.l3.tron.model;

import fr.unicaen.info.l3.tron.model.player.AbstractPlayer;
import fr.unicaen.info.l3.tron.utils.Coordinates2D;
import fr.unicaen.info.l3.tron.utils.Movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represent square of the grid
 */
public final class Square implements Cloneable {

    /**
     * State of this square
     */
    private State state;

    /**
     * Reference of player with action linked to this square
     */
    private AbstractPlayer player;

    /**
     * Coordinate of this square
     */
    private Coordinates2D coordinates;

    /**
     * Logical constructor
     *
     * @param column Column coordinate
     * @param line   Line coordinate
     */
    public Square(int column, int line) {
        this.coordinates = new Coordinates2D(column, line);
        this.state = State.NOTVISITED;
    }

    /**
     * Private constructor for passing to the next state
     *
     * @param coordinates Coordinate of this square
     * @param newState    New state
     * @param player      Reference of player with action linked to this square
     */
    private Square(Coordinates2D coordinates, State newState, AbstractPlayer player) {
        this.coordinates = coordinates;
        this.state = newState;
        this.player = player;

    }

    /**
     * Accessor of state
     *
     * @return Return state of this square
     * @see State
     */
    public State getState() {
        return state;
    }

    /**
     * Accessor of player
     *
     * @return Return player linked to this square
     */
    public AbstractPlayer getPlayer() {
        return player;
    }

    /**
     * Accessor of line
     *
     * @return Return line coordinate
     */
    public int getLine() {
        return coordinates.getLine();
    }

    /**
     * Accessor of colonne
     *
     * @return Return column coordinate
     */
    public int getColumn() {
        return coordinates.getColumn();
    }

    /**
     * Block the status of the square. The square is not accessible by any player.
     */
    public void disableSquare() { this.state = State.BLOCKED; }

    /**
     * Pass to the next state
     *
     * @param player Reference to the player linked to this square
     * @return Return a new instance of this object with the next state
     */
    public Square nextState(AbstractPlayer player) {
        return new Square(coordinates, this.state.next(), player);
    }

    /**
     * Retrieves the list of neighbors from the square
     *
     * @param grid                 Reference of the grid
     * @param includeOccupedSquare Determines whether to take into account the square occupied or not
     * @return Returns a list that contains all neighbors accessible from this square.
     */
    public List<Square> getNeighbor(Grid grid, boolean includeOccupedSquare) {
        List<Square> neighbor = new ArrayList<Square>();

        int newColumn;
        int newLine;
        for (Movement movement : Movement.values()) {
            newColumn = this.getColumn() + movement.getDeltaColumn();
            newLine = this.getLine() + movement.getDeltaLine();

            // Check if displacement are in the grid
            if ((newColumn >= 0) && (newColumn < grid.getSize()) && (newLine >= 0) && (newLine < grid.getSize())) {

                // Add neighbor only if this neighbor are not visited
                if (grid.getGrid()[newColumn][newLine].getState().equals(State.NOTVISITED)) {
                    neighbor.add(grid.getGrid()[newColumn][newLine]);
                }
                else if (includeOccupedSquare &&
                    (grid.getGrid()[newColumn][newLine].getState().equals(State.OCCUPIED))) {
                    neighbor.add(grid.getGrid()[newColumn][newLine]);
                }
            }
        }

        return neighbor;
    }

    /**
     * Reinitialize state of this square
     */
    public void resetState() {
        state = State.NOTVISITED;
        player = null;
    }

    @Override
    public Square clone() {
        Square s = null;
        try {
            s = (Square) super.clone();

            s.state = this.state;
            s.coordinates = new Coordinates2D(coordinates.getColumn(), coordinates.getLine());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return s;
    }

    public Square clone(Map<AbstractPlayer, AbstractPlayer> playerCloneReference) {
        Square s = this.clone();
        s.player = playerCloneReference.get(s.player);
        return s;
    }

    @Override
    public String toString() {
        return "Square [" + coordinates.getColumn() + ";" + coordinates.getLine() + "]";
    }
}
