package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        //generate by piece using pragmatic functions plausible places it could move to
        if (piece.getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        return List.of();
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        //moving upright the board
        directionUpdater(1, 1, board, myPosition, available);
        //moving upleft the board
        directionUpdater(-1, 1, board, myPosition, available);
        //moving downright the board
        directionUpdater(1, -1, board, myPosition, available);
        //moving downleft the board
        directionUpdater(-1, -1, board, myPosition, available);
        return available;
    }

    public void directionUpdater(int col_delta, int row_delta, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        ChessPiece piece = board.getPiece(myPosition);
        int count = 1;
        while (myPosition.getColumn() + count * col_delta <= 8 &&
                myPosition.getRow() + count * row_delta <= 8 &&
                myPosition.getColumn() + count * col_delta >= 1 &&
                myPosition.getRow() + count * row_delta >= 1 &&
                board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) == null) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
            count++;
        }
        if (myPosition.getColumn() + count * col_delta <= 8 &&
                myPosition.getRow() + count * row_delta <= 8 &&
                myPosition.getColumn() + count * col_delta >= 1 &&
                myPosition.getRow() + count * row_delta >= 1 &&
                board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) != null &&
                board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)).pieceColor != piece.pieceColor) {
            moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
        }

    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        //if piece of opposite color is in row+1col+1 or row-1col+1 then it can move there
        //can also move col+1 as long as there is not a piece already there
        //moves once or twice first turn
        //promotes to anything at the end
        //
        ChessPiece upright = board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1));
        if (upright != null) {
            if (upright.pieceColor != piece.pieceColor) {
                //
            }
        }
        return List.of(new ChessMove(new ChessPosition(5, +1), new ChessPosition(1, 8), null));
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
        ChessPiece p = (ChessPiece) obj;
        return (this.pieceColor == p.pieceColor &&
                this.type == p.type);

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
