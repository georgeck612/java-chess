package pieces;

import comp124graphics.Image;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;

import java.util.List;

public class King extends Piece {

    public King(String color, String pos, Board board) {
        super(color, pos, "K", board);
        this.type = "K";
        this.value = 1000;
        if (color.equals("white")) {
            this.dispImage = new Image(0, 0, "res/images/wK.png");
            this.capImage = new Image(0, 0, "res/captured/wK.png");
        } else {
            this.dispImage = new Image(0, 0, "res/images/bK.png");
            this.capImage = new Image(0, 0, "res/captured/bK.png");
        }
    }

    public King(King king) {
        super(king.getColor(), king.getPos(), king.type, king.board);
    }

    public boolean isRuleViolation(Move move) {
        double rankDiff = move.getRankDiff();
        double fileDiff = move.getFileDiff();
        double m = rankDiff / fileDiff;
        return !((m == 0.0 || m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY || Math.abs(m) == 1.0) &&
                (rankDiff == 1.0 && fileDiff == 1.0) || (rankDiff + fileDiff == 1.0));
    }

    /**
     * Castles this king on the kingside.
     */
    private void castleKingside() {
        if (this.getColor().equals("white")) {
            board.movePiece(this, new Move(this, "g1"));
            board.movePiece(board.getwR2(), new Move(board.getwR2(), "f1"));
        } else {
            board.movePiece(this, new Move(this, "g8"));
            board.movePiece(board.getbR2(), new Move(board.getbR2(), "f8"));
        }
    }

    /**
     * Castles this king on the queenside.
     */
    private void castleQueenside() {
        if (getColor().equals("white")) {
            board.movePiece(this, new Move(this, "c1"));
            board.movePiece(board.getwR1(), new Move(board.getwR1(), "d1"));
        } else {
            board.movePiece(this, new Move(this, "c8"));
            board.movePiece(board.getbR1(), new Move(board.getbR1(), "d8"));
        }
    }

    public void setSpecialMoves() {
        specialMoveSet.clear();
        specialMoveSet.addAll(generateSpecialMoves());
    }

    public List<Move> generateSpecialMoves() {
        return specialMoveGenerator.getCastlingMoves(this);
    }

    /**
     * Castles this king depending on the move entered.
     *
     * @param move A move - two squares to this king's right or left.
     */
    public void useSpecialMove(Move move) {
        if (move.getEndPos().equals("g1") || move.getEndPos().equals("g8")) {
            castleKingside();
        } else if (move.getEndPos().equals("c1") || move.getEndPos().equals("c8")) {
            castleQueenside();
        }
    }
}
