package qwirkle.view.gamePlayFrame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import qwirkle.model.Tile;

public final class TileNode extends Rectangle {
    private String name;
    private Tile tile;
    private boolean empty;
    private int col = 0;
    private int row = 0;

    //Empty tile
    public TileNode() {
        empty = true;
        setWidth(50);
        setHeight(50);
        setFill(Color.rgb(255, 255, 255, 0.5));
        setStyle("-fx-border-radius: 2;");
        name = String.valueOf(hashCode());
    }
    //Player Tile
    public TileNode(Tile tile, double size) {
        this.tile = tile;
        setWidth(size);
        setHeight(size);
        setFill(tile.getIconImage());
        setStyle("-fx-cursor: CLOSED_HAND;");
        name = tile.toString();
    }

    public Tile getTile() {
        return tile;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean hasTile() {
        return !empty;
    }

    public void savePosition(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }

}
