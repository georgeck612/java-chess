package internalBoard;

import moveTools.Converter;
import moveTools.FenNotation;
import moveTools.RuleEnforcer;
import pieces.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Board {
    //TODO: major refactoring esp re: move class
    //TODO: make a way to create a deep copy of a board
    private String turn = "white";
    private List<FenNotation> allFens = new ArrayList<>();
    private Piece[][] internalBoard = new Piece[8][8]; //The board that the external board shows
    private Deque<Piece> captured = new ArrayDeque<>(32); //Holds all captured pieces
    private List<Piece> whitePieces = new ArrayList<>(16); //Contains all non-captured white pieces
    private List<Piece> blackPieces = new ArrayList<>(16); //Contains all non-captured black pieces
    private List<String> blackMoves = new ArrayList<>(), whiteMoves = new ArrayList<>();
    private List<Move> allMoves = new ArrayList<>();
    private RuleEnforcer ruler = new RuleEnforcer(this);


    //  numMove: tracks total number of moves on the board.
//  fiftyMoveTrack: tracks the number of consecutive moves without a capture or a pawn move for a draw situation.
    private int numMoves = 0, fiftyMoveTrack = 0;

    private King wK, bK;
    private Rook wR1, wR2, bR1, bR2;

    /**
     * The rest of the remaining instance variables are used to help track the current move
     */
    private String oldTile, moveTile, movePromoted;
    private Piece movePiece, otherPiece;
    private boolean didTake, didCheck, didCheckmate, didDraw, didCastle, didPromote, otherPieceCan;


    public Board() {
        initializePieces();
        updateMoves();
        setFenNotation();
    }


    /**
     * NOTE: THIS COPY CONSTRUCTOR MAY NOT BE FULLY OPTIMIZED; PRIORITIZING REDUNDANCY UNTIL 100% CERTAIN OF CORRECT FUNCTIONALITY
     */
    public Board(Board board) {
        this.turn = board.turn;
        this.whitePieces = copyWhitePieces(this, board.whitePieces);
        this.blackPieces = copyBlackPieces(this, board.blackPieces);
        this.wK = (King) board.wK.copyPiece(this);
        this.bK = (King) board.bK.copyPiece(this);
        this.wR1 = (Rook) board.wR1.copyPiece(this);
        this.wR2 = (Rook) board.wR2.copyPiece(this);
        this.bR1 = (Rook) board.bR1.copyPiece(this);
        this.bR2 = (Rook) board.bR2.copyPiece(this);
        this.internalBoard = copyInternalBoard(this);
        this.captured = copyCaptured(this, board.captured);
        this.whiteMoves = new ArrayList<>(board.whiteMoves);
        this.blackMoves = new ArrayList<>(board.blackMoves);
        this.allMoves = copyMoves(this, board.allMoves);
        this.ruler = new RuleEnforcer(this);
        this.numMoves = board.numMoves;
        this.fiftyMoveTrack = board.fiftyMoveTrack;
        this.oldTile = board.oldTile;
        this.moveTile = board.moveTile;
        this.movePromoted = board.movePromoted;
        if (board.movePiece != null) {
            this.movePiece = board.movePiece.copyPiece(this);
        }
        if (board.otherPiece != null) {
            this.otherPiece = board.otherPiece.copyPiece(this);
        }
        this.didTake = board.didTake;
        this.didCheck = board.didCheck;
        this.didCheckmate = board.didCheckmate;
        this.didDraw = board.didDraw;
        this.didCastle = board.didCastle;
        this.didPromote = board.didPromote;
        this.otherPieceCan = board.otherPieceCan;
        this.allFens = copyFens(this, board.allFens);
        this.updateMoves();
    }

    private List<Move> copyMoves(Board board, List<Move> moves) {
        List<Move> result = new ArrayList<>();
        for (Move move : allMoves) {
            result.add(new Move(move.getPiece().copyPiece(board), move.getEndPos()));
        }
        return result;
    }

    private Piece[][] copyInternalBoard(Board board) {
        Piece[][] result = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (internalBoard[i][j] != null) {
                    result[i][j] = internalBoard[i][j].copyPiece(board);
                }
            }
        }
        return result;
    }

    private Deque<Piece> copyCaptured(Board board, Deque<Piece> cap) {
        Deque<Piece> result = new ArrayDeque<>();
        Deque<Piece> temp = new ArrayDeque<>();
        for (Piece piece : cap) {
            temp.push(piece.copyPiece(board));
        }
        for (Piece piece : temp) { //preserves original order
            result.push(piece);
        }
        return result;
    }

    private List<FenNotation> copyFens(Board board, List<FenNotation> fens) {
        List<FenNotation> result = new ArrayList<>();
        for (FenNotation fenNotation : fens) {
            result.add(new FenNotation(fenNotation, board));
        }
        return result;
    }

    private List<Piece> copyWhitePieces(Board board, List<Piece> pieces) {
        List<Piece> result = new ArrayList<>();
        for (Piece piece : pieces) {
            result.add(piece.copyPiece(board));
        }
        return result;
    }

    private List<Piece> copyBlackPieces(Board board, List<Piece> pieces) {
        List<Piece> result = new ArrayList<>();
        for (Piece piece : pieces) {
            result.add(piece.copyPiece(board));
        }
        return result;
    }

    /**
     * Creates the sets of pieces needed for the game.
     */
    private void createPieces() {
        wK = new King("white", "e1", this);
        bK = new King("black", "e8", this);
        wR1 = new Rook("white", "a1", this);
        wR2 = new Rook("white", "h1", this);
        bR1 = new Rook("black", "a8", this);
        bR2 = new Rook("black", "h8", this);

        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        for (int i = 0; i < 8; i++) {
            whitePieces.add(new Pawn("white", letters[i] + "2", this));
            blackPieces.add(new Pawn("black", letters[i] + "7", this));
        }

        whitePieces.add(wR1);
        whitePieces.add(wR2);
        blackPieces.add(bR1);
        blackPieces.add(bR2);
        whitePieces.add(new Knight("white", "b1", this));
        whitePieces.add(new Knight("white", "g1", this));
        blackPieces.add(new Knight("black", "b8", this));
        blackPieces.add(new Knight("black", "g8", this));
        whitePieces.add(new Bishop("white", "c1", this));
        whitePieces.add(new Bishop("white", "f1", this));
        blackPieces.add(new Bishop("black", "c8", this));
        blackPieces.add(new Bishop("black", "f8", this));
        whitePieces.add(new Queen("white", "d1", this));
        blackPieces.add(new Queen("black", "d8", this));
        whitePieces.add(wK);
        blackPieces.add(bK);
    }

    private void initializePieces() {
        createPieces();

        for (Piece piece : whitePieces) {
            int[] moveCoord = Converter.notationToCoord(piece.getPos());
            internalBoard[moveCoord[0]][moveCoord[1]] = piece;
        }
        for (Piece piece : blackPieces) {
            int[] moveCoord = Converter.notationToCoord(piece.getPos());
            internalBoard[moveCoord[0]][moveCoord[1]] = piece;
        }
    }

    /**
     * Moves a piece to a target square, and adjusts its position and related variables accordingly.
     *
     * @param piece A chess piece.
     * @param move  A target square.
     */
    public void move(Piece piece, Move move) {
        int capSize = captured.size();
        resetCurrentMove(); //resets all of the variables so we don't overlap anything
        oldTile = move.getPiece().getPos();
        //oldTile = piece.getPos();
        checkOtherPieceCan(move.getPiece(), move);


        if (move.getPiece().getSpecialMoveSet().contains(move)) {
            piece.useSpecialMove(move);

            //Checks to see if this move was a castle
            if (piece.getType().equals("K"))
                didCastle = true;

        } else {
            movePiece(move.getPiece(), move);
        }
        move.getPiece().addMoveCount();
        move.getPiece().setLastMove(numMoves);
        numMoves++;
        endTurn();

        updateMoveLists(capSize, move.getPiece(), move);

    }


    /**
     * updates all game move lists after current turn
     *
     * @param capSize
     * @param piece
     * @param move
     */
    private void updateMoveLists(int capSize, Piece piece, Move move) {
        updateCurrentMove(capSize, piece, move);
        if (turn.equals("black"))
            whiteMoves.add(getCurrentMove());
        else
            blackMoves.add(getCurrentMove());

        setFenNotation();
    }


    /**
     * Checks to see if a piece of the same color and type can move to the same spot as the selected piece
     *
     * @param piece
     * @param move
     */
    private void checkOtherPieceCan(Piece piece, Move move) {
        if (piece.getType().equals("N") || piece.getType().equals("B") || piece.getType().equals("R")) {
            if (piece.getColor().equals("white")) {
                for (Piece p : whitePieces) {
                    setOtherPieceandCan(piece, p, move);
                }
            } else {
                for (Piece p : blackPieces) {
                    setOtherPieceandCan(piece, p, move);
                }
            }
        }
    }

    /**
     * sets the instance variables otherPiece and otherPieceCan
     *
     * @param piece
     * @param testPiece
     * @param move
     */
    private void setOtherPieceandCan(Piece piece, Piece testPiece, Move move) {
        if (testPiece.getType().equals(piece.getType()) && !testPiece.equals(piece)) {
            testPiece.generatePossibleMoves();
            otherPieceCan = (testPiece.getMoveSet().contains(move));
            if (otherPieceCan)
                otherPiece = testPiece;
        }
    }

    /**
     * updates all of the variables associated with the current move
     *
     * @param capSize
     * @param piece
     * @param move
     */
    private void updateCurrentMove(int capSize, Piece piece, Move move) {

        //sets movePiece, moveTile, and oldTile for move purposes
        movePiece = piece;
        moveTile = move.getEndPos();

        String rank = move.getEndPos().substring(1, 2);
        if (piece.getType().equals("P") && (rank.equals("1") || rank.equals("8")))
            didPromote = true;


        //Checks to see if a piece has been captured
        if (captured.size() != capSize)
            didTake = true;

        //Checks for check and checkmate
        if (ruler.inCheck(turn)) {
            if (isGameOver())
                didCheckmate = true;
            didCheck = true;
        }
        //Checks for a draw (this is highly unecessary right now but I'm keeping it for now)
        else if (isGameOver()) {
            didDraw = true;
        }

        //Tracks the draw type 50 move rule
        if (!didTake && !piece.getType().equals("P"))
            fiftyMoveTrack++;
        else
            fiftyMoveTrack = 0;
    }


    /**
     * Gets the fen notation for the current board position
     */
    private void setFenNotation() {
        FenNotation fen = new FenNotation(this);
        allFens.add(fen);
    }

    public String getLastFen() {
        return allFens.get(allFens.size() - 1).getFullFen();
    }


    /**
     * Checks to see if a target square on the board is occupied by a piece.
     *
     * @param coord A target square.
     * @return <code>false</code> if this square is empty, <code>true</code> if not.
     */
    public boolean isOccupied(String coord) {
        int[] boardCoord = Converter.notationToCoord(coord);
        return internalBoard[boardCoord[0]][boardCoord[1]] != null;
    }


    /**
     * Updates allMoves to contain all possible legal moves for whoever's turn it is
     */
    private void updateBoardMoveList() {
        allMoves.clear();
        if (turn.equals("white")) {
            for (Piece piece : whitePieces) {
                allMoves.addAll(piece.getMoveSet());
                allMoves.addAll(piece.getSpecialMoveSet());
            }
        } else {
            for (Piece piece : blackPieces) {
                allMoves.addAll(piece.getMoveSet());
                allMoves.addAll(piece.getSpecialMoveSet());
            }
        }
    }

    /**
     * If white's turn, each white piece moveSet becomes all legal moves in current position.
     * Each black piece sets its moveSet to all possible moves in current position
     * If black's turn, each black piece moveSet becomes all legal moves in current position.
     * Each white piece sets its moveSet to all possible moves in current position
     */
    private void updatePieces() {
        if (turn.equals("white")) {
            for (Piece piece : whitePieces) {
                piece.generateLegalMoves();
            }
            for (Piece piece : blackPieces) {
                piece.generatePossibleMoves();
            }
        } else {
            for (Piece piece : blackPieces) {
                piece.generateLegalMoves();
            }
            for (Piece piece : whitePieces) {
                piece.generatePossibleMoves();
            }
        }
    }

    /**
     * Updates the special moves movesSet for each piece in the given turn
     */
    private void updateSpecials() {
        if (turn.equals("white")) {
            for (Piece piece : whitePieces) {
                piece.setSpecialMoves();
            }
        } else {
            for (Piece piece : blackPieces) {
                piece.setSpecialMoves();
            }
        }
    }

    /**
     * Updates all move lists for all pieces in color of given turn
     */
    private void updateAllPieceMoveLists() {
        updatePieces();
        updateSpecials();
    }

    /**
     * updates all piecemoves and updates the board moves for whichever color's turn it is
     */
    private void updateMoves() {
        updateAllPieceMoveLists();
        updateBoardMoveList();
    }

    /**
     * Moves a piece, setting its position to the input move and physically moving it on the board.
     */
    public void movePiece(Piece piece, Move move) {
        int oldRank = move.getStartRank();
        int oldFile = move.getStartFile();
        int destRank = move.getEndRank();
        int destFile = move.getEndFile();

        piece.setPos(move.getEndPos());

        if (internalBoard[destRank][destFile] != null) {
            imprisonPiece(internalBoard[destRank][destFile]);
        }
        internalBoard[destRank][destFile] = piece;
        internalBoard[oldRank][oldFile] = null;
    }

    /**
     * Captures a piece.
     *
     * @param piece The piece to be captured.
     */
    public void imprisonPiece(Piece piece) {
        int[] coord = Converter.notationToCoord(piece.getPos());
        captured.push(piece);
        if (whitePieces.contains(piece)) {
            whitePieces.remove(piece);
        } else {
            blackPieces.remove(piece);
        }
        internalBoard[coord[0]][coord[1]] = null;
    }

    /**
     * Makes a new piece for promotion purposes.
     *
     * @param oldPawn The pawn to be promoted.
     * @param upgrade The type of piece to be promoted to.
     * @return a new chess piece of the desired type with the old pawn's color and position.
     */
    private Piece makePromotedPiece(Piece oldPawn, String upgrade) {

        switch (upgrade) {
            case "Q":
                return new Queen(oldPawn.getColor(), oldPawn.getPos(), this);
            case "R":
                return new Rook(oldPawn.getColor(), oldPawn.getPos(), this);
            case "B":
                return new Bishop(oldPawn.getColor(), oldPawn.getPos(), this);
            case "N":
                return new Knight(oldPawn.getColor(), oldPawn.getPos(), this);
        }
        return null;
    }

    /**
     * Promotes a pawn to a new piece.
     *
     * @param pawn    A pawn.
     * @param upgrade The type of piece to be promoted to.
     */
    public void promote(Piece pawn, String upgrade) {

        Piece promoPiece = makePromotedPiece(pawn, upgrade);

        int[] coord = Converter.notationToCoord(pawn.getPos());

        if (pawn.getColor().equals("white")) {
            whitePieces.remove(pawn);
            whitePieces.add(promoPiece);
        } else {
            blackPieces.remove(pawn);
            blackPieces.add(promoPiece);
        }

        internalBoard[coord[0]][coord[1]] = promoPiece;
        updateAllPieceMoveLists();
    }


    /**
     * Switches the current turn and updates moves for this Board and its pieces.
     */
    private void endTurn() {
        if (turn.equals("white")) {
            turn = "black";
        } else {
            turn = "white";
        }
        updateMoves();
    }

    /**
     * resets all of the instance variables to prepare for a new game
     */
    private void resetAllVariables() {
        turn = "white";
        internalBoard = new Piece[8][8];
        whitePieces.clear();
        blackPieces.clear();
        captured.clear();
        allMoves.clear();
        numMoves = 0;
        fiftyMoveTrack = 0;
        resetCurrentMove();
    }

    /**
     * Erases the whole internal board and initializes an entirely new game
     */
    public void newGame() {
        resetAllVariables();
        initializePieces();
        updateMoves();
    }


    /**
     * checks to see if the game has ended (either a draw or a checkmate)
     *
     * @return boolean
     */
    public boolean isGameOver() {
        updateBoardMoveList();
        boolean allMovesEmpty = allMoves.isEmpty();

        return (allMovesEmpty || fiftyMoveTrack == 100 || isThreeMoveDraw());
    }

    /**
     * checks to see if a three move draw has occurred
     *
     * @return
     */
    private boolean isThreeMoveDraw() {
        String currFen = allFens.get(allFens.size() - 1).getBoardFen();
        int counter = 0;
        try {
            for (int i = 2; i < 15; i++) {
                if (currFen.equals(allFens.get(allFens.size() - i).getBoardFen()))
                    counter++;
                if (counter == 2) {
                    return true;
                }
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException error) {
            return false;
        }
    }


    /**
     * returns the string that is current move just played in full chess notation
     */
    public String getCurrentMove() {
        if (didCastle)
            return getCastleMove();


        if (movePiece.getType().equals("P"))
            return addCheckAndMate(getPawnCurrentMove());
        else
            return addCheckAndMate(getPieceCurrentMove());


    }

    /**
     * checks for check or checkmate and adds the correct character to the end of current move if necessary
     *
     * @param move
     * @return
     */
    private String addCheckAndMate(String move) {
        if (didCheck) {
            if (didCheckmate)
                move += "#";
            else
                move += "+";
        }

        return move;
    }

    /**
     * returns the string of the appropriate castling move
     *
     * @return
     */
    private String getCastleMove() {
        if (moveTile.substring(0, 1).equals("g"))
            return "0-0";
        else if (moveTile.substring(0, 1).equals("c"))
            return "0-0-0";
        else
            return "";
    }

    /**
     * returns the move if the piece is a pawn
     *
     * @return
     */
    private String getPawnCurrentMove() {
        String move = "";

        if (didTake)
            move = oldTile.substring(0, 1) + "x";
        move += moveTile;

        if (didPromote) {
            int[] coord = Converter.notationToCoord(moveTile);
            Piece promPiece = internalBoard[coord[0]][coord[1]];
            movePromoted = promPiece.getType();

            move += movePromoted;
        }

        return move;
    }

    /**
     * calculates current move if the piece is not a pawn
     *
     * @return
     */
    private String getPieceCurrentMove() {

        String move = movePiece.getType();

        if (otherPieceCan) {
            char[] otherPiecePos = otherPiece.getPos().toCharArray();
            char[] thisPiecePos = oldTile.toCharArray();

            if (otherPiecePos[0] == thisPiecePos[0])
                move += thisPiecePos[1];
            else
                move += thisPiecePos[0];
        }

        if (didTake)
            move += "x";
        move += moveTile;

        return move;
    }

    /**
     * resets all variables associated with current move to prepare to annotate the next move
     */
    private void resetCurrentMove() {
        movePiece = null;
        oldTile = null;
        moveTile = null;
        otherPiece = null;
        didTake = false;
        didCheck = false;
        didCheckmate = false;
        didDraw = false;
        didCastle = false;
        didPromote = false;
        otherPieceCan = false;
    }


    public Piece[][] getInternalBoard() {
        return internalBoard;
    }

    public String getTurn() {
        return turn;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public int getFiftyMoveTrack() {
        return fiftyMoveTrack;
    }

    public Deque<Piece> getCaptured() {
        return captured;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public List<String> getBlackMoves() {
        return blackMoves;
    }

    public List<String> getWhiteMoves() {
        return whiteMoves;
    }

    public List<Move> getAllMoves() {
        return allMoves;
    }

    public Rook getwR1() {
        return wR1;
    }

    public Rook getwR2() {
        return wR2;
    }

    public Rook getbR1() {
        return bR1;
    }

    public Rook getbR2() {
        return bR2;
    }

    public King getwK() {
        return wK;
    }

    public King getbK() {
        return bK;
    }

    @Override
    public String toString() {
        for (Piece[] pieces : internalBoard) {
            for (Piece p : pieces) {
                System.out.print(p + " ");
            }
            System.out.println();
        }
        return super.toString();
    }
}

