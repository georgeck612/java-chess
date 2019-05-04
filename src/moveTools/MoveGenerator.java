package moveTools;

import internalBoard.Board;
import internalBoard.Move;
import pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {

    protected Board board;

    public MoveGenerator(Board board) {
        this.board = board;
    }

    /**
     * Checks that all the squares between two squares on the same rank are empty.
     *
     * @param common  The common rank or file which the two squares share.
     * @param compare1 The differing position of the first square.
     * @param compare2 The differing position of the second square.
     * @return <code>true</code> if all the squares between the first and second squares are empty, <code>false</code> if not.
     */
    private boolean rankFileCheck(int common, int compare1, int compare2, boolean rankOrFile) {
        int start = (compare1 > compare2 ? compare2 : compare1);
        int end = (compare1 == start ? compare2 : compare1);
        for (int i = start + 1; i < end; i++) {
            if (board.isOccupied(Converter.coordToNotation(common, i)) && rankOrFile || board.isOccupied(Converter.coordToNotation(i, common)) && !rankOrFile) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that all the squares between two squares on a diagonal are empty.
     *
     * @param rank1 The rank of the first square.
     * @param file1 The file of the first square.
     * @param rank2 The rank of the second square.
     * @param file2 The file of the second square
     * @param slope The slope of the line between the squares (1 or -1).
     * @return <code>true</code> if all the squares between the first and second squares are empty, <code>false</code> if not.
     */
    private boolean diagonalCheck(int rank1, int file1, int rank2, int file2, double slope) {
        int rankDiff = rank1 - rank2;
        int destR = (rankDiff > 0 ? rank1 : rank2);
        int destF = (destR == rank1 ? file1 : file2);
        rankDiff = Math.abs(rankDiff);
        if (slope > 0) {
            for (int i = 0; i < rankDiff - 1; i++) {
                if (board.isOccupied(Converter.coordToNotation(destR - 1 - i, destF - 1 - i))) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i = 0; i < rankDiff - 1; i++) {
                if (board.isOccupied(Converter.coordToNotation(destR - 1 - i, destF + 1 + i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Checks that all the squares between two squares on any straight line are empty.
     *
     * @param rank1 The rank of the first square.
     * @param file1 The file of the first square.
     * @param rank2 The rank of the second square.
     * @param file2 The file of the second square
     * @param slope The slope of the line between the squares.
     * @return <code>true</code> if all the squares between the first and second squares are empty, <code>false</code> if not.
     */
    private boolean isClear(int rank1, int file1, int rank2, int file2, double slope) {
        if (Math.abs(slope) == 0) {
            return rankFileCheck(rank2, file1, file2, true);
        } else if (slope == Double.POSITIVE_INFINITY || slope == Double.NEGATIVE_INFINITY) {
            return rankFileCheck(file2, rank1, rank2, false);
        } else if (Math.abs(slope) == 1) {
            return diagonalCheck(rank1, file1, rank2, file2, slope);
        }
        return false;
    }

    /**
     * Checks to see if the given move is legal if a pawn were to take it.
     *
     * @param move     A move on the board (such as a5 or d3).
     * @param fileDiff The difference between the pawn's current file and the move's file.
     * @param slope    The slope of the line between the pawn's position and the move's.
     * @return <code>true</code> if the move is legal, <code>false</code> if not.
     */
    private boolean isLegalPawnMove(Move move, double fileDiff, double slope) {
        if (Math.abs(slope) == 1 && board.isOccupied(move.getEndPos()) && Math.abs(fileDiff) == 1) {
            return true;
        } else if (board.isOccupied(move.getEndPos())) {
            return false;
        }
        return (fileDiff == 0);
    }

    /**
     * Checks to see that a move would not take a piece into a square occupied by a piece of the same color.
     *
     * @param piece    A chess piece.
     * @param destRank The rank of the move.
     * @param destFile The file of the move.
     * @param move     A move on the board (such as a5 or d3).
     * @return <code>true</code> if the target square is occupied by a piece of the same color, <code>false</code> if not.
     */
    private boolean isFriendlyFire(Piece piece, int destRank, int destFile, Move move) {
        return board.isOccupied(move.getEndPos()) && piece.getColor().equals(board.getInternalBoard()[destRank][destFile].getColor());
    }

    /**
     * Checks to see that a move is physically possible within the constraints for the type of piece.
     *
     * @param piece A chess piece.
     * @param move  A move on the board (such as a5 or d3).
     * @return <code>true</code> if the move is impossible for the given piece, <code>false</code> if not.
     */
    private boolean isImpossibleMove(Piece piece, Move move) {
        return move.getEndPos().equals(piece.getPos()) || piece.isRuleViolation(move);
    }

    /**
     * Checks to see if a given move is legal in relation to the other pieces on the board (excluding check).
     *
     * @param piece A <code>Piece</code>.
     * @param move  A target square.
     * @return <code>true</code> if the move is a legal one, <code>false</code> if not.
     */
    private boolean canMove(Piece piece, Move move) {
        int startRank = move.getStartRank();
        int startFile = move.getStartFile();
        int destRank = move.getEndRank();
        int destFile = move.getEndFile();
        double fileDiff = move.getFileDiff();
        double slope = ((double) destRank - startRank) / (destFile - startFile);

        return !isImpossibleMove(piece, move) &&
                !isFriendlyFire(piece, destRank, destFile, move) &&
                (piece.getType().equals("N") || isClear(startRank, startFile, destRank, destFile, slope)) &&
                (!piece.getType().equals("P") || isLegalPawnMove(move, fileDiff, slope));
    }

    /**
     * Generates all moves that a given piece could hypothetically move to (does not take check into account).
     *
     * @param piece A chess piece.
     * @return A <code>List</code> of moves that the piece can do..
     */
    public List<Move> generateMoves(Piece piece) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Move move = new Move(piece, Converter.coordToNotation(row, col));
                if (canMove(piece, move)) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
