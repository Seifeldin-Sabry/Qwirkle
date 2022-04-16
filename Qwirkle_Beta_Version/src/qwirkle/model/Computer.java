package qwirkle.model;

import java.util.*;

import static qwirkle.model.Grid.MID;

/**
 * @author Seifeldin Ismail
 */
public class Computer extends Player {
    public enum LevelOfDifficulty {
        EASY, AI
    }

    private LevelOfDifficulty levelOfDifficulty;

    public Computer(Bag bag, Grid grid, LevelOfDifficulty levelOfDifficulty) {
        super("Computer", bag, grid);
        this.levelOfDifficulty = levelOfDifficulty;
    }


    public Move makeMove() {
        Set<Move> allMovesPossible = getBoard().getUsedSpaces();
        Set<Move> allEdges = getBoard().getAllEdges();
        switch (levelOfDifficulty) {
            case EASY -> {
                if (allMovesPossible.isEmpty()) {
                    Random randomTileChooser = new Random();
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

    public LevelOfDifficulty getLevelOfDifficulty() {
        return levelOfDifficulty;
    }
}

