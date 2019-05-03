package pieces;

import comp124graphics.Image;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.List;

public class Bishop extends Piece {


    public Bishop(String color, String pos, Board board) {
        super(color, pos, "B", board);
        this.value = 3;
        this.type = "B";
        if (color.equals("white")) {
            this.capImageFile = "res/captured/wB.png";
            this.dispImage = new Image(0, 0, "res/images/wB.png");
            this.capImage = new Image(0, 0, "res/captured/wB.png");
        } else {
            this.capImageFile = "res/captured/bB.png";
            this.dispImage = new Image(0, 0, "res/images/bB.png");
            this.capImage = new Image(0, 0, "res/captured/bB.png");
        }
    }


    public boolean isRuleViolation(Move move) {
        double rankDiff = move.getRankDiff();
        double fileDiff = move.getFileDiff();
        double m = rankDiff / fileDiff;
        return !(Math.abs(m) == 1.0);
    }

    @Override
    public List<Move> generateSpecialMoves() {
        return null;
    }

    public void setSpecialMoves(){

    }

    public void useSpecialMove(Move move){

    }
}
