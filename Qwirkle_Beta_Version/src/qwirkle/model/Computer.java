package qwirkle.model;

import java.util.*;

import static qwirkle.model.Grid.MID;

/**
 * @author Seifeldin Ismail
 */
public class Computer extends Player {

    private Random randomTileChooser;
    private LevelOfDifficulty levelOfDifficulty;

    public enum LevelOfDifficulty {
        EASY, AI

    }

    public Computer(Bag bag, Grid grid, LevelOfDifficulty levelOfDifficulty) {
        super("Computer", bag, grid);
        this.levelOfDifficulty = levelOfDifficulty;
        randomTileChooser = new Random();
    }


    public Move makeMove() {
        Set<Move> allMovesPossible = getBoard().getUsedSpaces();
        Set<Move> allEdges = getBoard().getAllEdges();
        switch (levelOfDifficulty) {
            case EASY -> {
                if (allMovesPossible.isEmpty()) {
                    int randomTileInHandIndex = randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
                    Move firstMove = new Move(getDeck().getTilesInDeck().get(randomTileInHandIndex), new Move.Coordinate(MID, MID));
                    return firstMove;
                } else {
                    for (Move move : allEdges) {
                        for (int side = 0; side < move.getCoordinate().getAdjacentCoords().length; side++) {
                            final Move.Coordinate adjacentCoord = move.getCoordinate().getAdjacentCoords()[side];
                            if (getBoard().isEmpty(adjacentCoord.getRow(), adjacentCoord.getColumn())) {
                                for (Tile t : getDeck().getTilesInDeck()) {
                                    Move possibleMove = new Move(t, adjacentCoord);
                                    if (getBoard().isValidMove(possibleMove)) {
                                        return  possibleMove;
                                    }
                                }
                            }
                        }
                    }
                }

            }
            case AI -> {
                //function to make move based on valid moves in the grid
                //if no valid moves, then trade the lowest occuring tile
                // if mutiple low occurences, then pick one at random
                //if there are valid moves, then make a move that is the most profitable
            }

        }
        return null;
    }

    public List<Move> makeMoves() {
        LinkedList<Move> moves = new LinkedList<>();
        Turn turn = new Turn();
        boolean hasMoves = true;
        do {
            Move move = makeMove();
            if (move == null) {
                hasMoves = false;
            } else {
                if (moves.size() == 0) {
                    moves.add(move);
                    getBoard().boardAddMove(move);
                    getDeck().getTilesInDeck().remove(move.getTile());
                    turn.add(move);
                } else {
                    turn.add(move);
                    if (getBoard().isValidMove(turn)) {
                        moves.add(move);
                        getBoard().boardAddMove(move);
                        getDeck().getTilesInDeck().remove(move.getTile());
                    } else {
                        turn.getMoves().removeLast();
                    }
                }
            }
        } while (hasMoves);
        return moves;
    }

    public LevelOfDifficulty getLevelOfDifficulty() {
        return levelOfDifficulty;
    }


    /**
     *
     * @return a Set of tiles with the lowest occurence in the deck to trade
     */
    public void trade() {
        switch (levelOfDifficulty) {
            case EASY -> {//trade a random number of tiles
                tradeRandomNumTiles();
            }
            case AI ->{
                //find the tile that has no color occurence, nor shape occurence
                //if there are multiple tiles with no correlation to others, then trade them all
                ArrayList<Tile> tilesWithSameShape= new ArrayList<>();
                ArrayList<Tile> tilesWithSameColor= new ArrayList<>();
                ArrayList<Tile> tilesWithNoCorr= new ArrayList<>();

                for (Tile t: getDeck().getTilesInDeck()) {
                    for (int i = 0; i < getDeck().getTilesInDeck().size(); i++) {
                        Tile otherTile = getDeck().getTilesInDeck().get(i);
                        if(!t.isSameColor(otherTile) && !t.isSameShape(otherTile)){
                            tilesWithNoCorr.add(t);
                            continue;
                        }
                        if (t.isSameShape(otherTile) && !t.isSameColor(otherTile)) {
                            tilesWithSameShape.add(otherTile);
                            continue;
                        }
                        if (t.isSameColor(otherTile) && !t.isSameShape(otherTile)) {
                            tilesWithSameColor.add(otherTile);
                        }

                    }
                }
                if(tilesWithNoCorr.size() >= tilesWithSameShape.size() || tilesWithNoCorr.size() >= tilesWithSameColor.size()){
                    getDeck().trade(getBag(),tilesWithNoCorr);
                    return;
                }
                else if(tilesWithSameShape.size() > tilesWithSameColor.size()){
                    getDeck().trade(getBag(),tilesWithSameColor);
                    return;
                }
                else if(tilesWithSameShape.size() < tilesWithSameColor.size()){
                    getDeck().trade(getBag(),tilesWithSameShape);
                    return;
                }
                else {//if all tiles have no correlation, then trade a random number of tiles
                    tradeRandomNumTiles();
                }

            }
        }
    }

