package fr.unicaen.info.l3.tron.utils;

/**
 * Coordinates in a 2D coordinate system
 */
public final class Coordinates2D {

    /**
     * Column coordinate
     */
    private int column;

    /**
     * Line coordinate
     */
    private int line;

    /**
     * Logical constructor
     *
     * @param column Coordinate column (x)
     * @param line   Coordinate line (y)
     */
    public Coordinates2D(int column, int line) {
        this.column = column;
        this.line = line;
    }

    /**
     * Accessor of column
     *
     * @return Returns the column coordinate
     */
    public int getColumn() {
        return column;
    }

    /**
     * Mutator ofcolonne
     *
     * @param column New column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Accessor of line
     *
     * @return Returns the line coordinate
     */
    public int getLine() {
        return line;
    }

    /**
     * Mutator ofligne
     *
     * @param line New line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Updates the coordinate
     *
     * @param movement Movement to the new coordinate
     */
    public void updateCoordonate(Movement movement) {
        column += movement.getDeltaColumn();
        line += movement.getDeltaLine();
    }

    @Override
    public String toString() {
        return "[" + column + ";" + line + "]";
    }
}
