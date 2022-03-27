package src.qwirkle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
    private int amountOfTilesLeft;
    private final List<Tile> tiles;
    Random random = new Random();

    public Bag() {
        this.tiles = new ArrayList<>(amountOfTilesLeft);
        int counter = 1;
        for (Tile.TileColor c: Tile.TileColor.values())
        {
            for (Tile.TileShape s: Tile.TileShape.values() )
            {
                tiles.add(new Tile(c,s,counter++));
                tiles.add(new Tile(c,s,counter++));
                tiles.add(new Tile(c,s,counter++));
            }
        }
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public Tile drawTileFromBag() {
        int randPosition = random.nextInt(tiles.size());
        Tile toGet = tiles.get(randPosition);
        tiles.remove(randPosition);
        getAmountOfTilesLeft();
        return toGet;
    }
    public int getAmountOfTilesLeft(){
        amountOfTilesLeft = tiles.size();
        return amountOfTilesLeft;
    }
}

