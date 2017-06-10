package fr.unicaen.info.l3.tron.model;

/**
 * This enumeration contains all available states of square
 */
public enum State {

    /**
     * Square not accessible
     */
    BLOCKED,

    /**
     * Square not yet visit
     */
    NOTVISITED,

    /**
     * Square occupied by a player
     */
    OCCUPIED,

    /**
     * Square visited by player
     */
    VISITED;

    /**
     * Pass to the next state
     *
     * @return Return new state
     */
    public final State next() {
        return State.values()[(this.ordinal() + 1) % State.values().length];
    }
}
