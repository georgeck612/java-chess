package pieces;

import comp124graphics.Image;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.List;

public class Knight extends Piece {

    public Knight(String color, String pos, Board board) {
        super(color, pos, "N", board);
        this.type = "N";
        this.value = 3;
        if (color.equals("white")) {
            this.capImageFile = "res/captured/wN.png";
            this.dispImage = new Image(0, 0, "res/images/wN.png");
            this.capImage = new Image(0, 0, "res/captured/wN.png");
        } else {
            this.capImageFile = "res/captured/bN.png";
            this.dispImage = new Image(0, 0, "res/images/bN.png");
            this.capImage = new Image(0, 0, "res/captured/bN.png");
        }
    }

    @Override
    public Piece copyPiece(Board board) {
        return new Knight(getColor(), getPos(), board);
    }

    public boolean isRuleViolation(Move move) {
        double rankDiff = move.getRankDiff();
        double fileDiff = move.getFileDiff();
        return ((rankDiff != 2.0 && rankDiff != 1.0) ||
                (fileDiff != 2.0 && fileDiff != 1.0) ||
                (rankDiff == fileDiff)); //a knight's move is always 2 squares in one direction and 1 square in another
    }

    public List<Move> generateSpecialMoves() {
        return null;
    }

    public void setSpecialMoves(){

    }

    public void useSpecialMove(Move move){

    }
}
