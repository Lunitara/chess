package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private ChessBoard board;

    public ChessGame() {
        this.color = TeamColor.WHITE;
        this.board = new ChessBoard();
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
        color = team;
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
     * loop through all the moves and try and find them and see if it would put
     * you in check and if it would then you don't add it to the set of moves
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> goodMoves = new ArrayList<>();
        ChessPiece pieceToCheck = this.board.getPiece(startPosition);
        Collection<ChessMove> possibleValid = pieceToCheck.pieceMoves(board, startPosition);
        for (ChessMove move : possibleValid) {
            ChessBoard boardClone = board.clone();
            boardClone.applyMove(move);
            if (!boardClone.boardInCheck(pieceToCheck.pieceColor)) {
                goodMoves.add(move);
            }
        }
        return goodMoves;

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceAtStart = this.board.getPiece(move.getStartPosition());
        if (pieceAtStart == null) {
            throw new InvalidMoveException();
        }
        if (move.getStartPosition() == null) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (!valid.contains(move)) {
            throw new InvalidMoveException();

        }
        if (this.color != pieceAtStart.getTeamColor()) {
            throw new InvalidMoveException();
        }
        this.board.applyMove(move);
        if (pieceAtStart.getTeamColor() == TeamColor.WHITE) {
            this.color = TeamColor.BLACK;
        } else {
            this.color = TeamColor.WHITE;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.boardInCheck(teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Boolean inCheckmate = true;
        Collection<ChessPosition> ourPieces  = this.board.getAllPositionsOfColor(teamColor);
        for (ChessPosition ourPiece : ourPieces) {
            if (!validMoves(ourPiece).isEmpty()) {
                inCheckmate = false;
            }
        }
        return inCheckmate;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition king = this.board.getKing(teamColor);
        if (!this.board.boardInCheck(teamColor)) {
            if (isInCheckmate(teamColor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.color, this.board);
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
        ChessGame g = (ChessGame) obj;
        return (g.board == this.board &&
                g.color == this.color);
    }
}
