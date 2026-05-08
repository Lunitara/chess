package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{
    final ChessGame.TeamColor pieceColor;
    final PieceType type;
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
        List<ChessMove> available = new ArrayList<>();
        if (piece.getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition, available);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            return rookMoves(board, myPosition, available);
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            return queenMoves(board, myPosition, available);
        }
        if (piece.getPieceType() == PieceType.KING) {
            return kingMoves(board, myPosition, available);
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, available);
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition, available);
        }
        return List.of();
    }


    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.pieceColor == ChessGame.TeamColor.WHITE) {
            whitePawnMoves(board, myPosition, available);
        }
        else {
            blackPawnMoves(board, myPosition, available);
        }

        return available;
    }

    public void blackPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece downright;
        ChessPiece downleft;
        if (myPosition.getRow() - 1 <1 || myPosition.getColumn() +1 >8) {
            downright = null;
        }
        else {
            downright = board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1));
        }
        if (myPosition.getRow() - 1 < 1 || myPosition.getColumn() -1 <1) {
            downleft = null;
        }
        else {
            downleft = board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1));
        }
        //start on row 2
        if (myPosition.getRow() == 7) {
            //if there's no one right in front
            if (board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()), null));
            }
            //if there's no one up to two in front
            if (board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow()-2, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -2, myPosition.getColumn()), null));
            }
            //downright
            if (downright != null && downright.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()+1), null));

            }
            //downleft
            if (downleft != null && downleft.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1), null));

            }
        }
        else if (myPosition.getRow() == 2) {
            PieceType[] possibles = {
                    PieceType.KNIGHT,
                    PieceType.QUEEN,
                    PieceType.BISHOP,
                    PieceType.ROOK
            };
            if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()), possibility));

                }
            }
            //downright
            if (downright != null && downright.pieceColor != piece.pieceColor) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()+1), possibility));

                }
            }
            //downleft
            if (downleft != null && downleft.pieceColor != piece.pieceColor) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1), possibility));

                }
            }
        }
        else {
            if (board.getPiece(new ChessPosition(myPosition.getRow()-1, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()), null));
            }
            //downright
            if (downright != null && downright.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()+1), null));

            }
            //downleft
            if (downleft != null && downleft.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn()-1), null));

            }
        }
    }


    public void whitePawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece upright;
        ChessPiece upleft;
        if (myPosition.getRow() + 1 > 8 || myPosition.getColumn() +1 >8) {
            upright = null;
        }
        else {
            upright = board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1));
        }
        if (myPosition.getRow() + 1 >8 || myPosition.getColumn() -1 <1) {
            upleft = null;
        }
        else {
            upleft = board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1));
        }
        //start on row 2
        if (myPosition.getRow() == 2) {
            //if there's no one right in front
            if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()), null));
            }
            //if there's no one up to two in front
            if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow()+2, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +2, myPosition.getColumn()), null));
            }
            //upright
            if (upright != null && upright.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()+1), null));

            }
            //upleft
            if (upleft != null && upleft.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()-1), null));

            }
        }
        else if (myPosition.getRow() == 7) {
            PieceType[] possibles = {
                    PieceType.KNIGHT,
                    PieceType.QUEEN,
                    PieceType.BISHOP,
                    PieceType.ROOK
            };
            if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()), possibility));

                }
            }
            //upright
            if (upright != null && upright.pieceColor != piece.pieceColor) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()+1), possibility));

                }
            }
            //upleft
            if (upleft != null && upleft.pieceColor != piece.pieceColor) {
                for (PieceType possibility: possibles) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()-1), possibility));

                }
            }
        }
        else {
            if (board.getPiece(new ChessPosition(myPosition.getRow()+1, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()), null));
            }
            //upright
            if (upright != null && upright.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()+1), null));

            }
            //upleft
            if (upleft != null && upleft.pieceColor != piece.pieceColor) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() +1, myPosition.getColumn()-1), null));

            }
        }
    }


    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {

        directionUpdater(1,1,board, myPosition, available);
        directionUpdater(1,-1,board, myPosition, available);
        directionUpdater(-1,1,board, myPosition, available);
        directionUpdater(-1,-1,board, myPosition, available);

        return available;
    }
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {

        directionUpdater(2,1,board, myPosition, available);
        directionUpdater(2,-1,board, myPosition, available);
        directionUpdater(-2,1,board, myPosition, available);
        directionUpdater(-2,-1,board, myPosition, available);
        directionUpdater(1,2,board, myPosition, available);
        directionUpdater(1,-2,board, myPosition, available);
        directionUpdater(-1,2,board, myPosition, available);
        directionUpdater(-1,-2,board, myPosition, available);

        return available;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {

        directionUpdater(0,1,board, myPosition, available);
        directionUpdater(0,-1,board, myPosition, available);
        directionUpdater(1,0,board, myPosition, available);
        directionUpdater(-1,0,board, myPosition, available);

        return available;
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {

        directionUpdater(0,1,board, myPosition, available);
        directionUpdater(0,-1,board, myPosition, available);
        directionUpdater(1,0,board, myPosition, available);
        directionUpdater(-1,0,board, myPosition, available);
        directionUpdater(1,1,board, myPosition, available);
        directionUpdater(1,-1,board, myPosition, available);
        directionUpdater(-1,1,board, myPosition, available);
        directionUpdater(-1,-1,board, myPosition, available);

        return available;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {

        directionUpdater(0,1,board, myPosition, available);
        directionUpdater(0,-1,board, myPosition, available);
        directionUpdater(1,0,board, myPosition, available);
        directionUpdater(-1,0,board, myPosition, available);
        directionUpdater(1,1,board, myPosition, available);
        directionUpdater(1,-1,board, myPosition, available);
        directionUpdater(-1,1,board, myPosition, available);
        directionUpdater(-1,-1,board, myPosition, available);

        return available;
    }

    public void directionUpdater(int row_delta, int col_delta, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
        ChessPiece piece = board.getPiece(myPosition);
        int count = 1;
        if (piece.getPieceType() == PieceType.KING || piece.getPieceType() == PieceType.KNIGHT ) {
            if (myPosition.getRow() + count*row_delta<= 8 &&
                    myPosition.getColumn() + count*col_delta<= 8 &&
                    myPosition.getRow() + count*row_delta>= 1 &&
                    myPosition.getColumn() + count*col_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow()+ count*row_delta, myPosition.getColumn()+ count*col_delta)) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
            }
            if (myPosition.getRow() + count*row_delta<= 8 &&
                    myPosition.getColumn() + count*col_delta<= 8 &&
                    myPosition.getRow() + count*row_delta>= 1 &&
                    myPosition.getColumn() + count*col_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow()+ count*row_delta, myPosition.getColumn()+ count*col_delta)) != null &&
                    piece.getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow()+ count*row_delta, myPosition.getColumn()+ count*col_delta)).getTeamColor()) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+ count*row_delta, myPosition.getColumn()+ count*col_delta), null));

            }

        }
        else {
            while (myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta >= 1 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                count++;
            }
            if (myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta >= 1 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) != null &&
                    piece.getTeamColor() != board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)).getTeamColor()) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));

            }
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pieceColor, this.type);
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
        ChessPiece p = (ChessPiece) obj;
        return (p.pieceColor == this.pieceColor &&
                p.type == this.type);
    }
}
