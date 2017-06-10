package fr.unicaen.info.l3.tron.model.player;

import fr.unicaen.info.l3.tron.utils.Coordinates2D;
import fr.unicaen.info.l3.tron.utils.Movement;

/**
 * Player's abstract class in case one wants to add other types of players thereafter
 */
public abstract class AbstractPlayer implements Player {

    /**
     * Coordinates where the player is on the grid
     *
     * @see Coordinates2D
     */
    protected Coordinates2D coordinates;

    /**
     * Last movement of the player
     *
     * @see Movement
     */
    protected Movement lastMovement;

    /**
     * Coalition number to which the player belongs
     */
    protected int coalition;

    /**
     * ID of this player
     */
    protected int playerID;

    /**
     * Defined if a player is alive or not
     */
    protected boolean alive;

    /**
     * Logical constructor with default coalition 0
     *
     * @param column   Player column coordinate
     * @param line     Player line coordinate
     * @param playerID ID of player
     */
    public AbstractPlayer(int column, int line, int playerID) {
        this(column, line, 0, playerID);
    }

    /**
     * Logical constructor
     *
     * @param column    Player column coordinate
     * @param line      Player line coordinate
     * @param coalition ID of coalition
     * @param playerID  ID of player
     */
    public AbstractPlayer(int column, int line, int coalition, int playerID) {
        this.coordinates = new Coordinates2D(column, line);
        this.coalition = coalition;
        this.playerID = playerID;
        this.alive = true;
    }

    /**
     * Accessor of lastMovement
     *
     * @return Recovers the player's last move
     */
    public final Movement getLastMovement() {
        return lastMovement;
    }

    /**
     * Accessor of coalition.
     *
     * @return Returns the coalition number to which the player belongs.
     */
    public final int getCoalition() {
        return coalition;
    }

    /**
     * Accessor of coordinates
     *
     * @return Retrieves the player's coordinates
     */
    public final Coordinates2D getCoordinates() {
        return coordinates;
    }

    /**
     * Accessor of line
     *
     * @return Recover the player's line coordinate
     */
    public final int getLine() {
        return coordinates.getLine();
    }

    /**
     * Accessor of column
     *
     * @return Recover the player's column coordinate
     */
    public final int getColumn() {
        return coordinates.getColumn();
    }

    /**
     * Accessor of PlayerID
     *
     * @return Retrieve player id
     */
    public final int getPlayerID() {
        return playerID;
    }

    /**
     * Boolean accessor of alive
     *
     * @return Returns TRUE if the player is alive, FALSE otherwise
     */
    public final boolean isAlive() {
        return alive;
    }

    /**
     * Mutator of alive
     *
     * @param alive New value
     */
    public final void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Observable accessor of alive
     *
     * @return Returns the observable {@link #alive} property of the player
     */
    public final boolean aliveProperty() {
        return alive;
    }

    @Override
    public AbstractPlayer clone() {
        AbstractPlayer p = null;
        try {
            p = (AbstractPlayer) super.clone();
            p.coordinates = new Coordinates2D(coordinates.getColumn(), coordinates.getLine());
            p.lastMovement = lastMovement;
            p.alive = alive;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return p;
    }
}
