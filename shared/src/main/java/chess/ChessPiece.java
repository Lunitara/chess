package chess;

import com.google.gson.reflect.TypeToken;

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
        if (piece.getPieceType() == PieceType.KING) {
            return kingMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        return List.of();
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        //moving two up one right the board
        directionUpdater(1, 2, board, myPosition, available);
        //moving two up one left
        directionUpdater(-1, 2, board, myPosition, available);
        //moving two left one right
        directionUpdater(-2, 1, board, myPosition, available);
        //moving two left one left
        directionUpdater(-2, -1, board, myPosition, available);
        //moving two right one left
        directionUpdater(2, -1, board, myPosition, available);
        //moving two right one right
        directionUpdater(2, 1, board, myPosition, available);
        //moving two down one right
        directionUpdater(1, -2, board, myPosition, available);
        //moving two down one left
        directionUpdater(-1, -2, board, myPosition, available);
        return available;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        //moving up the board
        directionUpdater(0, 1, board, myPosition, available);
        //moving right the board
        directionUpdater(1, 0, board, myPosition, available);
        //moving down the board
        directionUpdater(0, -1, board, myPosition, available);
        //moving left the board
        directionUpdater(-1, 0, board, myPosition, available);
        return available;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        //moving up the board
        directionUpdater(0, 1, board, myPosition, available);
        //moving right the board
        directionUpdater(1, 0, board, myPosition, available);
        //moving down the board
        directionUpdater(0, -1, board, myPosition, available);
        //moving left the board
        directionUpdater(-1, 0, board, myPosition, available);
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

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        //moving up the board
        directionUpdater(0, 1, board, myPosition, available);
        //moving right the board
        directionUpdater(1, 0, board, myPosition, available);
        //moving down the board
        directionUpdater(0, -1, board, myPosition, available);
        //moving left the board
        directionUpdater(-1, 0, board, myPosition, available);
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

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> available = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        //check if it's white
        if (piece.pieceColor == ChessGame.TeamColor.WHITE) {
            whitePawnMoves(board, myPosition, available);
        }
        else {
            blackPawnMoves(board, myPosition, available);
        }
        return available;    }

 public void whitePawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
     ChessPiece piece = board.getPiece(myPosition);
     ChessPiece upleft;
     ChessPiece upright;
     if (myPosition.getColumn() -1 <1) {
         upleft = null;
     }
     else {
         upleft = board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1));

     }
     if (myPosition.getColumn() +1 > 8) {
         upright = null;
     }
     else {
         upright = board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1));
     }
     if (myPosition.getRow() == 7) {
         //if it's about to promote
         if (upright != null) {
             if (upright.pieceColor != piece.pieceColor) {
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.QUEEN));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.ROOK));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.BISHOP));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), PieceType.KNIGHT));
             }
         }
         if (upleft != null) {
             if (upleft.pieceColor != piece.pieceColor) {
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.QUEEN));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.ROOK));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.BISHOP));
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1), PieceType.KNIGHT));
             }
         }
         //if there isn't a piece right in front it can move there
         if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() ), PieceType.QUEEN));
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() ), PieceType.ROOK));
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() ), PieceType.BISHOP));
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() ), PieceType.KNIGHT));

         }
     }
     else {
         //if it's the first turn of the pawn it can move twice if nothing obstructs it
         if (myPosition.getRow() == 2 && board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())) == null) {
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()), null));

         }
         //if piece of opposite color is in row+1col+1 or row-1col+1 then it can move there

         if (upright != null && myPosition.getColumn() != 8) {
             if (upright.pieceColor != piece.pieceColor) {
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1), null));
             }
         }
         if (upleft != null && myPosition.getColumn() != 1) {
             if (upleft.pieceColor != piece.pieceColor) {
                 available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1), null));
             }
         }
         //if there isn't a piece right in front ic an move there
         if (board.getPiece(new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn())) == null) {
             available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn()), null));

         }

     }
 }


    public void blackPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece downleft;
        ChessPiece downright;
        if (myPosition.getColumn() -1 <1) {
            downleft = null;
        }
        else {
            downleft = board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() -1));

        }
        if (myPosition.getColumn() +1 > 8) {
            downright = null;
        }
        else {
            downright = board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1));
        }
        if (myPosition.getRow() == 2) {
            //if it's about to promote
            if (downright != null) {
                if (downright.pieceColor != piece.pieceColor) {
                    pawnPromotion(1, -1, board, myPosition, available);

                }
            }
            if (downleft != null) {
                if (downleft.pieceColor != piece.pieceColor) {
                    pawnPromotion(-1, -1, board, myPosition, available);

                }
            }
            //if there isn't a piece right in front i5 can move there
            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
                pawnPromotion(-1, 0, board, myPosition, available);
            }
        }
        else {
            //if it's the first turn of the pawn it can move twice if nothing obstructs it
            if (myPosition.getRow() == 7 && board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null && board.getPiece(new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()), null));

            }
            //if piece of opposite color is in row+1col+1 or row-1col+1 then it can move there

            if (downright != null && myPosition.getColumn() != 8) {
                if (downright.pieceColor != piece.pieceColor) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1), null));
                }
            }
            if (downleft != null && myPosition.getColumn() != 1) {
                if (downleft.pieceColor != piece.pieceColor) {
                    available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() -1, myPosition.getColumn() - 1), null));
                }
            }
            //if there isn't a piece right in front it can move there
            if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
                available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn()), null));

            }

        }
    }
public void pawnPromotion(int col_delta, int row_delta, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> available) {
    int count = 1;
    PieceType[] pieces = {
            PieceType.BISHOP,
            PieceType.ROOK,
            PieceType.QUEEN,
            PieceType.KNIGHT,
    };
    for (PieceType piece : pieces) {
        if (board.getPiece(new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn())) == null) {
            available.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), piece));
        }
    }


}
    public void directionUpdater(int col_delta, int row_delta, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        ChessPiece piece = board.getPiece(myPosition);
        int count = 1;
        if (piece.getPieceType() == PieceType.KING) {
            if (myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    myPosition.getRow() + count * row_delta >= 1) {
                //if it's not out of bounds then...
                if (board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) == null) {
                    //add if the place has a piece of opposite color
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                } else if (board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)).pieceColor != piece.pieceColor) {
                    //add if it doesn't have a piece at all
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                }
            }
        } else if (piece.getPieceType() == PieceType.KNIGHT) {
            if (myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    myPosition.getRow() + count * row_delta >= 1) {
                //if it's not out of bounds then...
                if (board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) == null) {
                    //add if the place has a piece of opposite color
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                } else if (board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)).pieceColor != piece.pieceColor) {
                    //add if it doesn't have a piece at all
                    moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                }
            }
        } else {
            //go through all positions
            while (myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    myPosition.getRow() + count * row_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) == null) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
                count++;
            }
            //check last position
            if (myPosition.getColumn() + count * col_delta <= 8 &&
                    myPosition.getRow() + count * row_delta <= 8 &&
                    myPosition.getColumn() + count * col_delta >= 1 &&
                    myPosition.getRow() + count * row_delta >= 1 &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)) != null &&
                    board.getPiece(new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta)).pieceColor != piece.pieceColor) {
                moves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow() + count * row_delta, myPosition.getColumn() + count * col_delta), null));
            }
        }


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
