package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    final int row;
    final int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {

        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {

        return this.col;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = result * 31 + col;
        result = result * 31 + row;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ChessPosition p = (ChessPosition) obj;
        return (p.col == this.col && p.row == this.row);
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }
}
