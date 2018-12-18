package moveTools;

import internalBoard.Board;
import pieces.Piece;

public class FenNotation{
    private Board board;
    private String boardFen, restFen, fullFen;

    public FenNotation(Board board){
        this.board = board;
        fenBoardNotation();
        restOfFen();
        fullFen = boardFen + " " + restFen;
    }



    private void fenBoardNotation(){
        StringBuilder fen = new StringBuilder();

        for (Piece[] pieces : board.getInternalBoard()) {
            Integer emptySquare = 0;
            for (Piece piece : pieces) {
                if (piece == null)
                    emptySquare++;
                else {
                    if (emptySquare > 0)
                        fen.append(emptySquare.toString());

                    if (piece.getColor().equals("white"))
                        fen.append(piece.getType());
                    else
                        fen.append(piece.getType().toLowerCase());

                    emptySquare = 0;
                }
            }
            if (emptySquare > 0)
                fen.append(emptySquare.toString());
            fen.append("/");
        }

        fen.deleteCharAt(fen.toString().length() - 1);
        boardFen = fen.toString();
    }

    private void restOfFen(){
        restFen = board.getTurn().toCharArray()[0] + " " + checkCastleFen() + " " + checkEnPassantFen() + " " + checkHalfAndFullFen();
    }

    private String checkHalfAndFullFen(){
        Integer fifMoves = board.getFiftyMoveTrack();
        Integer halfMoves = board.getNumMoves()/2 + 1;

        return fifMoves.toString() + " " + halfMoves.toString();
    }

    private String checkEnPassantFen() {
        if (board.getTurn().equals("black")) {
            for (Piece piece : board.getWhitePieces())
                if (piece.getType().equals("P") && piece.getPos().substring(1, 2).equals("4") && piece.getMoveCount() == 1 && board.getNumMoves() - 1 == piece.getLastMove()) {
                    int[] coord = Converter.notationToCoord(piece.getPos());
                    return Converter.coordToNotation(5, coord[1]);
                }
        }
        else{
            for (Piece piece : board.getBlackPieces())
                if (piece.getType().equals("P") && piece.getPos().substring(1, 2).equals("5") && piece.getMoveCount() == 1 && board.getNumMoves() - 1 == piece.getLastMove()) {
                    int[] coord = Converter.notationToCoord(piece.getPos());
                    return Converter.coordToNotation(2, coord[1]);
                }
        }
        return "-";
    }

    private String checkCastleFen() {
        StringBuilder fenCheck = new StringBuilder();
        if (checkCastleAvailability("white", "king"))
            fenCheck.append("K");
        if (checkCastleAvailability("white", "queen"))
            fenCheck.append("Q");
        if (checkCastleAvailability("black", "king"))
            fenCheck.append("k");
        if (checkCastleAvailability("black", "queen"))
            fenCheck.append("q");

        if(fenCheck.toString().equals(""))
            fenCheck.append("-");

        return fenCheck.toString();
    }

    private boolean checkCastleAvailability(String side, String kingOrQueen) {
        if (side.equals("white")) {
            if (kingOrQueen.equals("king"))
                return board.getwK().getMoveCount() == 0 && board.getwR2().getMoveCount() == 0;
            else
                return board.getwK().getMoveCount() == 0 && board.getwR1().getMoveCount() == 0;
        } else {
            if (kingOrQueen.equals("queen"))
                return board.getbK().getMoveCount() == 0 && board.getbR2().getMoveCount() == 0;
            else
                return board.getbK().getMoveCount() == 0 && board.getbR1().getMoveCount() == 0;
        }
    }



    public String getBoardFen() {
        return boardFen;
    }

    public String getRestFen() {
        return restFen;
    }

    public String getFullFen() {
        return fullFen;
    }
}
