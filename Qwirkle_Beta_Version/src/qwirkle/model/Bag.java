package qwirkle.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author: Seifeldin Ismail
 */
public class Bag {
    private int amountOfTilesLeft;
    private final ArrayList<Tile> tiles;
    private final ArrayList<Tile> dbTiles;
    Random random = new Random();

    public Bag() {
        this.tiles = new ArrayList<>();

        int counter = 1;
        for(int i = 0; i < 3; i++) {
            for(int color = 0; color < Tile.TileColor.values().length; color++) {
                for(int shape = 0; shape < Tile.TileShape.values().length; shape++) {
                    Tile tile = new Tile(Tile.TileColor.values()[color],Tile.TileShape.values()[shape],counter++);
                    tiles.add(tile);
                }
            }
        }
        //to save to the database
        dbTiles = new ArrayList<>();
        dbTiles.addAll(tiles);

    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public Tile drawTileFromBag() {
        if (tiles.size() == 0) {
            return null;
        }
        int randPosition = random.nextInt(tiles.size());
        Tile toGet = tiles.get(randPosition);
        tiles.remove(randPosition);
        getAmountOfTilesLeft();
        return toGet;
    }

    public ArrayList<Tile> getDbTiles() {
        return new ArrayList<>(dbTiles);
    }

    public int getAmountOfTilesLeft(){
        amountOfTilesLeft = tiles.size();
        return amountOfTilesLeft;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Tile tile: tiles) {
            sb.append(String.format("Tile color: %s, Tile shape: %s, id: %d\n",tile.getColor(),tile.getShape(),tile.getTile_id()));
        }
        return sb.toString();
    }
}
