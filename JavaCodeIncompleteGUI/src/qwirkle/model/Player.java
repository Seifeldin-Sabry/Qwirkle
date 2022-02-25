package qwirkle.model;

import java.util.List;


import static qwirkle.model.PlayerColor.RED;

public class Player {

    private final String name;
    private final PlayerColor playerColor;
    private final Grid grid;
    private final Score score;
    private List<Tile> hand;
    private final Bag bag;
    private boolean isPlayingFirst;


    /**
     *  Construct
     * @param name
     * @param color
     * @param grid
     * @param bag
     * @param score
     * @param isPlayingFirst
     */
    Player(String name, PlayerColor color, Grid grid, Bag bag, Score score, boolean isPlayingFirst){
        this.name = name;
        this.playerColor = color;
        this.grid = grid;
        this.bag = bag;
        this.score = score;
        this.isPlayingFirst = isPlayingFirst;
        this.hand = bag.initialHandSetup();
    }

    public Player(String name, Grid grid, Bag bag, Score score, boolean isPlayingFirst){
        this(name, RED, grid, bag, score, isPlayingFirst);
    }




    public String getName() {
        return name;
    }

    public List<Tile> getHand() {
        return hand;
    }

    public void removeFromHand(Tile tile){
        for (Tile tilesInHand:
                hand) {
            if (tilesInHand.equals(tile))
                hand.remove(tilesInHand);
            break;
        }
    }

    public Move determineMove() {
        //not done yet because its very different for the Player
        // ie: only validation of moves/actions is required
        return null;
    }

    public boolean isPlayingFirst() {
        return isPlayingFirst;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", playerColor=" + playerColor +
                ", grid=" + grid +
                ", score=" + score +
                ", hand=" + hand +
                ", bag=" + bag +
                ", isPlayingFirst=" + isPlayingFirst +
                '}';
    }
}
