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
    private boolean whiteLeftRookorKingMoved;
    private boolean whiteRightRookorKingMoved;
    private boolean blackLeftRookorKingMoved;
    private boolean blackRightRookorKingMoved;


    public ChessGame() {
        this.color = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.whiteRightRookorKingMoved = false;
        this.whiteLeftRookorKingMoved = false;
        this.blackLeftRookorKingMoved = false;
        this.blackRightRookorKingMoved = false;
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
        //castle logic
        //neither the king nor rook have moved since game start
        TeamColor currentColor = pieceToCheck.getTeamColor();

        castleLogic(currentColor, goodMoves);


        return goodMoves;

    }

    public void canCastle(TeamColor color, boolean rightDirection, Collection<ChessMove> goodMoves) {
        int kingRow;
        if (color == TeamColor.WHITE) {
            kingRow = 1;
        } else {
            kingRow = 8;
        }
        if (this.getBoard().boardInCheck(color)) {
            return;
        }
        int delta;
        int startRookCol;
        if (rightDirection) {
            delta = 1;
            startRookCol = 8;
        } else {
            delta = -1;
            startRookCol = 1;
        }
        int col = 5 + delta;
        while (col != startRookCol) {
            if (this.board.getPiece(new ChessPosition(kingRow, col)) != null) {
                return;
            }
            col += delta;
        }
        col = 5 + delta;
        int endKingCol = 5 + delta * 2;
        while (col != endKingCol) {
            ChessBoard boardClone = board.clone();
            boardClone.applyMove(new ChessMove(new ChessPosition(kingRow, 5),
                    new ChessPosition(kingRow, col), null));
            if (boardClone.boardInCheck(color)) {
                return;
            }
            col += delta;
        }
        ChessBoard boardClone = board.clone();
        boardClone.applyMove(new ChessMove(new ChessPosition(kingRow, 5),
                new ChessPosition(kingRow, endKingCol), null));
        boardClone.applyMove(new ChessMove(new ChessPosition(kingRow, startRookCol),
                new ChessPosition(kingRow, endKingCol - delta), null));
        if (boardClone.boardInCheck(color)) {
            return;
        }
        goodMoves.add(new ChessMove(new ChessPosition(kingRow, 5),
                new ChessPosition(kingRow, endKingCol), null));

    }

    public void castleLogic(TeamColor color,
                            Collection<ChessMove> goodMoves) {
        Collection<ChessMove> virtualKingMoves = new ArrayList<>();
        ChessPiece tempPiece = board.getPiece(new ChessPosition(1, 1));

        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.ROOK ||
                tempPiece.getTeamColor() != TeamColor.WHITE) {
            this.whiteLeftRookorKingMoved = true;
        }
        tempPiece = board.getPiece(new ChessPosition(1, 8));
        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.ROOK ||
                tempPiece.getTeamColor() != TeamColor.WHITE) {
            this.whiteRightRookorKingMoved = true;
        }
        //black
        tempPiece = board.getPiece(new ChessPosition(8, 1));
        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.ROOK ||
                tempPiece.getTeamColor() != TeamColor.BLACK) {
            this.blackLeftRookorKingMoved = true;
        }
        tempPiece = board.getPiece(new ChessPosition(8, 8));
        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.ROOK ||
                tempPiece.getTeamColor() != TeamColor.BLACK) {
            this.blackRightRookorKingMoved = true;
        }
        //check kings positions
        tempPiece = board.getPiece(new ChessPosition(1, 5));
        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.KING ||
                tempPiece.getTeamColor() != TeamColor.WHITE) {
            this.whiteLeftRookorKingMoved = true;
            this.whiteRightRookorKingMoved = true;
        }
        tempPiece = board.getPiece(new ChessPosition(8, 5));
        if (tempPiece == null || tempPiece.getPieceType() != ChessPiece.PieceType.KING ||
                tempPiece.getTeamColor() != TeamColor.BLACK) {
            this.blackLeftRookorKingMoved = true;
            this.blackRightRookorKingMoved = true;
        }
        if (color == TeamColor.WHITE) {
            if (!this.whiteRightRookorKingMoved) {
                canCastle(TeamColor.WHITE, true, goodMoves);
            }
            if (!this.whiteLeftRookorKingMoved) {
                canCastle(TeamColor.WHITE, false, goodMoves);
            }
        }
        if (color == TeamColor.BLACK) {
            if (!this.blackRightRookorKingMoved) {
                canCastle(TeamColor.BLACK, true, goodMoves);
            }
            if (!this.blackLeftRookorKingMoved) {
                canCastle(TeamColor.BLACK, false, goodMoves);
            }
        }
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
        checkKingRookPos(move);

        if (pieceAtStart.getTeamColor() == TeamColor.WHITE) {
            this.color = TeamColor.BLACK;
        } else {
            this.color = TeamColor.WHITE;
        }

    }
