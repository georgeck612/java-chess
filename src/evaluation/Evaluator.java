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
    public int count = 0;

    public int miniMax(Board board, int depth, int alpha, int beta, boolean maxing) {
        if (depth == 0) {
            return evaluate(board);
        }

        if (maxing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < board.getAllMoves().size(); i++) {
                count++;
                Move move = board.getAllMoves().get(i);
                if (move.getPiece().getColor().equals("white")) {
                    Board board1 = new Board(board);
                    Move move1 = new Move(move.getPiece().copyPiece(board1), move.getEndPos());
                    board1.move(move1.getPiece(), move1);
                    int eval = miniMax(board1, depth - 1, alpha, beta, false);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < board.getAllMoves().size(); i++) {
                count++;
                Move move = board.getAllMoves().get(i);
                if (move.getPiece().getColor().equals("black")) {
                    Board board1 = new Board(board);
                    Move move1 = new Move(move.getPiece().copyPiece(board1), move.getEndPos());
                    board1.move(move1.getPiece(), move1);
                    int eval = miniMax(board1, depth - 1, alpha, beta, true);
                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                }
            }
            return minEval;
        }
    }
}