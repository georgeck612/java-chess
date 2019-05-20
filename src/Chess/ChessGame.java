package Chess;

import externalBoard.BoardSetup;
import internalBoard.Board;

public class ChessGame {

    public static void main(String[] args) {
       BoardSetup boardSetup =  new BoardSetup();
        Board board = new Board(boardSetup.board);
        System.out.println("");
    }
}
