package externalBoard;

import comp124graphics.CanvasWindow;
import comp124graphics.Ellipse;
import comp124graphics.GraphicsText;
import comp124graphics.Image;

import ChessBoard.*;
import Menu.ChessMenu;
import internalBoard.Board;
import internalBoard.Move;
import moveTools.Converter;
import moveTools.RuleEnforcer;
import pieces.Piece;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;


public class BoardSetup extends CanvasWindow implements MouseListener, MouseMotionListener {
    public Board board;
    private ChessBoard gameBoard; //The GUI board
    private Piece sPiece = null; //The current highlighted piece on the GUI board
    private int sPX, sPY, capDispSpace = 12; //sPX and sPY are the sPiece x and y spots in the internal board
    private GraphicsText menuText, checkmateText, tieText;
    private ChessMenu menu;
    private boolean moved = false, gameOver = false; //If a new move has just been played or not
    private Piece[][] internalBoard;
    private Collection<Image> promoPieces = new ArrayList<>(4);
    private List<String> blackMoves, whiteMoves;
    private RuleEnforcer ruler;

    //ArrayList of the show moves dots that highlight what possible squares you can move to
    private Collection<Ellipse> dots = new ArrayList<>();

    //Piece alignment constants
    private final int BOARD_SIZE = 80, ALIGN_PIECE = 8, SHIFT_BOARD_RIGHT = 100, SHIFT_BOARD_DOWN = 50;

    //Dot color (changes depending on turn)
    private final Color[] dotColor = {Color.YELLOW, Color.BLACK};

    //If a piece has been promoted
    private Piece promoPiece;


    public BoardSetup(String title, int width, int height) {
        super(title, width, height);
        board = new Board();
        gameBoard = new ChessBoard(BOARD_SIZE, SHIFT_BOARD_RIGHT, SHIFT_BOARD_DOWN, board);
        internalBoard = board.getInternalBoard();

        ruler = new RuleEnforcer(board);

        menu = new ChessMenu(width, height, board);
        menuText = new GraphicsText("Menu", width - 50, height - 50);

        checkmateText = new GraphicsText("Checkmate", width - 100, height/2);
        tieText =  new GraphicsText("Draw", width - 100, height/2);

        blackMoves = board.getBlackMoves();
        whiteMoves = board.getWhiteMoves();

        System.out.println(board.getLastFen());

        addMouseListener(this);
        addMouseMotionListener(this);
        redraw();
    }

    public BoardSetup(){
        this("Chess", 1000, 800);
    }



    /**
     * redraws the board visually
     */
    private void redraw() {
        removeAll();

        add(gameBoard); //Adds the chess board
        add(menuText); //Adds the menu button

        showCaps(); //Shows captured pieces
        ifPromoted(); //Checks for possible promotion
        redrawPieces(); //DrawsUI based off of position in internalBoard

        sPiece = null;
        dots.clear();


        if(board.isGameOver()) {
            gameOver = true;
            if(ruler.inCheck(board.getTurn()))
                add(checkmateText);
            else
                add(tieText);
        }

        //prints current turn-color and move
        if (moved) {
            System.out.println(board.getLastFen());
            moved = false;
        }

    }



    /**
     * prints out last played move based off of current turn
     * @return String of the last move. returns ("") if no move.
     */
    private String printCurrentMove() {
        String m = "";
        if (promoPiece == null) {
            if (board.getTurn().equals("black")) {
                m = whiteMoves.get(whiteMoves.size() - 1);
                System.out.println("white: " + m);
            } else if (board.getTurn().equals("white") && whiteMoves.size() > 0) {
                m = blackMoves.get(blackMoves.size() - 1);
                System.out.println("black: " + m);
            }
        }
        moved = false;
        return m;
    }

    /**
     * Prints the moves of the game up to current state
     */
    private void printMoves(String col) {
        if (col.equals("white")) {
            int space = 7 - whiteMoves.get(whiteMoves.size() - 1).length();
            String spaces = "";
            for(int i = 0; i < space; i ++){
                spaces += " ";
            }
            System.out.print(spaces + blackMoves.get(blackMoves.size() - 1));
        } else {
            System.out.println();
            if(whiteMoves.size() < 10)
                System.out.print("0");
            System.out.print(whiteMoves.size() + ". " + whiteMoves.get(whiteMoves.size() - 1));
        }
        moved = false;
    }



