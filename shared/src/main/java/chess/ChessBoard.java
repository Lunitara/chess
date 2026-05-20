package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow()-1][position.getColumn()-1];
    }


    public Collection<ChessPosition> getAllPositionsOfColor(ChessGame.TeamColor color) {
        List<ChessPosition> allPieces = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (this.getPiece(new ChessPosition(i,j)) != null && this.getPiece(new ChessPosition(i,j)).getTeamColor() == color) {
                    allPieces.add(new ChessPosition(i,j));
                }
            }
        }
        return allPieces;
    }
    public void applyMove(ChessMove move) {
        ChessPosition startingPosition = move.getStartPosition();
        ChessPiece startPiece = this.getPiece(startingPosition);
        if (move.getPromotionPiece() != null) {
            startPiece = new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece());
        }
        this.addPiece(move.getEndPosition(), startPiece);
        this.addPiece(move.getStartPosition(), null);
    }

    public boolean boardInCheck(ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor enemyTeamColor;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            enemyTeamColor = ChessGame.TeamColor.BLACK;
        } else {
            enemyTeamColor = ChessGame.TeamColor.WHITE;
        }
        ChessPosition ourKingPiece = this.getKing(teamColor);
        Collection<ChessPosition> allEnemyPositions = this.getAllPositionsOfColor(enemyTeamColor);
        for (ChessPosition enemyPos : allEnemyPositions) {
            ChessPiece enemyPiece = this.getPiece(enemyPos);
            Collection<ChessMove> possibleValidMoves = enemyPiece.pieceMoves(this, enemyPos);
            for (ChessMove move : possibleValidMoves) {
                if (move.getEndPosition().equals(ourKingPiece)) {
                    return true;
                }
            }
        }
        return false;
    }
    public void addAllPiecesToClone(ChessBoard boardClone) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (this.getPiece(new ChessPosition(i,j)) != null) {
                    boardClone.addPiece(new ChessPosition(i,j),this.getPiece(new ChessPosition(i,j)));
                }
            }
        }
    }

    public ChessPosition getKing(ChessGame.TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (getPiece(new ChessPosition(i,j)) != null &&
                        getPiece(new ChessPosition(i,j)).getTeamColor() == color &&
                        getPiece(new ChessPosition(i,j)).getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i,j);
                }
            }
        }
        return null;

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        this.addPiece(new ChessPosition(1,1),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,2),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,3),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,4),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1,5),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1,6),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,7),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,8),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        //add pawn
        for (int i = 1; i <= 8; i++) {
            this.addPiece(new ChessPosition(2,i),new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

        }
        //black
        this.addPiece(new ChessPosition(8,1),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,2),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,3),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,4),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8,5),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8,6),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,7),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,8),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        //add pawn
        for (int i = 1; i <= 8; i++) {
            this.addPiece(new ChessPosition(7,i),new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));

        }
    }

    @Override
    public ChessBoard clone() {
        ChessBoard clone = new ChessBoard();
        this.addAllPiecesToClone(clone);
        return clone;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.squares);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        ChessBoard b = (ChessBoard) obj;
        return Arrays.deepEquals(b.squares, this.squares);
    }
}
