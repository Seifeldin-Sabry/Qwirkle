package qwirkle.model;

import java.util.ArrayList;


public class Player {
    private String name;
    private ArrayList<Tile> hand;
    private int points = 0;
    private ArrayList<Move> currentMoves;
    private ArrayList<Tile> currentSwap;
    private Bag bag;
    private Board board;

    public Player(String name,  Bag bag, Board grid) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.bag = bag;
        this.board = grid;
        this.currentSwap = new ArrayList<>();
        this.currentMoves = new ArrayList<>();
    }

    private void tradePiece(int indexToReplace){
        bag.getTiles().add(hand.get(indexToReplace));
        hand.remove(indexToReplace);
        hand.add(bag.drawTileFromBag());
    }

    //boolean to return whether it successfully added a piece
    public boolean addToSwap(Move move){
        Tile tile = move.getTile();
        if (tile != null) {
            currentSwap.add(tile);
            return true;
        }
        return false;
    }

    public void trade(ArrayList<Tile> listToSwap){
        for (int i = 0; i< listToSwap.size(); i++) {
            tradePiece(listToSwap.indexOf(i));
        }
    }

    public void fillHand(){
        for (int i = 0; i < 6; i++) {
            hand.add(bag.drawTileFromBag());
        }
        System.out.println(hand);
    }


    public void makeMove(Tile tile, Move.Coordinate coord){
        Move move = new Move(tile, coord);

        if (hand.contains(tile) && board.validMove(move, currentMoves)) {
            board.boardAddMove(move);
            currentMoves.add(move);
            hand.remove(move.getTile());
        }
    }

    public ArrayList<Tile> getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Tile> getCurrentSwap() {
        return currentSwap;
    }

    public void makeMove(Move move) {
        makeMove(move.getTile(), move.getCoordinate());
    }

    public ArrayList<Move> getCurrentMoves() {
        return currentMoves;
    }

    //Resets the array
    public void confirmTurn() {
        currentMoves.clear();
    }

    public Board getBoard() {
        return board;
    }
}
