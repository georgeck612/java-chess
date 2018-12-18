package ChessBoard;

import comp124graphics.GraphicsGroup;
import internalBoard.Board;
import pieces.Piece;

import java.awt.*;

public class ChessBoard extends GraphicsGroup {
    private int boardSize;
    private Board board;
    private Piece[][] internalBoard;
    private Tiles highlighted;
    private Color[] boardColors = {new Color(255, 253, 208), Color.GRAY};
    private Tiles[][] tiles = new Tiles[8][8];


    public ChessBoard(int boardSize, int shiftRight, int shiftDown, Board board) {
        super(shiftRight, shiftDown);
        this.boardSize = boardSize;
        this.board = board;
        this.internalBoard = board.getInternalBoard();
        GUIBoard();
    }

    /**
     * prints out the physical chess board (squares only)
     */
    private void GUIBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = col * boardSize;
                int y = row * boardSize;
                Tiles rect = new Tiles(x, y, boardSize);
                if ((row % 2) == (col % 2)) {
                    rect.setFillColor(boardColors[0]);
                    rect.setTileColor(boardColors[0]);
                } else {
                    rect.setFillColor(boardColors[1]);
                    rect.setTileColor(boardColors[1]);
                }
                rect.setFilled(true);
                this.add(rect);
                tiles[row][col] = rect;
            }
        }
    }

    /**
     * highlights selected tile and un-highlights all other highlighted tiles
     * @param x coord in tiles
     * @param y coord in tiles
     */
    public void selectTile(int x, int y) {
        deselectTile();
        highlightTile(x, y);
    }

    /**
     * un-highlights the currently highlighted tile
     */
    public void deselectTile() {
        if (highlighted != null) {
            highlighted.setStrokeColor(Color.black);
            highlighted.setStrokeWidth(1);
        }
    }

    /**
     * Highlights tile at given location in the Tile 2D array
     * if there is a piece that has legal moves (and it is that turn to move), the tile will highlight yellow
     * otherwise, the tile will highlight red
     */
    private void highlightTile(int x, int y) {
        Tiles rect = tiles[y][x];
        Piece piece = internalBoard[y][x];

        remove(rect);
        add(rect);

        rect.setStrokeWidth(5);
        if(piece == null || (piece.getMoveSet().size() == 0)) {
            rect.setStrokeColor(Color.red);
        }
        else if(!piece.getColor().equals(board.getTurn()))
            rect.setStrokeColor(Color.red);

        else
            rect.setStrokeColor(Color.yellow);
        highlighted = rect;
    }

    public void setInternalBoard(Piece[][] internalBoard) {
        this.internalBoard = internalBoard;
    }
}
