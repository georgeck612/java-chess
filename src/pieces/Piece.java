package pieces;

import internalBoard.Move;
import moveTools.MoveGenerator;
import moveTools.RuleEnforcer;
import comp124graphics.*;
import internalBoard.Board;
import moveTools.SpecialMoveGenerator;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {

    protected List<Move> specialMoveSet = new ArrayList<>();

    private MoveGenerator moveGenerator;
    private RuleEnforcer ruleEnforcer;
    protected SpecialMoveGenerator specialMoveGenerator;

    protected Board board;
    protected int value;
    private final String COLOR;
    protected String capImageFile;
    protected Image dispImage, capImage;
    private int moveCount, lastMove = 0;
    private String pos;
    protected String type;
    private List<Move> moveSet = new ArrayList<>();

    Piece(String color, String pos, String type, Board board) {
        this.board = board;
        this.moveGenerator = new MoveGenerator(this.board);
        this.ruleEnforcer = new RuleEnforcer(this.board);
        this.specialMoveGenerator = new SpecialMoveGenerator(this.board);
        this.pos = pos;
        this.COLOR = color;
        this.type = type;
        this.moveCount = 0;
    }

    /**
     * Defines how a piece moves directionally.
     *
     * @param move a move (such as a6 or f4).
     * @return <code>false</code> if the move is allowed, <code>true</code> otherwise.
     */
    public abstract boolean isRuleViolation(Move move);

    /**
     * Generates all possible moves for this piece, without taking into account check violations
     */
    public void generatePossibleMoves() {
        setMoveSet(moveGenerator.generateMoves(this));
    }

    /**
     * Generates all legal moves for this piece based off of the given board
     */
    public void generateLegalMoves(){
        generatePossibleMoves();
        ruleEnforcer.checkFilter(this, moveSet);
    }

    /**
     * Generates the special moves set for the piece
     * @return
     */
    public abstract List<Move> generateSpecialMoves();

    public abstract void setSpecialMoves();

    /**
     * if the move is in the special move set, the piece moves to the square (move)
     * @param move
     */
    public abstract void useSpecialMove(Move move);

    public void addMoveCount() {
        moveCount++;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getColor() {
        return COLOR;
    }

    public String getType() {
        return type;
    }

    public Image getDispImage() {
        return dispImage;
    }

    public Image getCapImage() {
        return capImage;
    }

    public void setLastMove(int lastMove) {
        this.lastMove = lastMove;
    }

    public int getLastMove() {
        return lastMove;
    }

    public List<Move> getMoveSet() {
        return moveSet;
    }

    public List<Move> getSpecialMoveSet() {
        return specialMoveSet;
    }

    private void setMoveSet(List<Move> moves) {
        this.moveSet = moves;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return COLOR.substring(0, 1) + type + " at " + pos;
    }
}

