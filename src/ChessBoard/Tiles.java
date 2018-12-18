package ChessBoard;

import comp124graphics.Rectangle;

import java.awt.*;

public class Tiles extends Rectangle {
    private Color tileColor;

    public Tiles(int x, int y, int size) {
        super(x, y, size, size);
    }

    public void setTileColor(Color col) {
        tileColor = col;
    }

    public Color getTileColor() {
        return tileColor;
    }

}