    private void tradeRandomNumTiles() {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(getDeck().getTilesInDeck());
        int randomTileNumToTrade = randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
        for (int i = 0; i < randomTileNumToTrade; i++) {
            int randomTileIndex = randomTileChooser.nextInt(tilesToTrade.size());
            getDeck().getTilesInDeck().remove(tilesToTrade.get(randomTileIndex));
        }
        getDeck().trade(getBag(),tilesToTrade);
    }

    public HashMap<Move, Set<Set<Move>>> getAllValidMoves() {

        HashMap<Move, Set<Set<Move>>> validMoves = new HashMap<>();
        return validMoves;
    }



    //this works perfectly
    //Next step (this is for the lecturers to read)
    //1. make a move that return a hashmap of the edge Move and a list of combinations that are valid
    //2. after this is completed we can make both the difficulties (easy and hard) based on that method
    //because we can just filter out based on score so easy plays moves that are 4 or less points, hard plays moves that are 5 or more points (example)
    //INPUT: [ORANGE EIGHT_POINT_STAR27, RED CIRCLE77, BLUE SQUARE52, GREEN EIGHT_POINT_STAR81, ORANGE CIRCLE101, RED SQUARE4] :random generated deck for computer
    //OUTPUT: [
    // [GREEN EIGHT_POINT_STAR81, ORANGE EIGHT_POINT_STAR27],
    // [ORANGE CIRCLE101, RED CIRCLE77],
    // [RED SQUARE4, BLUE SQUARE52],
    // [ORANGE CIRCLE101, ORANGE EIGHT_POINT_STAR27],
    // [RED SQUARE4, RED CIRCLE77],
    // [ORANGE EIGHT_POINT_STAR27],
    // [RED CIRCLE77], [BLUE SQUARE52],
    // [GREEN EIGHT_POINT_STAR81],
    // [ORANGE CIRCLE101],
    // [RED SQUARE4]
    // ]
    public Set<Set<Tile>> allDeckTileCombinations() {
        Set<Set<Tile>> allTileCombinations = new HashSet<>();
        Set<Set<Tile>> toReturn = new HashSet<>();
        List<Tile> tilesInDeck = getDeck().getTilesInDeck();
        for (Tile tile : tilesInDeck) {
            Set<Tile> tileCombination = new HashSet<>();

            for (Tile t : tilesInDeck) {
                if(t.equals(tile)) {
                    continue;
                }
                if (t.isSameShape(tile) && !t.isSameColor(tile)) {
                    tileCombination.add(t);
                    tileCombination.add(tile);
                }
            }
            allTileCombinations.add(tileCombination);
        }
        for (Tile tile : tilesInDeck) {
            Set<Tile> tileCombination = new HashSet<>();

            for (Tile t : tilesInDeck) {
                if(t.equals(tile)) {
                    continue;
                }
                if (!t.isSameShape(tile) && t.isSameColor(tile)) {
                    tileCombination.add(t);
                    tileCombination.add(tile);
                }
            }
            allTileCombinations.add(tileCombination);
        }

        for (Set<Tile> list : allTileCombinations) {
            for (int i = 0; i < list.size(); i++) {
                Set<Tile> tiles = new HashSet<>();
                for (int j = i; j < list.size()-i; j++) {
                    tiles.add(list.stream().toList().get(j));
                }
                toReturn.add(tiles);
            }
        }

        toReturn.removeIf(x -> x.size() == 0);
        tilesInDeck.stream().distinct().forEach(x -> toReturn.add(new HashSet<>(List.of(x))));
        return toReturn;
    }


}

