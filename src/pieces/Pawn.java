package pieces;

import comp124graphics.Image;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(String color, String pos, Board board) {
        super(color, pos, "P", board);
        this.type = "P";
        if (color.equals("white")) {
            this.capImageFile = "res/captured/wP.png";
            this.dispImage = new Image(0, 0, "res/images/wP.png");
            this.capImage = new Image(0, 0, "res/captured/wP.png");
        } else {
            this.capImageFile = "res/captured/bP.png";
            this.dispImage = new Image(0, 0, "res/images/bP.png");
            this.capImage = new Image(0, 0, "res/captured/bP.png");
        }
    }

    public boolean isRuleViolation(Move move) {
        double destRank = move.getEndRank();
        double startRank = move.getStartRank();
        double rankDiff = move.getRankDiff();
        if (getColor().equals("white")) {
            if (destRank - startRank > 0) {
                return true;
            } else if (startRank == 6) {
                return (rankDiff > 2);
            }
            return (destRank - startRank != -1);
        } else {
            if (destRank - startRank <= 0) {
                return true;
            } else if (startRank == 1) {
                return (rankDiff > 2);
            }
            return destRank - startRank != 1;
        }
    }

    @Override
    public List<Move> generateSpecialMoves() {
        List<Move> moves = new ArrayList<>();
        moves.add(specialMoveGenerator.getEnPassantSquares(this));
        return moves;
    }

    public void setSpecialMoves(){
        specialMoveSet.clear();
        specialMoveSet.addAll(generateSpecialMoves());
        specialMoveSet.removeAll(Collections.singleton(null));
    }

    public void useSpecialMove(Move move) {
        int[] coord = Converter.notationToCoord(move.getEndPos());
        if (getColor().equals("white")) {
            Piece toBeCaptured = board.getInternalBoard()[coord[0] + 1][coord[1]];
            board.movePiece(this, move);
            board.imprisonPiece(toBeCaptured);
        } else {
            Piece toBeCaptured = board.getInternalBoard()[coord[0] - 1][coord[1]];
            board.movePiece(this, move);
            board.imprisonPiece(toBeCaptured);
        }
    }
}
