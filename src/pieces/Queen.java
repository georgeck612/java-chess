package pieces;

import comp124graphics.*;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.List;

public class Queen extends Piece {

    public Queen(String color, String pos, Board board) {
        super(color, pos, "Q", board);
        this.type = "Q";
        if (color.equals("white")) {
            this.capImageFile = "res/captured/wQ.png";
            this.dispImage = new Image(0, 0, "res/images/wQ.png");
            this.capImage = new Image(0, 0, "res/captured/wQ.png");
        } else {
            this.capImageFile = "res/captured/bQ.png";
            this.dispImage = new Image(0, 0, "res/images/bQ.png");
            this.capImage = new Image(0, 0, "res/captured/bQ.png");
        }
    }

    public boolean isRuleViolation(Move move) {
        double rankDiff = move.getRankDiff();
        double fileDiff = move.getFileDiff();
        double m = rankDiff / fileDiff;
        return !(m == 0.0 || m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY || Math.abs(m) == 1.0);
    }

    public List<Move> generateSpecialMoves(){
        return null;
    }

    public void setSpecialMoves(){

    }

    public void useSpecialMove(Move move) {
    }
}
