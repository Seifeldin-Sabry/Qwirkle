package qwirkle.model;

import java.util.ArrayList;

public class Deck {

    private ArrayList<Tile> deck;
    private ArrayList<Tile> currentSwap;
    private static final int HAND_SIZE = 6;

    public Deck() {
        this.deck = new ArrayList<>();
    }

    private void tradePiece(Bag bag, int tileToReplace) {
        bag.getTiles().add(deck.get(tileToReplace));
        deck.remove(tileToReplace);
        deck.add(bag.drawTileFromBag());
    }

    //boolean to return whether it successfully added a piece
    public boolean addToSwap(Move move) {
        Tile tile = move.getTile();
        if (tile != null) {
            currentSwap.add(tile);
            return true;
        }
        return false;
    }

    public boolean trade(Bag bag, ArrayList<Tile> listToSwap) {
        //TODO: implement in bag that if size is less than tiles toTrade, then trade == failure
        if (listToSwap.size() > bag.getTiles().size()) return false;
        // can't make a swap because player can't get the same amount of tiles back
        for (int i = 0; i < listToSwap.size(); i++) {
            if (deck.contains(listToSwap.get(i))) {
                tradePiece(bag, deck.indexOf(listToSwap.get(i)));
            }
        }
        return true;
    }


    public void refill(Bag bag) {
        for (int i = 0; i < HAND_SIZE; i++) {
            deck.add(bag.drawTileFromBag());
        }
    }

    public ArrayList<Tile> getTilesInDeck() {
        return deck;
    }

}
