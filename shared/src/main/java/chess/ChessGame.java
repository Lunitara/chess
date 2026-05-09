package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
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
     * loop through all the moves and try and find them and see if it would put
     * you in check and if it would then you don't add it to the set of moves
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> goodMoves = new ArrayList<>();
        ChessPiece pieceToCheck = this.board.getPiece(startPosition);
        Collection<ChessMove> possiblevalid = pieceToCheck.pieceMoves(board, startPosition);
        for (ChessMove move : possiblevalid) {
            ChessBoard boardClone = board.clone();
            pieceToCheck.pieceMoves(boardClone, )
        }

    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (!valid.contains(move)) {
            throw new InvalidMoveException();

        }
        if (this.color != this.board.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException();
        }
        this.board.applyMove(move);


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor enemyTeamColor;
        if (teamColor == TeamColor.WHITE) {
            enemyTeamColor = TeamColor.BLACK;
        } else {
            enemyTeamColor = TeamColor.WHITE;
        }
        ChessPosition ourKingPiece = this.getBoard().getKing(teamColor);
        Collection<ChessPosition> allEnemyPositions = this.getBoard().getAllPositionsOfColor(enemyTeamColor);
        for (ChessPosition enemyPos : allEnemyPositions) {
            ChessPiece enemyPiece = getBoard().getPiece(enemyPos);
            Collection<ChessMove> possibleValidMoves = enemyPiece.pieceMoves(getBoard(), enemyPos);
            for (ChessMove move : possibleValidMoves) {
                if (move.getEndPosition() == ourKingPiece) {
                    return true;
                }
            }
        }
        return false;
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
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return  this.board;
    }

}
