package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        //saves row and positions
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }


    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ChessPosition p = (ChessPosition) obj;
        return (this.row == p.row &&
                this.col == p.col);

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%d],[%d]", row, col);
    }
}
