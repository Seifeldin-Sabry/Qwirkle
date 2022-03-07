package qwirkle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
    private int amountOfTilesLeft = 108;
    private List<Tile> tiles;
    Random random = new Random();

    public Bag() {
        this.tiles = new ArrayList<>(amountOfTilesLeft);
        for (TileColor c:TileColor.values())
        {
            for (TileShape s:TileShape.values() )
            {
                tiles.add(new Tile(c,s));
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
        return toGet;
    }
}