    /**
     * Draws pieces onto the canvas based off of position in internalBoard
     */
    private void redrawPieces() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (internalBoard[row][col] != null) {
                    //Aligns the pieces properly
                    int y_ = row * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN;
                    int x_ = col * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_RIGHT;

                    add(internalBoard[row][col].getDispImage(), x_, y_);
                }
            }
        }
    }

    /**
     * shows captured pieces. Pawns are aligned next to each other and all other pieces are ligned next to each other
     * directly below the pawns of the same color
     */
    private void showCaps() {
        Deque<Piece> caps = board.getCaptured();
        int x = 8 * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_RIGHT;
        int y = 200;

        //Shows all captured pawns
        int bSpace = capDispSpace;
        int wSpace = capDispSpace;
        for (Piece p : caps) {
            if (p.getType().equals("P")) {
                if(p.getColor().equals("white")) {
                    wSpace = displayCapPiece(p, x, y, wSpace, 50, "white");
                }
                else{
                    bSpace = displayCapPiece(p, x, y, bSpace, 50, "black");
                }
            }
        }

        //Shows all captured non-pawns
        wSpace = capDispSpace;
        bSpace = capDispSpace;
        for (Piece p : caps) {
            if (!p.getType().equals("P")) {
                if(p.getColor().equals("white")) {
                    wSpace = displayCapPiece(p, x, y, wSpace, 0, "white");
                }
                else{
                    bSpace = displayCapPiece(p, x, y, bSpace, 0, "black");
                }
            }
        }
    }

    /**
     * Displays the given captured piece
     * @param p The captured piece
     * @param x x-coord on canvas
     * @param y y-coord on canvas
     * @param space how much space to in between the piece and the side of the GUI board
     * @param shiftY how much the piece needs to shift down on the canvas
     * @param color oops
     * @return
     */
    private int displayCapPiece(Piece p, int x, int y, int space, int shiftY, String color){
        if (color.equals("white")) {
            add(p.getCapImage(), x + space, y - shiftY);
            space += capDispSpace;
        } else {
            add(p.getCapImage(), x + space, getHeight() - y - shiftY);
            space += capDispSpace;
        }

        return space;
    }






    /**
     * Shows the legal moves by the piece in the given rank and file
     *
     * @param row
     * @param col
     */
    private void showMoveSquares(int row, int col) {

        clearDots();

        Piece piece = internalBoard[row][col];
        if (piece != null && internalBoard[row][col].getColor().equals(board.getTurn())) {
            List<Move> moves = piece.getMoveSet();
            moves.addAll(piece.getSpecialMoveSet());
            Color dotCol = selectDotColor(piece);

            addDots(moves, dotCol);

        }
    }

    /**
     * adds a dot to the canvas on each tile in a list of moves to show where a given piece can go
     * @param moves list of available moves for a piece
     * @param dotCol color of all of the dots
     */
    private void addDots(Iterable<Move> moves, Color dotCol) {
        for (Move move : moves) {
            int[] coords = Converter.notationToCoord(move.getEndPos());
            int x = coords[1] * BOARD_SIZE + SHIFT_BOARD_RIGHT;
            int y = coords[0] * BOARD_SIZE + SHIFT_BOARD_DOWN;
            Ellipse dot = new Ellipse(x + BOARD_SIZE / 2 - 7, y + BOARD_SIZE / 2 - 7, 14, 14);
            dot.setFillColor(dotCol);
            dot.setFilled(true);
            add(dot);
            dots.add(dot);
        }
    }

    /**
     * Removes every item in dots from the canvas and clears the ArrayList dots.
     */
    private void clearDots() {
        if (!dots.isEmpty()) {
            for (Ellipse dot : dots)
                remove(dot);
            dots.clear();
        }
    }

    /**
     * selects the dot color based off of turn
     * @param piece
     * @return
     */
    private Color selectDotColor(Piece piece) {
        if (piece.getColor().equals("white"))
            return dotColor[0];
        else
            return dotColor[1];

    }




    /**
     * If variable sPiece is able to be promoted, sets up the interface options for promotion and disallows all other
     * actions until a piece is selected for promotion
     */
    private void ifPromoted() {
        try {
            if (sPiece.getType().equals("P") && (Converter.notationToCoord(sPiece.getPos())[0] == 0 || Converter.notationToCoord(sPiece.getPos())[0] == 7)) {
                promoPieces.clear();
                promoPiece = sPiece;
                promoPieces.add(new Image(ALIGN_PIECE, 2 * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN, "res/promoImages/Queen.png"));
                promoPieces.add(new Image(ALIGN_PIECE, 3 * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN, "res/promoImages/Rook.png"));
                promoPieces.add(new Image(ALIGN_PIECE, 4 * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN, "res/PromoImages/Bishop.png"));
                promoPieces.add(new Image(ALIGN_PIECE, 5 * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN, "res/PromoImages/Knight.png"));

                for (Image i : promoPieces) {
                    add(i);
                }

            }
        } catch (NullPointerException e) {
            //shrug
        }
    }

    /**
     * promotes a pawn on the back rank to the selected piece
     *
     * @param y
     */
    private void promote(int x, int y) {
        if (x == 0) {
            switch (y) {
                case 2:
                    board.promote(promoPiece, "Q");
                    break;
                case 3:
                    board.promote(promoPiece, "R");
                    break;
                case 4:
                    board.promote(promoPiece, "B");
                    break;
                case 5:
                    board.promote(promoPiece, "N");
                    break;
            }
            promoPiece = null;

            moved = true;
            redraw();
        }
    }





    /**
     * what to do if a menu button has been clicked
     * @param x mouse x-coord
     * @param y mouse y-coord
     */
    private void checkMenuTabs(double x, double y) {
        if (menu.getBackToGame().equals(menu.getElementAt(x, y))) {
            menu.clicked(x, y);
            redraw();
        } else if (menuText.equals(getElementAt(x, y))) {
            openMenu();
        } else if (menu.getRestart().equals(menu.getElementAt(x, y))) {
            menu.clicked(x, y);
            board = menu.getBoard();
            internalBoard = menu.getInternalBoard();
            gameBoard.setInternalBoard(internalBoard);
            gameOver = false;
            redraw();
        }
    }

    /**
     * Moves sPiece to a given move on the GUI board by updating the internalBoard. Also switches turn color and
     * sets moved to be true (because a piece has moved)
     * @param move
     */
    private void movePiece(Move move) {
        board.move(sPiece, move);
        moved = true;
    }

    /**
     * opens the menu screen
     */
    private void openMenu(){
        removeAll();
        menu.draw();
        add(menu);
    }



    @Override
    public void mousePressed(MouseEvent e) {
        double x_ = e.getX();
        double y_ = e.getY();
        int x = (int) ((x_ - SHIFT_BOARD_RIGHT - ALIGN_PIECE) / BOARD_SIZE);
        int y = (int) ((y_ - SHIFT_BOARD_DOWN - ALIGN_PIECE) / BOARD_SIZE);


        if (x < 8 && y < 8 && x >= 0 && y >= 0 && !menu.isMenu() && !gameOver) { //If the click is within the bounds of the board
            try {
                if (internalBoard[y][x].getColor().equals(board.getTurn())) { //If the chosen piece is the correct color
                    sPiece = internalBoard[y][x]; //sets sPiece

                    showMoveSquares(y, x); //Shows possible moves from the tile internalBoard[y][x]

                    remove(sPiece.getDispImage());
                    add(sPiece.getDispImage());
                    sPX = x;
                    sPY = y;

                }
            } catch (NullPointerException error) {
                sPiece = null;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double x_ = e.getX();
        double y_ = e.getY();
        int x = (int) ((x_ - SHIFT_BOARD_RIGHT - ALIGN_PIECE) / BOARD_SIZE);
        int y = (int) ((y_ - SHIFT_BOARD_DOWN - ALIGN_PIECE) / BOARD_SIZE);


        if (!menu.isMenu() && !gameOver) { //Checks to see if we are in a menu screen
            if (promoPiece == null) { //Checks to see if we need to select a piece to promote a pawn to before continuing
                if (x < 8 && y < 8 && x >= 0 && y >= 0) { //Checks to see if a click is within the bounds oof GUI board

                    if (sPiece != null) {
                        Move move = new Move(sPiece, Converter.coordToNotation(y, x));
                        //If the piece can legally move to given square, move the piece to that square
                        if (!sPiece.getPos().equals(move.getEndPos()) && (sPiece.getSpecialMoveSet().contains(move) || sPiece.getMoveSet().contains(move))) {
                            movePiece(move);
                        }
                    }
                }
                //If mouseRemoved outside of board bouds, reset piece to original position,  reset sPiece and deselect tile.
                else if (sPiece != null) {
                    double resetX = sPX * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_RIGHT;
                    double resetY = sPY * BOARD_SIZE + ALIGN_PIECE + SHIFT_BOARD_DOWN;
                    sPiece.getDispImage().setPosition(resetX, resetY);
                    sPiece = null;
                    gameBoard.deselectTile();
                }
                redraw();
            } else {

                //If a pawn needs to be promoted and click occurs in correct spot, promote sPiece (Pawn) to selected piece
                if (x == 0 && (y < 6 && y > 1)) {
                    promote(x, y);
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double x_ = e.getX();
        double y_ = e.getY();
        int x = (int) ((x_ - SHIFT_BOARD_RIGHT) / BOARD_SIZE);
        int y = (int) ((y_ - SHIFT_BOARD_DOWN) / BOARD_SIZE);


        //Makes sPiece draggable over board
        if (x < 8 && y < 8 && x >= 0 && y >= 0 && !menu.isMenu() && !gameOver) {
            if (sPiece != null) {
                sPiece.getDispImage().setPosition(x_ - 32, y_ - 32); //centers image on mouse
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = ((e.getX() - SHIFT_BOARD_RIGHT - ALIGN_PIECE) / BOARD_SIZE);
        int y = ((e.getY() - SHIFT_BOARD_DOWN - ALIGN_PIECE) / BOARD_SIZE);
        if (x < 8 && y < 8 && x >= 0 && y >= 0 && !menu.isMenu() && !gameOver) {
            gameBoard.selectTile(x, y);
        }else {
            checkMenuTabs(e.getX(), e.getY()); //Checks to see if a menu button has been clicked (any menu button)
        }


    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = ((e.getX() - SHIFT_BOARD_RIGHT) / BOARD_SIZE);
        int y = ((e.getY() - SHIFT_BOARD_DOWN) / BOARD_SIZE);

        if (x < 8 && y < 8 && x >= 0 && y >= 0 && !menu.isMenu() && !gameOver) {
            gameBoard.selectTile(x, y);
            showMoveSquares(y, x); //If hovering over a piece, shows possible moves
        }
    }


    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }






    private ArrayList<Character> initilaizeLetters() {

        ArrayList<Character> letters = new ArrayList<>();
        letters.add('a');
        letters.add('b');
        letters.add('c');
        letters.add('d');
        letters.add('e');
        letters.add('f');
        letters.add('g');
        letters.add('h');

        return letters;

    }

    private void inputToMove(String side) {
//        Scanner scan = new Scanner(System.in);
//        ArrayList<Character> letters = initilaizeLetters();
//
//        System.out.println("type in a move for " + side);
//        String fullMove = scan.next(), usableMove = "";
//
//        if (fullMove.length() == 2) {
//            usableMove = "P" + fullMove;
//        }
//
//        if (fullMove.equals("0-0")) {
//            if (side.equals("white")) {
//                fullMove = "Kg1";
//            } else {
//                fullMove = "Kg8";
//            }
//        }
//        if (fullMove.equals("0-0-0")) {
//            if (side.equals("white")) {
//                fullMove = "Kc1";
//            } else {
//                fullMove = "Kc8";
//            }
//        }
//
//        Piece[][] iBoard = internalBoard;
//        char[] move = fullMove.toCharArray();
//        for (int i = 0; i < move.length; i++) {
//            if (move[i] != 'x') {
//                usableMove += move[i];
//            }
//        }
//
//        move = usableMove.toCharArray();
//        String pieceType = move[0] + "";
//        String tile = move[1] + "" + move[2];
//        System.out.println(pieceType + " " + tile);
//
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                Piece piece = iBoard[row][col];
//                if (piece != null) {
//                    if (board.getAllMoves().contains(tile)) {
//                        if (piece.getType().equals(pieceType) && piece.getColor().equals(side)) {
//                            board.move(piece, tile);
//
//                            if (side.equals("black")) {
//                                whiteMoves.add(board.getCurrentMove());
//                            } else {
//                                blackMoves.add(board.getCurrentMove());
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (board.isGameOver()) {
//            removeAll();
//            board = new Board();
//        }
//        redraw();
//
//
    }

}