public void checkKingRookPos(ChessMove move) {
    ChessPiece pieceAtStart = this.board.getPiece(move.getStartPosition());

    //marks whether rook or king has been moved since game start for castling
    if ((pieceAtStart.getPieceType() == ChessPiece.PieceType.ROOK &&
            Objects.equals(move.getStartPosition(), new ChessPosition(1, 1))) ||
            (pieceAtStart.getPieceType() == ChessPiece.PieceType.KING &&
                    Objects.equals(move.getStartPosition(), new ChessPosition(1, 5)) &&
                    pieceAtStart.pieceColor == TeamColor.WHITE)) {
        this.whiteLeftRookorKingMoved = true;
    }
    if (move.getEndPosition().getRow() == 1 && move.getEndPosition().getColumn() == 1) {
        this.whiteLeftRookorKingMoved = true;
    }
    if ((pieceAtStart.getPieceType() == ChessPiece.PieceType.ROOK &&
            Objects.equals(move.getStartPosition(), new ChessPosition(1, 8))) ||
            (pieceAtStart.getPieceType() == ChessPiece.PieceType.KING &&
                    Objects.equals(move.getStartPosition(), new ChessPosition(1, 5)) &&
                    pieceAtStart.pieceColor == TeamColor.WHITE)) {
        this.whiteRightRookorKingMoved = true;
    }
    if (move.getEndPosition().getRow() == 1 && move.getEndPosition().getColumn() == 8) {
        this.whiteRightRookorKingMoved = true;
    }
    if ((pieceAtStart.getPieceType() == ChessPiece.PieceType.ROOK &&
            Objects.equals(move.getStartPosition(), new ChessPosition(8, 1))) ||
            (pieceAtStart.getPieceType() == ChessPiece.PieceType.KING &&
                    Objects.equals(move.getStartPosition(), new ChessPosition(8, 5)) &&
                    pieceAtStart.pieceColor == TeamColor.BLACK)) {
        this.blackLeftRookorKingMoved = true;
    }
    if (move.getEndPosition().getRow() == 8 && move.getEndPosition().getColumn() == 1) {
        this.blackLeftRookorKingMoved = true;
    }
    if ((pieceAtStart.getPieceType() == ChessPiece.PieceType.ROOK &&
            Objects.equals(move.getStartPosition(), new ChessPosition(8, 8))) ||
            (pieceAtStart.getPieceType() == ChessPiece.PieceType.KING &&
                    Objects.equals(move.getStartPosition(), new ChessPosition(8, 5)) &&
                    pieceAtStart.pieceColor == TeamColor.BLACK)) {
        this.blackRightRookorKingMoved = true;
    }
    if (move.getEndPosition().getRow() == 8 && move.getEndPosition().getColumn() == 8) {
        this.blackRightRookorKingMoved = true;
    }
    //white king left rook castle
    if (Objects.equals(move.getStartPosition(), new ChessPosition(1, 5)) &&
            Objects.equals(move.getEndPosition(), new ChessPosition(1, 3)) &&
            pieceAtStart.getPieceType() == ChessPiece.PieceType.KING) {
        this.board.applyMove(new ChessMove(new ChessPosition(1, 5),
                new ChessPosition(1, 3), null));
        this.board.applyMove(new ChessMove(new ChessPosition(1, 1),
                new ChessPosition(1, 4), null));
    }//white king right rook castle
    else if (Objects.equals(move.getStartPosition(), new ChessPosition(1, 5)) &&
            Objects.equals(move.getEndPosition(), new ChessPosition(1, 7)) &&
            pieceAtStart.getPieceType() == ChessPiece.PieceType.KING) {
        this.board.applyMove(new ChessMove(new ChessPosition(1, 5),
                new ChessPosition(1, 7), null));
        this.board.applyMove(new ChessMove(new ChessPosition(1, 8),
                new ChessPosition(1, 6), null));
    }
    //black king left rook castle
    else if (Objects.equals(move.getStartPosition(), new ChessPosition(8, 5)) &&
            Objects.equals(move.getEndPosition(), new ChessPosition(8, 3)) &&
            pieceAtStart.getPieceType() == ChessPiece.PieceType.KING) {
        this.board.applyMove(new ChessMove(new ChessPosition(8, 5),
                new ChessPosition(8, 3), null));
        this.board.applyMove(new ChessMove(new ChessPosition(8, 1),
                new ChessPosition(8, 4), null));
    }
    //black ing right rook castle
    else if (Objects.equals(move.getStartPosition(), new ChessPosition(8, 5)) &&
            Objects.equals(move.getEndPosition(), new ChessPosition(8, 7)) &&
            pieceAtStart.getPieceType() == ChessPiece.PieceType.KING) {
        this.board.applyMove(new ChessMove(new ChessPosition(8, 5),
                new ChessPosition(8, 7), null));
        this.board.applyMove(new ChessMove(new ChessPosition(8, 8),
                new ChessPosition(8, 6), null));
    }
    //end of castling code
    else {
        this.board.applyMove(move);
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
        boolean inCheckmate = true;
        Collection<ChessPosition> ourPieces = this.board.getAllPositionsOfColor(teamColor);
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
        if (!this.board.boardInCheck(teamColor)) {
            return isInCheckmate(teamColor);
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
        return Objects.equals(this.board, g.board) && Objects.equals(this.color, g.color);
    }
}
