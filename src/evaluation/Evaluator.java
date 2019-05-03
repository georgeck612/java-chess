package evaluation;

import internalBoard.*;
import pieces.Piece;

public class Evaluator {

    public int evaluate(Board board) {
        int whiteSum = 0;
        int blackSum = 0;
        for (Piece piece : board.getWhitePieces()) {
            if (!piece.getType().equals("K")) {
                whiteSum += piece.getValue();
            }
        }
        for (Piece piece : board.getBlackPieces()) {
            if (!piece.getType().equals("K")) {
                blackSum += piece.getValue();
            }
        }
        return whiteSum - blackSum;
    }
}