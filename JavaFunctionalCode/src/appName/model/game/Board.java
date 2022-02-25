package appName.model.game;

import java.util.ArrayList;
import java.util.List;

public class Board {
    //2D List of played tiles
    private List<List<Tile>> playedTiles;

    //Constructor
    public Board() {
        playedTiles = new ArrayList<>();
        int j = 0;
        boolean row = true;
        for (int i =0; i < getPlayedTiles().size(); i++){
            while (row) {
                for (; j < getPlayedTiles().get(i).size(); j++) {
                this.playedTiles.get(i).add(new Tile());
                }
                row = false;
            }
            this.playedTiles.get(i).add(null);
        }
    }


    //Getters
    public List<List<Tile>> getPlayedTiles() {
        return playedTiles;
    }

    //Setters
    public void setPlayedTiles(Tile playedTile, int[][] position) {

    }

    //Methods
    public boolean validPosition() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder("");
        for (List<Tile> tileRow : playedTiles) {
            for (Tile tile : tileRow) {
                if (tile == null) {
                    txt.append("null ");
                } else {
                    txt.append("not null");
                }
            }
            txt.append("\n");
        }
        return txt.toString();
    }
}

