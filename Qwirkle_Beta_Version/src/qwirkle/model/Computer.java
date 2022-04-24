package qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public Turn play() {
        if (levelOfDifficulty == LevelOfDifficulty.EASY) {
            return makeMoves();
        } else {
            //AI method once finished
            return AIMove();
        }
    }


    private Move possibleMove() {
        Set<Move> allMovesPossible = getBoard().getUsedSpaces();
        if (allMovesPossible.isEmpty()) {
            int randomTileInHandIndex = randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
            Move firstMove = new Move(getDeck().getTilesInDeck().get(randomTileInHandIndex), new Move.Coordinate(MID, MID));
            return firstMove;
        } else {
            for (Move move : allMovesPossible) {
                for (int side = 0; side < move.getCoordinate().getAdjacentCoords().length; side++) {
                    final Move.Coordinate adjacentCoord = move.getCoordinate().getAdjacentCoords()[side];
                    if (getBoard().isEmpty(adjacentCoord.getRow(), adjacentCoord.getColumn())) {
                        for (Tile t : getDeck().getTilesInDeck()) {
                            Move possibleMove = new Move(t, adjacentCoord);
                            if (getBoard().isValidMove(possibleMove)) {
                                return possibleMove;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Turn makeMoves() {
        LinkedList<Move> movesToReturn = new LinkedList<>();
        Set<Move> allEdges = getBoard().getAllEdges();
        Turn turn = new Turn();
        int i = 0;
        while (i < allEdges.size()) {
            Move possibleMove = possibleMove();
            if (possibleMove == null) {
                return turn;
            } else {
                if (movesToReturn.size() == 0) {
                    movesToReturn.add(possibleMove);
                    getBoard().boardAddMove(possibleMove);
                    getDeck().getTilesInDeck().remove(possibleMove.getTile());
                    turn.add(possibleMove);
                } else {
                    turn.add(possibleMove);
                    if (getBoard().isValidMove(turn)) {
                        movesToReturn.add(possibleMove);
                        getBoard().boardAddMove(possibleMove);
                        getDeck().getTilesInDeck().remove(possibleMove.getTile());
                    } else {
                        turn.getMoves().removeLast();
                    }
                }
            }
            i++;
        }
        return turn;
    }

    private Turn AIMove() {
        Set<Move> usedTiles = getBoard().getUsedSpaces();
        if (usedTiles.isEmpty()) {
            Turn firstTurn = makeMoves();
            return firstTurn;
        } else {
            HashMap<Move, Set<Turn>> allMoves = getAllValidMoves();

            //RULE NO1: if there are no valid moves, then trade
            if (allMoves.isEmpty()) {
                return null;
            }
            //if there are valid moves, then make a move that is the most profitable

            //RULE NO2: if there are valid moves, eliminate ones that give the opponent a chance to double his points
            allMoves = eliminatePotentialOpponentQwirkles(allMoves, 5);
            if (allMoves.isEmpty()) {
                return null;
            }

            //RULE NO3: if there are valid moves, eliminate ones that score lower than 4 points
            allMoves = eliminateMovesThatHaveScoreOrLess(allMoves, 3);
            if (allMoves.isEmpty()) {
                return null;
            }

            //RULE NO4: if there are valid moves, play the most profitable one
            Turn mostprofitable = getMostProfitableTurn(allMoves);
            return mostprofitable;
        }
    }

    private HashMap<Move, Set<Turn>> eliminatePotentialOpponentQwirkles(HashMap<Move, Set<Turn>> allMoves, int score) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn) {
                    grid.boardAddMove(move);
                }
                if (turn.calcScore(grid) != score) {
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;
    }

    /**
     * @param allMoves
     * @param score
     * @return allMoves that don't score 5 points
     */
    private HashMap<Move, Set<Turn>> eliminateMovesThatHaveScore(HashMap<Move, Set<Turn>> allMoves, int score) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn) {
                    grid.boardAddMove(move);
                }
                if (turn.calcScore(grid) != score) {
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;
    }

    private HashMap<Move, Set<Turn>> eliminateMovesThatHaveScoreOrLess(HashMap<Move, Set<Turn>> allMoves, int score) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn) {
                    grid.boardAddMove(move);
                }
                if (turn.calcScore(grid) > score) {
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;
    }

    private Turn getMostProfitableTurn(HashMap<Move, Set<Turn>> allMoves) {
        //assign to the first element
        Turn mostProfitable = null;
        while (mostProfitable == null) {
            for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
                Set<Turn> turns = entry.getValue();
                for (Turn turn : turns) {
                    mostProfitable = turn;
                    if (mostProfitable != null) {
                        break;
                    }
                }
            }
        }
        Grid grid = getBoard().getDeepCopy();
        for (Move move : mostProfitable) {
            grid.boardAddMove(move);
        }
        int scoreMostProfitable = mostProfitable.calcScore(grid);
        for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                grid = getBoard().getDeepCopy();
                for (Move move : turn) {
                    grid.boardAddMove(move);
                }
                if (turn.calcScore(grid) > scoreMostProfitable) {
                    mostProfitable = turn;
                }
            }
        }
        return mostProfitable;
    }

    public LevelOfDifficulty getLevelOfDifficulty() {
        return levelOfDifficulty;
    }


    /**
     * @return a Set of tiles with the lowest occurence in the deck to trade
     */
    public void trade() {
        switch (levelOfDifficulty) {
            case EASY -> {
                tradeRandomNumTiles();
                System.out.println("Trade easyMode");
            }
            case AI -> {
                //find the tile that has no color occurence, nor shape occurence
                //if there are multiple tiles with no correlation to others, then trade them all
                tradeAI();
                System.out.println("Trade AI");
            }
        }
    }

    private void tradeAI() {
        HashMap<Tile, Integer> sameTileCount = new HashMap<>();
        HashMap<Tile.TileColor, Integer> sameColorCount = new HashMap<>();
        HashMap<Tile.TileShape, Integer> sameShapeCount = new HashMap<>();
        HashSet<Tile> tilesToTrade = new HashSet<>();

        for (Tile tile : getDeck().getTilesInDeck()) {
            sameTileCount.put(tile, getDeck().getTilesInDeck().stream().filter(t -> t.equals(tile)).toList().size());
            sameColorCount.put(tile.getColor(), getDeck().getTilesInDeck().stream().filter(t -> t.isSameColor(tile)).toList().size());
            sameShapeCount.put(tile.getShape(), getDeck().getTilesInDeck().stream().filter(t -> t.isSameShape(tile)).toList().size());
        }

        System.out.println(sameTileCount);
        System.out.println(sameColorCount);
        System.out.println(sameShapeCount);


        //if more than 1 of the same tile, then trade all sets of same tiles
        if (sameTileCount.values().stream().anyMatch(i -> i > 1)) {
            System.out.println("trade all same tiles");
            for (Tile tile : sameTileCount.keySet()) {
                if (sameTileCount.get(tile) > 1) {
                    tilesToTrade.add(tile);
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        //if all maps have same value, then trade all tiles with same value, and value is 1, then trade random tiles
        if (sameColorCount.values().stream().allMatch(i -> i == 1)
                && sameShapeCount.values().stream().allMatch(i -> i == 1)) {
            System.out.println("trade random tiles");
            tradeRandomNumTiles();
            return;
        }

        //if all maps have same value, and value more than 1, then trade same tiles
        if (sameColorCount.values().stream().allMatch(i -> i > 1)
                && sameShapeCount.values().stream().allMatch(i -> i > 1)
                && sameTileCount.values().stream().allMatch(i -> i > 1)) {
            System.out.println("trade all same tiles");
            for (Tile tile : sameTileCount.keySet()) {
                if (sameTileCount.get(tile) > 1) {
                    tilesToTrade.add(tile);
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        //if color frequency and shape frequency are the same, pick a random number to trade color or shape
        //if random number is 0, then trade shape, if 1, then trade color
        if (sameColorCount.values().stream().allMatch(i -> Objects.equals(i, sameShapeCount.values()
                .stream()
                .findFirst()
                .get()))
                && sameShapeCount.values().stream().allMatch(i -> Objects.equals(i, sameColorCount.values()
                .stream()
                .findFirst()
                .get()))) {
            System.out.println("trade random color or shape");
            int random = randomTileChooser.nextInt(2);
            if (random == 0) {
                Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();

                for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
                    Tile.TileShape key = entry.getKey();
                    Integer value1 = entry.getValue();
                    if (value1.compareTo(value) == 0) {
                        tilesToTrade.addAll(tradeSameShape(key));
                    }
                }
                tradeTiles(tilesToTrade);
            } else {

                Integer value = sameColorCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
                for (Map.Entry<Tile.TileColor, Integer> entry : sameColorCount.entrySet()) {
                    Tile.TileColor key = entry.getKey();
                    Integer value1 = entry.getValue();
                    if (value1.compareTo(value) == 0) {
                        tilesToTrade.addAll(tradeSameColor(key));
                    }
                }
                tradeTiles(tilesToTrade);
            }
            return;
        }

        //if color frequency minimum is less than shape frequency minimum, then trade color
        if (sameColorCount.values().stream().min(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().min(Integer::compareTo).get()) < 0) {
            System.out.println("trade color");
            Integer value = sameColorCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileColor, Integer> entry : sameColorCount.entrySet()) {
                Tile.TileColor key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameColor(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        //if color frequency minimum is greater than shape frequency minimum, then trade shape, as there is more color variety
        if (sameColorCount.values().stream().min(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().min(Integer::compareTo).get()) > 0) {
            System.out.println("trade shape");
            Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
                Tile.TileShape key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameShape(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }


        //if color frequency max is greater than shape frequency max, then trade least common shape
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) > 0) {
            System.out.println("trade least common shape");
            Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
                Tile.TileShape key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameShape(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        //if color frequency max is less than shape frequency max, then trade least common color
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) < 0) {
            System.out.println("trade least common color");
            Integer value = sameColorCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileColor, Integer> entry : sameColorCount.entrySet()) {
                Tile.TileColor key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameColor(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }


        //if more variety in color, then trade least common color
        if (sameColorCount.size() > sameShapeCount.size()) {
            System.out.println("trade least common color");
            Integer value = sameColorCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileColor, Integer> entry : sameColorCount.entrySet()) {
                Tile.TileColor key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameColor(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        //if more variety in shape, then trade least common shape
        if (sameColorCount.size() < sameShapeCount.size()) {
            System.out.println("trade least common shape");
            Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
            for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
                Tile.TileShape key = entry.getKey();
                Integer value1 = entry.getValue();
                if (value1.compareTo(value) == 0) {
                    tilesToTrade.addAll(tradeSameShape(key));
                }
            }
            tradeTiles(tilesToTrade);
            return;
        }

        System.out.println("trade RANDOM");
        tradeRandomNumTiles();
    }


    private void tradeTiles(HashSet<Tile> tiles) {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(tiles);
        getDeck().trade(getBag(), tilesToTrade);
    }


    private HashSet<Tile> tradeSameColor(Tile.TileColor leastCommonColor) {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(getDeck().getTilesInDeck().stream().filter(t -> t.getColor() == leastCommonColor).toList());
        return new HashSet<>(tilesToTrade);
    }

    /**
     * Trade tiles with same shape
     * these are the least common tiles in the deck
     *
     * @param leastCommonShape
     * @return
     */
    private HashSet<Tile> tradeSameShape(Tile.TileShape leastCommonShape) {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(getDeck().getTilesInDeck().stream().filter(t -> t.getShape() == leastCommonShape).toList());
        return new HashSet<>(tilesToTrade);
    }

    /**
     * trades all but one of the same tile
     * trading most common tile is implemeneted because strategically you want either the same shape or the same color to get the most points
     * if you have the same tile in your hand you can't place many of them in a row
     *
     * @param mostCommonTile: the most common tile in the deck
     * @return
     */
    private HashSet<Tile> tradeSameTile(Tile mostCommonTile) {
        ArrayList<Tile> tilesWithSameTile = new ArrayList<>(getDeck().getTilesInDeck().stream().filter(t -> t.equals(mostCommonTile)).toList());
        tilesWithSameTile.remove(mostCommonTile);
        return new HashSet<>();
    }

    private void tradeRandomNumTiles() {
        ArrayList<Tile> tilesToTrade = new ArrayList<>();
        ArrayList<Tile> tilesInDeck = new ArrayList<>(getDeck().getTilesInDeck());
        //has to be at least 1 tile to trade
        int randomTileNumToTrade = randomTileChooser.nextInt(getDeck().getTilesInDeck().size()) == 0 ? 1 : randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
        for (int i = 0; i < randomTileNumToTrade; i++) {
            int randomTileIndex = randomTileChooser.nextInt(tilesInDeck.size());
            tilesToTrade.add(tilesInDeck.get(randomTileIndex));
            tilesInDeck.remove(randomTileIndex);
        }
        getDeck().trade(getBag(), tilesToTrade);
    }

    private List<List<Tile>> getAllPossibleCombinationAndPermutations() {
        Set<Set<Tile>> setOfCombinations = mostOccuringTileCombo();
        List<List<Tile>> allCombinations = new ArrayList<>();
        for (Set<Tile> combo : setOfCombinations) {
            List<Tile> tileCombo = new ArrayList<>(combo);
            allCombinations.addAll(getAllPermutations(tileCombo));
        }
        return allCombinations;
    }

    public Set<Set<Tile>> mostOccuringTileCombo() {
        Set<Set<Tile>> allTileCombinations = new HashSet<>();
        Set<Set<Tile>> toReturn = new HashSet<>();
        List<Tile> tilesInDeck = getDeck().getTilesInDeck();
        for (Tile tile : tilesInDeck) {
            Set<Tile> tileShapeCombination = new HashSet<>();
            Set<Tile> tileColorCombination = new HashSet<>();

            for (Tile t : tilesInDeck) {
                if (t.equals(tile)) {
                    continue;
                }
                if (t.isSameShape(tile) && !t.isSameColor(tile)) {
                    tileShapeCombination.add(t);
                    tileShapeCombination.add(tile);
                    continue;
                }
                if (!t.isSameShape(tile) && t.isSameColor(tile)) {
                    tileColorCombination.add(t);
                    tileColorCombination.add(tile);
                }
            }
            allTileCombinations.add(tileShapeCombination);
            allTileCombinations.add(tileColorCombination);
        }

        //after getting the highest tile combo
        //we need to make lists out of every combo into smaller ones
        //that way we can get all the combos
        for (Set<Tile> list : allTileCombinations) {
            toReturn.addAll(getAllCombinations(list));
        }
        toReturn.removeIf(x -> x.size() == 0);

        return toReturn;
    }

    public HashMap<Move, Set<Turn>> getAllValidMoves() {

        HashMap<Move, Set<Turn>> validMoves = new HashMap<>();
        List<List<Tile>> allCombinations = getAllPossibleCombinationAndPermutations();
        Grid grid;
        Set<Move> edges = getBoard().getAllOccupiedEdges();

        ArrayList<Move> moves = new ArrayList<>();

        for (Move edge : edges) {
            validMoves.computeIfAbsent(edge, k -> new HashSet<>());
            Move.Coordinate coordinate = edge.getCoordinate();
            for (List<Tile> combo : allCombinations) {
                boolean isEmptyUp = getBoard().isEmpty(coordinate.getRow() - 1, coordinate.getColumn());
                boolean isEmptyDown = getBoard().isEmpty(coordinate.getRow() + 1, coordinate.getColumn());
                boolean isEmptyLeft = getBoard().isEmpty(coordinate.getRow(), coordinate.getColumn() - 1);
                boolean isEmptyRight = getBoard().isEmpty(coordinate.getRow() - 1, coordinate.getColumn() + 1);

                if (isEmptyUp) {
                    grid = getBoard().getDeepCopy();
                    for (int i = 0; i < combo.size(); i++) {
                        Move.Coordinate newCoordinate = new Move.Coordinate(coordinate.getRow() - (i + 1), coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }

                if (isEmptyDown) {
                    grid = getBoard().getDeepCopy();
                    for (int i = 0; i < combo.size(); i++) {
                        Move.Coordinate newCoordinate = new Move.Coordinate(coordinate.getRow() + (i + 1), coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }

                if (isEmptyLeft) {
                    grid = getBoard().getDeepCopy();
                    for (int i = 0; i < combo.size(); i++) {
                        Move.Coordinate newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() - (i + 1));
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }

                if (isEmptyRight) {
                    grid = getBoard().getDeepCopy();
                    for (int i = 0; i < combo.size(); i++) {
                        Move.Coordinate newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() + (i + 1));
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }
                //for loop for 1up then right example: |->>>
                boolean firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn() + (i));
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1up then left example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn() - (i));
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1left then up example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() - 1);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() - i, coordinate.getColumn() - 1);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1left then down example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() - 1);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() + i, coordinate.getColumn() - 1);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1right then up example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() + 1);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() - i, coordinate.getColumn() + 1);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1right then down example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow(), coordinate.getColumn() + 1);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() + i, coordinate.getColumn() + 1);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1down then left example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow() + 1, coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() + 1, coordinate.getColumn() - i);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }


                //for loop for 1down then right example: |->>>
                firstTileFlag = true;
                grid = getBoard().getDeepCopy();
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn());
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!grid.isValidMoves(moves)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(coordinate.getRow() - 1, coordinate.getColumn() + i);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!grid.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
                if (moves.size() > 0) {
                    validMoves.get(edge).add(new Turn(moves));
                    moves.clear();
                }

            }
        }
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : validMoves.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            Set<Turn> turns = entry.getValue();
            Set<Turn> toAdd = new HashSet<>();
            for (Turn turn : turns) {
                if (turn.size() > 1) {
                    toAdd.add(turn);
                }
            }
            toReturn.put(entry.getKey(), toAdd);
        }
        return toReturn;
    }


    //this works perfectly
    //Next step (this is for the lecturers to read)
    //1. make a move that return a hashmap of the edge Move and a list of combinations that are valid
    //2. after this is completed we can make both the difficulties (easy and hard) based on that method
    //because we can just filter out based on score so easy plays moves that are 4 or less points, hard plays moves that are 5 or more points (example)
    //INPUT:[RED CLOVER 1, ORANGE DIAMOND 102, BLUE CIRCLE 17, BLUE CLOVER 85, BLUE EIGHT_POINT_STAR 87, ORANGE EIGHT_POINT_STAR 27]
    //OUTPUT:[
    // [BLUE CIRCLE 17, BLUE EIGHT_POINT_STAR 87],
    // [BLUE CLOVER 85],
    // [BLUE CLOVER 85, RED CLOVER 1],
    // [ORANGE DIAMOND 102],
    // [BLUE CLOVER 85, BLUE CIRCLE 17, BLUE EIGHT_POINT_STAR 87],
    // [ORANGE EIGHT_POINT_STAR 27],
    // [BLUE CLOVER 85, BLUE CIRCLE 17],
    // [ORANGE DIAMOND 102, ORANGE EIGHT_POINT_STAR 27],
    // [ORANGE EIGHT_POINT_STAR 27, BLUE EIGHT_POINT_STAR 87],
    // [BLUE CIRCLE 17],
    // [BLUE EIGHT_POINT_STAR 87],
    // [RED CLOVER 1]
    // ]
    public Set<Set<Tile>> allDeckTileCombinations() {
        Set<Set<Tile>> allTileCombinations = new HashSet<>();
        Set<Set<Tile>> toReturn = new HashSet<>();
        List<Tile> tilesInDeck = getDeck().getTilesInDeck();
        for (Tile tile : tilesInDeck) {
            Set<Tile> tileShapeCombination = new HashSet<>();
            Set<Tile> tileColorCombination = new HashSet<>();

            for (Tile t : tilesInDeck) {
                if (t.equals(tile)) {
                    continue;
                }
                if (t.isSameShape(tile) && !t.isSameColor(tile)) {
                    tileShapeCombination.add(t);
                    tileShapeCombination.add(tile);
                    continue;
                }
                if (!t.isSameShape(tile) && t.isSameColor(tile)) {
                    tileColorCombination.add(t);
                    tileColorCombination.add(tile);
                }
            }
            allTileCombinations.add(tileShapeCombination);
            allTileCombinations.add(tileColorCombination);
        }

        //after getting the highest tile combo
        //we need to make lists out of every combo into smaller ones
        //that way we can get all the combos
        for (Set<Tile> list : allTileCombinations) {
            toReturn.addAll(getAllCombinations(list));
        }
        toReturn.removeIf(x -> x.size() == 0);

        return toReturn;
    }

    /**
     * This method returns all the possible combinations of a given list of tiles
     * uses the power of recursion
     *
     * @param list
     * @return
     */
    private Set<Set<Tile>> getAllCombinations(Set<Tile> list) {
        Set<Set<Tile>> toReturn = new HashSet<>();
        if (list.isEmpty()) {
            toReturn.add(new HashSet<>());
            return toReturn;
        }
        ArrayList<Tile> listCopy = new ArrayList<>(list);
        Tile head = listCopy.get(0);
        Set<Tile> rest = new HashSet<>(listCopy.subList(1, listCopy.size()));

        Set<Set<Tile>> combosWithoutFirst = getAllCombinations(rest);

        for (Set<Tile> set : combosWithoutFirst) {
            Set<Tile> comboWithFirst = new HashSet<>();
            comboWithFirst.add(head);
            comboWithFirst.addAll(set);
            toReturn.add(comboWithFirst);
        }
        toReturn.addAll(combosWithoutFirst);
        return toReturn;
    }

    /**
     * This method returns all the combinations and every single order of the given list of tiles
     * this is to make it easier to test for all possible valid moves in the game
     *
     * @param list
     * @return
     */
    public List<List<Tile>> getAllPermutations(List<Tile> list) {
        List<List<Tile>> toReturn = new ArrayList<>();
        if (list.size() == 1) {
            toReturn.add(list);
            return toReturn;
        }
        for (int i = 0; i < list.size(); i++) {
            Tile currentTile = list.get(i);
            List<Tile> remainingTiles = new ArrayList<>(list);
            remainingTiles.remove(i);
            List<List<Tile>> permutations = getAllPermutations(remainingTiles);
            for (List<Tile> permutation : permutations) {
                List<Tile> newList = new ArrayList<>();
                newList.add(currentTile);
                newList.addAll(permutation);
                toReturn.add(newList);
            }
        }
        return toReturn;
    }

    @Override
    public void save() {
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                    INSERT INTO int_player(player_id, player_name, difficulty)
                            VALUES (nextval('player_id_seq'),?,?);
                    """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setString(1, getName());
            ptsmt.setString(2, levelOfDifficulty.toString());
            ptsmt.executeUpdate();
            ptsmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }
    }

}



