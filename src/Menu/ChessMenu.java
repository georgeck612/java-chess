package Menu;

import comp124graphics.GraphicsGroup;
import comp124graphics.GraphicsText;
import internalBoard.Board;
import pieces.Piece;

public class ChessMenu extends GraphicsGroup{

    private Board board;
    private Piece[][] internalBoard;
    private GraphicsText restart, backToGame;
    private boolean isMenu = false;

    public ChessMenu(int maxX, int maxY, Board board){
        super(0, 0);

        this.board = board;
        this.internalBoard = board.getInternalBoard();

        restart = new GraphicsText("RESTART GAME", maxX/2, maxY/2);
        backToGame = new GraphicsText("Back", maxX - 50, maxY - 50);
    }

    /**
     * Adds all of the text to this menu GraphicsGroup and sets isMenu to true
     * (so we can't still play the game while on the menu)
     */
    public void draw(){
        add(restart);
        add(backToGame);
        isMenu = true;
    }

    private void erase(){
        removeAll();
        isMenu = false;
    }

    /**
     * If something is registered as clicked in the CanvasWindow, this tells the program what to do based
     * on what text is clicked (if any)
     * @param x
     * @param y
     */
    public void clicked(double x, double y){
        //If backToGame is clicked, erase the menu and resume the current game
        if(backToGame.equals(getElementAt(x, y))){
            erase();
            isMenu = false;
        }

        //if restart is clicked, erase the menu, reset the internal board, and start that new game
        else if(restart.equals(getElementAt(x, y))){
            erase();
            restartGame();
            isMenu = false;
        }
    }

    /**
     * resets the instance board and internal board for a new game (based off of board class)
     */
    private void restartGame(){
        board.newGame();
        internalBoard = board.getInternalBoard();
    }




    public GraphicsText getRestart() {
        return restart;
    }

    public GraphicsText getBackToGame() {
        return backToGame;
    }

    public boolean isMenu() {
        return isMenu;
    }

    public Piece[][] getInternalBoard() {
        return internalBoard;
    }

    public Board getBoard() {
        return board;
    }
}
