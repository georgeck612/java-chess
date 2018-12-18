package moveTools;

import internalBoard.Board;
import internalBoard.Move;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class SpecialMoveGenerator extends MoveGenerator {

    private RuleEnforcer ruleEnforcer;

    public SpecialMoveGenerator(Board board) {
        super(board);
        this.ruleEnforcer = new RuleEnforcer(this.board);
    }

    private Move getWhiteEnPassant(Piece piece, int[] coord, String side) {
        int newRank = side.equals("white") ? coord[0] - 1 : coord[0] + 1;
        if (coord[1] >= 1 && coord[1] <= 6) {
            if (checkEnPassantLeft(piece)) {
                return new Move(piece, Converter.coordToNotation(newRank, coord[1] - 1));
            } else if (checkEnPassantRight(piece)) {
                return new Move(piece, Converter.coordToNotation(newRank, coord[1] + 1));
            }
        } else if (coord[1] == 0) { //left side of the board
            if (checkEnPassantRight(piece)) {
                return new Move(piece, Converter.coordToNotation(newRank, coord[1] + 1));
            }
        } else if (coord[1] == 7) { //right side of the board
            if (checkEnPassantLeft(piece)) {
                return new Move(piece, Converter.coordToNotation(newRank, coord[1] - 1));
            }
        }
        return null;
    }

    /**
     * Checks to see if a move by a piece is a valid en passant move
     *
     * @param piece
     * @param move
     * @return
     */
    private boolean isValidEnPassant(Piece piece, Move move) {
        String originalPos = piece.getPos();
        int[] moveCoord = Converter.notationToCoord(move.getEndPos());
        int capturedRank = piece.getColor().equals("white") ? moveCoord[0] + 1 : moveCoord[0] - 1;
        piece.useSpecialMove(move);
        boolean failed = ruleEnforcer.inCheck(piece.getColor());
        ruleEnforcer.undoMovePiece(piece, originalPos, false);
        ruleEnforcer.freePrisoner(capturedRank, moveCoord[1]);
        return !failed;
    }

    /**
     * gets all possible en passant squares for the given piece
     *
     * @param piece
     * @return a string with a possible en passant move or null if no move is possible
     */
    public Move getEnPassantSquares(Piece piece) {
        Move move = null;
        int[] coord = Converter.notationToCoord(piece.getPos());
        if (piece.getColor().equals("white") && coord[0] == 3) {
            move = getWhiteEnPassant(piece, coord, "white");
        } else if (piece.getColor().equals("black") && coord[0] == 4) {
            move = getWhiteEnPassant(piece, coord, "black");
        }
        if (move != null && isValidEnPassant(piece, move)) {
            return move;
        } else {
            return null;
        }
    }

    /**
     * checks to see if en passant is possible to the left of the given piece
     */
    private boolean checkEnPassantLeft(Piece piece) {
        int[] coord = Converter.notationToCoord(piece.getPos());
        String leftSquare = Converter.coordToNotation(coord[0], coord[1] - 1);
        return board.isOccupied(leftSquare) && board.getInternalBoard()[coord[0]][coord[1] - 1].getType().equals("P") &&
                board.getInternalBoard()[coord[0]][coord[1] - 1].getLastMove() == board.getNumMoves() - 1;
    }

    /**
     * Checks en passant is possible to the right of the given piece
     *
     * @param piece
     * @return
     */
    private boolean checkEnPassantRight(Piece piece) {
        int[] coord = Converter.notationToCoord(piece.getPos());
        String rightSquare = Converter.coordToNotation(coord[0], coord[1] + 1);
        return board.isOccupied(rightSquare) && board.getInternalBoard()[coord[0]][coord[1] + 1].getType().equals("P") &&
                board.getInternalBoard()[coord[0]][coord[1] + 1].getLastMove() == board.getNumMoves() - 1;
    }

    /**
     * checks squares inbetween the white king and rook to see if castling to that side is legal for white
     *
     * @param squareList
     * @return true if can castle, false is not
     */
    private boolean checkWhiteCastlingSquares(String[] squareList) {
        for (String tile : squareList) {
            for (Piece piece : board.getBlackPieces()) {
                for (Move move : piece.getMoveSet()) {
                    if (board.isOccupied(tile) || move.getEndPos().equals(tile)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * checks squares inbetween the black king and rook to see if castling to that side is legal for black
     *
     * @param squareList
     * @return true if can castle, false is not
     */
    private boolean checkBlackCastlingSquares(String[] squareList) {
        for (String tile : squareList) {
            for (Piece piece : board.getWhitePieces()) {
                for (Move move : piece.getMoveSet()) {
                    if (board.isOccupied(tile) || move.getEndPos().equals(tile)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if the king from the input side can castle kingside (O-O).
     */
    private boolean canCastleKingside(String side) {
        if (!ruleEnforcer.inCheck(side)) {
            if (side.equals("white")) {
                if (board.getwK().getMoveCount() == 0 && board.getwR2().getMoveCount() == 0) {
                    String[] moves = {"f1", "g1"};
                    return checkWhiteCastlingSquares(moves);
                }
            } else {
                if (board.getbK().getMoveCount() == 0 && board.getbR2().getMoveCount() == 0) {
                    String[] moves = {"f8", "g8"};
                    return checkBlackCastlingSquares(moves);
                }
            }
        }

        return false;
    }

    /**
     * Checks to see if the king from the input side can castle queenside (O-O-O).
     */
    private boolean canCastleQueenside(String side) {
        if (!ruleEnforcer.inCheck(side)) {
            if (side.equals("white")) {
                if (board.getwK().getMoveCount() == 0 && board.getwR1().getMoveCount() == 0) {
                    String[] moves = {"d1", "c1"};
                    return checkWhiteCastlingSquares(moves);
                }
            } else {
                if (board.getbK().getMoveCount() == 0 && board.getbR1().getMoveCount() == 0) {
                    String[] moves = {"d8", "c8"};
                    return checkBlackCastlingSquares(moves);
                }
            }
        }
        return false;
    }


    /**
     * Gets all legal castling moves for a given king.
     *
     * @param king A king.
     * @return A <code>List</code> of legal castling moves.
     */
    public List<Move> getCastlingMoves(Piece king) {
        List<Move> moves = new ArrayList<>();

        if (canCastleKingside(king.getColor())) {
            if (king.getColor().equals("white"))
                moves.add(new Move(king, "g1"));
            else
                moves.add(new Move(king, "g8"));
        }
        if (canCastleQueenside(king.getColor())) {
            if (king.getColor().equals("white"))
                moves.add(new Move(king, "c1"));
            else
                moves.add(new Move(king, "c8"));
        }

        return moves;
    }
}
