package qwirkle.model;

import java.util.ArrayList;
import java.util.List;

public class Move {

    private List<Tile> tiles;
    private Type type;

    public enum Type {
        MOVE, CHANGE, SKIP
    }

    /**
     * Constructor
     */
    public Move() {
        this.tiles = new ArrayList<>();
        this.type = Type.MOVE;
    }

   
    public void addTile(Tile t) {
       tiles.add(t);
    }

    public void setType(Type type) {
        this.type = type;
    }


    /**
     * Return the type of the move.
     *
     * @return
     */

    public Type getType() {
        return this.type;
    }

    /**
     * Returns all the tiles in the move.
     *
     * @return Tiles
     */
    public List<Tile> getTiles() {
        return this.tiles;
    }
}