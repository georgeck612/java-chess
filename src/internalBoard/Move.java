package internalBoard;

import moveTools.Converter;
import pieces.Piece;

public class Move {

    private Piece piece;

    private int[] moveStart;

    private int[] moveEnd;

    public Move(Piece piece, String move) {
        this.piece = piece;
        moveStart = Converter.notationToCoord(piece.getPos());
        moveEnd = Converter.notationToCoord(move);
    }

    public double getRankDiff() {
        return Math.abs(this.getEndRank() - this.getStartRank());
    }

    public double getFileDiff() {
        return Math.abs(this.getEndFile() - this.getStartFile());
    }

    public String getStartPos() {
        return Converter.coordToNotation(moveStart[0], moveStart[1]);
    }

    public String getEndPos() {
        return Converter.coordToNotation(moveEnd[0], moveEnd[1]);
    }

    public Piece getPiece(){
        return piece;
    }

    public int getStartRank(){
        return moveStart[0];
    }

    public int getStartFile(){
        return moveStart[1];
    }

    public int getEndRank(){
        return moveEnd[0];
    }

    public int getEndFile(){
        return moveEnd[1];
    }

    @Override
    public String toString() {
        return piece + " to " + getEndPos();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Move)) {
            return false;
        }

        Move move = (Move) obj;
        return (getStartPos().equals(move.getStartPos()) && getEndPos().equals(move.getEndPos()) && getPiece().equals(move.getPiece()));
    }
}
