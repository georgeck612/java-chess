package pieces;

import comp124graphics.Image;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.List;

public class Rook extends Piece {

    public Rook(String color, String pos, Board board) {
        super(color, pos, "R", board);
        this.type = "R";
        this.value = 5;

        if (color.equals("white")) {
            this.capImageFile = "res/captured/wR.png";
            this.dispImage = new Image(0, 0, "res/images/wR.png");
            this.capImage = new Image(0, 0, "res/captured/wR.png");
        } else {
            this.capImageFile = "res/captured/bR.png";
            this.dispImage = new Image(0, 0, "res/images/bR.png");
            this.capImage = new Image(0, 0, "res/captured/bR.png");
        }
    }

    public boolean isRuleViolation(Move move) {
        double rankDiff = move.getRankDiff();
        double fileDiff = move.getFileDiff();
        double m = rankDiff / fileDiff;
        return !(m == 0.0 || m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY);
    }

    @Override
    public List<Move> generateSpecialMoves() {
        return null;
    }

    public void setSpecialMoves(){

    }

    public void useSpecialMove(Move move) {
    }
}
