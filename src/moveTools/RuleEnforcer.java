package moveTools;

import internalBoard.Board;
import internalBoard.Move;
import pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;

public class RuleEnforcer {

    private Board board;

    public RuleEnforcer(Board board) {
        this.board = board;
    }

    /**
     * takes a Collection of moves for a given piece and checks to see if any of those moves would result
     * in the piece putting its own king into check. Removes all check violating moves from the moves set.
     *
     * @param piece a Piece (P, N, B, R, Q, K)
     * @param moves a set of all possible moves for the piece
     */
    public void checkFilter(Piece piece, Collection<Move> moves) {
        Collection<Move> badMoves = new ArrayList<>();
        for (Move move : moves) {
            if (isCheckViolation(piece, move)) {
                badMoves.add(move);
            }
        }
        moves.removeAll(badMoves);
    }

    /**
     * Undoes <code>movePiece</code>, setting the input piece back to the position it was before the move.
     *
     * @param piece       A chess piece whose previous move is to be undone.
     * @param originalPos The input piece's position before it was moved.
     * @param wasCapture  Whether the last move involved a capture or not.
     */
    public void undoMovePiece(Piece piece, String originalPos, boolean wasCapture) {

        int[] movedPos = Converter.notationToCoord(piece.getPos());
        int[] origPos = Converter.notationToCoord(originalPos);
        int movedRank = movedPos[0];
        int movedFile = movedPos[1];
        int originalRank = origPos[0];
        int originalFile = origPos[1];

        piece.setPos(originalPos);

        board.getInternalBoard()[originalRank][originalFile] = piece;

        if (wasCapture) {
            freePrisoner(movedRank, movedFile);
        } else {
            board.getInternalBoard()[movedRank][movedFile] = null;
        }
    }

    /**
     * Puts a recently captured piece back on the board.
     *
     * @param rightfulRank The piece's rank before it was captured.
     * @param rightfulFile The piece's file before it was captured.
     */
    public void freePrisoner(int rightfulRank, int rightfulFile) {
        Piece formerPrisoner = board.getCaptured().pop();
        if (formerPrisoner.getColor().equals("white")) {
            board.getWhitePieces().add(formerPrisoner);
        } else {
            board.getBlackPieces().add(formerPrisoner);
        }
        board.getInternalBoard()[rightfulRank][rightfulFile] = formerPrisoner;
    }

    /**
     * Checks to see if a piece can be taken by another piece of the opposite color.
     *
     * @return <code>true</code> if the input piece can be taken, <code>false</code> if not.
     */
    private boolean isThreatened(Piece piece) {
        if (piece.getColor().equals("white")) {
            for (Piece enemy : board.getBlackPieces()) {
                enemy.generatePossibleMoves();
                for (Move move : enemy.getMoveSet()) {
                    if (move.getEndPos().equals(piece.getPos())) {
                        return true;
                    }
                }
            }
        } else {
            for (Piece enemy : board.getWhitePieces()) {
                enemy.generatePossibleMoves();
                for (Move move : enemy.getMoveSet()) {
                    if (move.getEndPos().equals(piece.getPos())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if the given side is in check.
     *
     * @param side A side, either black or white.
     * @return <code>true</code> if the given side is in check, <code>false</code> if not.
     */
    public boolean inCheck(String side) {
        return side.equals("white") && isThreatened(board.getwK()) || side.equals("black") && isThreatened(board.getbK());
    }

    /**
     * Checks to see if the given move by the given piece will result in its side being in check.
     */
    private boolean isCheckViolation(Piece piece, Move move) {
        boolean checkViolation;
        String originalPos = piece.getPos();
        boolean wasCapture = false;

        if (board.isOccupied(move.getEndPos())) {
            wasCapture = true;
        }

        board.movePiece(piece, move);

        if (piece.getColor().equals("black")) {
            for (Piece piece1 : board.getWhitePieces()) {
                piece1.generatePossibleMoves();
            }
        } else {
            for (Piece piece1 : board.getBlackPieces()) {
                piece1.generatePossibleMoves();
            }
        }

        checkViolation = inCheck(piece.getColor());

        undoMovePiece(piece, originalPos, wasCapture);

        return checkViolation;
    }
}
