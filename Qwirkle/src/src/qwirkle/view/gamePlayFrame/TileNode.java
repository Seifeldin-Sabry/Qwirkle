package src.qwirkle.view.gamePlayFrame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import qwirkle.model.Tile;

public final class TileNode extends Rectangle {
    private String name;
    private Tile tile;
    private boolean empty;

    public TileNode() {
        empty = true;
        setWidth(50);
        setHeight(50);
        setFill(Color.rgb(255, 255, 255, 0.5));
        setStyle("-fx-border-radius: 2;");
        name = String.valueOf(hashCode());
    }

    public TileNode(Tile tile, double width, double height) {
        this.tile = tile;
        setFill(tile.getIconImage());
        setWidth(width);
        setHeight(height);
        setStyle("-fx-cursor: CLOSED_HAND; -fx-border-radius: 2;");
        name = tile + " " + tile.getSeqNo();
    }

    public Tile getTile(){
        return tile;
    }
    public boolean isEmpty(){
        return empty;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }

}
