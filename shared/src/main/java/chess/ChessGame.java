package chess;

import java.util.Collection;
import java.util.List;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor color;
    TeamColor turn;

    public ChessGame() {
        this.color = color;
        this.turn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.color;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard board = getBoard();
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            ChessPiece piece = board.getPiece(startPosition);
            Collection<ChessMove> possiblevalid = piece.pieceMoves(board, startPosition);
            if (this.color == TeamColor.WHITE) {
                for (int i = 0; i < possiblevalid.toArray().length; i++) {

                }
            }
            return piece.pieceMoves(board, startPosition);
        }

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean check = false;
        TeamColor otherTeamColor;
        if (teamColor == TeamColor.WHITE) {
            otherTeamColor = TeamColor.BLACK;
        } else {
            otherTeamColor = TeamColor.WHITE;
        }
        ChessBoard boardClone = this.getBoard().clone();
        ChessPosition ourKingPiece = this.getBoard().getKing(teamColor);
        Collection<ChessPosition> allOtherPositions = boardClone.getAllPositionsOfColor(otherTeamColor);
        for (ChessPosition blackPos : allOtherPositions) {
            ChessPiece otherPiece = boardClone.getPiece(blackPos);

        }
        return check;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return new ChessBoard();
    }

}
