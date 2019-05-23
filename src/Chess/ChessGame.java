package Chess;

import evaluation.Evaluator;
import externalBoard.BoardSetup;

public class ChessGame {

    public static void main(String[] args) {
        BoardSetup boardSetup = new BoardSetup();
        Evaluator evaluator = new Evaluator();
        System.out.println("started!");
        long start = System.currentTimeMillis();
        int evaulation = evaluator.miniMax(boardSetup.board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        long timeElapsed = (System.currentTimeMillis() - start) / 1000;
        System.out.println("score with depth 3: " + evaulation + "\nmoves looked at: " + evaluator.count + "\ntime elapsed: " + timeElapsed + " seconds");
    }
}
