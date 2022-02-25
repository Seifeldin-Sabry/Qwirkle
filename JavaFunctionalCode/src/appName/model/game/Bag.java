package appName.model.game;

import java.util.*;

public class Bag {
    private List<Tile> tileList = new ArrayList<>();

    //Constructor

    public Bag() {
    }

    public Bag(List<Tile> tilesInTheBag) {
        this.tileList = tilesInTheBag;
    }
    //Getters && Setters

    public List<Tile> getTileList() {
        return this.tileList;
    }

    public void setTileList(List<Tile> tileList) {
        this.tileList.addAll(tileList);
    }

    public void removeTilesFromTheBag(int index) {
        this.tileList.remove(index);
    }

    //methods
    //Generate initial tiles list
    public void generateTiles() {
        List<Tile.TileIcon> listPath = Collections.unmodifiableList(Arrays.asList(Tile.TileIcon.values()));
        List<Tile.TileColor> listColor = Collections.unmodifiableList(Arrays.asList(Tile.TileColor.values()));
        List<Tile.TileShape> listShape = Collections.unmodifiableList(Arrays.asList(Tile.TileShape.values()));
        for (int index = 1; index <= 3; index++) {
            for (Tile.TileIcon tileIcon : listPath) {
                this.tileList.add(new Tile(tileIcon.name() + "(" + index + ")", listPath.get(index), listColor.get(index), listShape.get(index)));
            }
        }
    }

    public void playerBagRefresh(List<Tile> currentList, List<Tile> tmpList, boolean remove) {
        if (remove) {
            for (int j = 0; j < tmpList.size(); j++) {
                for (int i = 0; i < currentList.size(); i++) {
                    if (tmpList.get(j).equals(currentList.get(i))) {
                        this.tileList.remove(i);
                        break;
                    }
                }
            }
        } else {
            for (int j = 0; j < tmpList.size(); j++) {
                for (int i = 0; i < currentList.size(); i++) {
                    this.tileList.add(tmpList.get(i));
                }
            }
        }
    }


    @Override
    public String toString() {
        StringBuilder tiles = new StringBuilder("");
        for (Tile tile: getTileList()) {
            tiles.append(tile);
        }
        return String.format("%s\n", tiles);
    }
}
