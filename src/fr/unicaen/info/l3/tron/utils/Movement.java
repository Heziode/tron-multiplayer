package fr.unicaen.info.l3.tron.utils;

/**
 * Enumeration of different movements possible by the player.
 */
public enum Movement {

    /**
     * Upward movement
     */
    UP(0, 1),

    /**
     * Movement to the right
     */
    RIGHT(1, 0),

    /**
     * Movement down
     */
    DOWN(0, -1),

    /**
     * Movement to the left
     */
    LEFT(-1, 0);

    /**
     * Delta line
     */
    private int deltaLine;

    /**
     * Delta column
     */
    private int deltaColumn;


    /**
     * Logic constructor (automaticly call by enum)
     *
     * @param deltaColumn Column shift
     * @param delatLine   Line shift
     */
    Movement(int deltaColumn, int delatLine) {
        this.deltaColumn = deltaColumn;
        this.deltaLine = delatLine;
    }

    /**
     * Accessor
     *
     * @return Value of {@link #deltaLine}.
     */
    public int getDeltaLine() {
        return deltaLine;
    }

    /**
     * Accessor
     *
     * @return Value of {@link #deltaColumn}.
     */
    public int getDeltaColumn() {
        return this.deltaColumn;
    }
}

