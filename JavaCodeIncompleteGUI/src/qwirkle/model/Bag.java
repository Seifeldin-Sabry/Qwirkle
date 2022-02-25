package qwirkle.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Seifeldin Ismail
 */
public class Bag {
    public static final int SIZE_HAND = 6;
    private final Random random = new Random();
    private List<Tile> tiles;

    /**
     * Initialises all 108 tiles
     * @return: 3 sets of 36 tiles
     */
    public Bag() {
        tiles = new ArrayList<>();
        for (TileIcon c: TileIcon.values()) {
            for (int i = 0; i < 6; i++) {
                tiles.add(new Tile(c));
                tiles.add(new Tile(c));
                tiles.add(new Tile(c));
            }
        }
        //making it more random by shuffling
        shuffleBag();
    }


    private void shuffleBag() {
        Collections.shuffle(this.tiles);
    }



    /**
     * @return beginning six tiles for players to start the game
     */
    public List<Tile> initialHandSetup() {
        List<Tile> tileShuffle = new ArrayList<>();

        for (int i = 0; i < SIZE_HAND; i++) {
            int randomTile = random.nextInt(tiles.size());
            tileShuffle.add(tiles.get(randomTile));
            this.tiles.remove(randomTile);
        }
        shuffleBag();
        return new ArrayList<>(tileShuffle);
    }

    /**
     *
     * @return amount of tiles left
     */
    public int getRemainingTiles(){
        return this.tiles.size();
    }

    /**
     * Takes the list of tiles to trade
     * add the tiles bag to the bag, shuffles bag twice
     *
     * @param tradeList
     * @return: Tiles replacing the ones traded
     */
    public List<Tile> tradeTiles(List<Tile> tradeList) {
        tiles.addAll(tradeList);
        shuffleBag();
        shuffleBag();
        return getTilesFromBag(tradeList.size());
    }

    /**
     * 'Trades' with the size of the<List>Tiles</List>
     * 1. first trade
     * 2.then call this method
     * probability of getting at least 1+ from the tiles that
     * @param tileAmount
     * @return
     */
    private List<Tile> getTilesFromBag(int tileAmount) {
        List<Tile> tilesToGive = new ArrayList<>();
        for (int i = 0; i < tileAmount; i++) {
            int randomTile = random.nextInt(tiles.size());
            tilesToGive.add(tiles.get(randomTile));
            this.tiles.remove(randomTile);
        }
        return tilesToGive;
    }
}