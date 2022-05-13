package qwirkle.view.gamePlayFrame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import qwirkle.model.Tile;
//JavaFX pseudo-node class that extends rectangle and gets painted with the images of the tiles
//It contains the coordinated on the grid too. By default a non-played tiled has x,y 0,0 coordinates till it is placed on the grid
//Tiles have default size 50x50 pixels. The GamePlayPresenter may change their size when the played tiles on the grid exceed
//the grid dimensions (done in the GamePlayPresenter, methods resizeGridContent and gridZoomOut)
public final class TileNode extends Rectangle {
    private String name;
    private Tile tile;
    private boolean empty; //All grey (empty spots) tiles where the player can place a tile before getting validated by the model
    private int col = 0; //GridPane coordinates
    private int row = 0;//GridPane coordinates

    //Empty tile (grey tile on the grid)
    public TileNode(double size) {
        empty = true;
        setWidth(size);
        setHeight(size);
        setFill(Color.rgb(255, 255, 255, 0.5));
        setStyle("-fx-border-radius: 2;");
        name = String.valueOf(hashCode());
    }
    //Player Tile - grid tile (size scalable)
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
