package qwirkle.model;

import java.util.ArrayList;

/**
 * @author: Seifeldin Ismail
 */

public class Deck {
    private ArrayList<Tile> deck;

    private static final int HAND_SIZE = 6;

    public Deck() {
        this.deck = new ArrayList<>(HAND_SIZE);
    }

    private void tradePiece(Bag bag, int tileToReplace) {
        bag.getTiles().add(deck.get(tileToReplace));
        deck.remove(tileToReplace);
        deck.add(bag.drawTileFromBag());
    }

    //boolean to return whether it successfully added a piece
    public boolean trade(Bag bag, ArrayList<Tile> listToSwap) {
        // can't make a swap because player can't get the same amount of tiles back
        if (listToSwap.size() > bag.getTiles().size()) return false;
        listToSwap.stream().filter(tile -> deck.contains(tile)).forEach(tile -> tradePiece(bag, deck.indexOf(tile)));
        return true;
    }


    public void refill(Bag bag){
        int deckSize = deck == null ? 0 : deck.size();
        int toRefill = HAND_SIZE - deckSize;
        for (int i = 0; i < toRefill; i++) {
            Tile tile = bag.drawTileFromBag();
            if (tile == null) return;
            deck.add(tile);
        }
    }

    public ArrayList<Tile> getTilesInDeck() {
        return deck;
    }

}