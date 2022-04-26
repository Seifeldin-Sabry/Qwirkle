package qwirkle.model.computer;

import qwirkle.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static qwirkle.model.computer.Computer.LevelOfDifficulty.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ComputerAI extends Computer implements QwirkleEngineAI {



    public ComputerAI(Bag bag, Grid grid) {
        super(bag, grid, AI);
    }

    /**
     * @return the best turn respective of the level of difficulty, null if no turn can be made
     */
    @Override
    public Turn makeTurn() {
        boolean isFirstTurn = getBoard().getUsedSpaces().size() == 0;
        if (isFirstTurn){
            return makeFirstTurn();
        }
        HashMap<Move, Set<Turn>> allMoves = getMoveValidator().getAllValidMoves(getBoard());
        if (thereAreNoMoves(allMoves)) {
            trade();
            return null;
        }
        allMoves = removeAllTurnsThatCanMakeOpponentQwirkle(allMoves);
        allMoves = removeAllTurnsThatContainLessThanScoreFour(allMoves);
        if (thereAreNoMoves(allMoves)) {
            trade();
            return null;
        }
        return getMostProfitableTurn(allMoves);
    }

    private Turn makeFirstTurn() {
        Set<Set<Tile>> combos = getMoveValidator().getLargestCombinations();
        if(combos.isEmpty()) {
            trade();
            return null;
        }
        Iterator<Set<Tile>> iterator = combos.iterator();
        Set<Tile> selectedCombo = null;
        while(iterator.hasNext()) {
            Set<Tile> combo = iterator.next();
            //if(combo.size() == 5 || combo.size() < 3)
            if(combo.size() == 5){
                continue;
            }
            if (combo.size() == 6) {
                selectedCombo = combo;
                break;
            }
            selectedCombo = combo;
        }
        if (selectedCombo == null) {
            trade();
            return null;
        }
        return firstTurnInRandomDirection(new ArrayList<>(selectedCombo));
    }




    private boolean thereAreNoMoves(HashMap<Move, Set<Turn>> allMoves) {
        for (Set<Turn> turns : allMoves.values()) {
            for (Turn turn : turns) {
                if(turn.size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }



    //working on it...
    @Override
    public HashMap<Move,Set<Turn>> removeAllTurnsThatCanMakeOpponentQwirkle(HashMap<Move, Set<Turn>> allMoves) {
//        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
//        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
//            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
//            Set<Turn> turns = entry.getValue();
//            for (Turn turn : turns) {
//                Grid grid = getBoard().getDeepCopy();
//                for (Move move : turn){
//                    grid.boardAddMove(move);
//                }
////                if(turn.calcScore(grid) != score){
////                    toReturn.get(entry.getKey()).add(turn);
////                }
//            }
//        }
//        return toReturn;
        return allMoves;
    }

    @Override
    public HashMap<Move, Set<Turn>> removeAllTurnsThatContainLessThanScoreFour(HashMap<Move, Set<Turn>> allMoves) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn){
                    grid.boardAddMove(move);
                }
                if(turn.calcScore(grid) > 3){
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;

    }

    @Override
    public Turn getMostProfitableTurn(HashMap<Move, Set<Turn>> allMoves) {
        Turn mostProfitable = null;
        while(mostProfitable == null){
            for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
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
        for (Move move : mostProfitable){
            grid.boardAddMove(move);
        }
        int scoreMostProfitable = mostProfitable.calcScore(grid);
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                grid = getBoard().getDeepCopy();
                for (Move move : turn){
                    grid.boardAddMove(move);
                }
                if(turn.calcScore(grid) > scoreMostProfitable){
                    mostProfitable = turn;
                }
            }
        }
        return mostProfitable;
    }



    /**
     * trades
     */
    @Override
    public void trade() {
        HashMap<Tile, Integer> sameTileCount = new HashMap<>();
        HashMap<Tile.TileColor, Integer> sameColorCount = new HashMap<>();
        HashMap<Tile.TileShape, Integer> sameShapeCount = new HashMap<>();

        for (Tile tile: getDeck().getTilesInDeck()) {
            sameTileCount.put(tile, getDeck().getTilesInDeck().stream().filter(t -> t.equals(tile)).toList().size());
            sameColorCount.put(tile.getColor(),getDeck().getTilesInDeck().stream().filter(t -> t.isSameColor(tile)).toList().size());
            sameShapeCount.put(tile.getShape(),getDeck().getTilesInDeck().stream().filter(t -> t.isSameShape(tile)).toList().size());
        }

        System.out.println(sameTileCount);
        System.out.println(sameColorCount);
        System.out.println(sameShapeCount);
        System.out.println(getDeck().getTilesInDeck());

        //if more than 1 of the same tile, then trade 1 of each pair
        if (sameTileCount.values().stream().anyMatch(i -> i > 1)) {
            System.out.println("trade same tiles");
            ArrayList<Tile> tiles =
                    sameTileCount.entrySet()
                            .stream()
                            .filter(e -> e.getValue() > 1)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(ArrayList::new));
            tiles.remove(sameTileCount.entrySet().stream().filter(e -> e.getValue() > 1).findFirst().get().getKey());
            //for testing
            System.out.println(tiles);
            tradeTiles(tiles);
            return;
        }



        //if all maps have same value, and value more than 1, then trade same tiles
        if (sameColorCount.values().stream().allMatch(i -> i > 1)
                && sameShapeCount.values().stream().allMatch(i -> i > 1)
                && sameTileCount.values().stream().allMatch(i -> i > 1)) {
            System.out.println("trade same tiles");
            ArrayList<Tile> tiles =
                    sameTileCount.entrySet()
                            .stream()
                            .filter(e -> e.getValue() > 1)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(ArrayList::new));
            tiles.remove(sameTileCount.entrySet().stream().filter(e -> e.getValue() > 1).findFirst().get().getKey());
            //for testing
            System.out.println(tiles);
            tradeTiles(tiles);
            return;
        }

        //if color frequency and shape frequency are the same, pick a random number to trade color or shape
        //if random number is 0, then trade shape, if 1, then trade color
        if(sameColorCount.values().stream().allMatch(i -> Objects.equals(i, sameShapeCount.values()
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
                tradeLeastOccurringShapes(sameShapeCount);
            }
            else {
                tradeLeastOccurringColors(sameColorCount);
            }
            return;
        }

        //if color frequency minimum is less than shape frequency minimum, then trade color
        if (sameColorCount.values().stream().min(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().min(Integer::compareTo).get()) < 0) {
            System.out.println("trade color");
            tradeLeastOccurringColors(sameColorCount);
            return;
        }

        //if color frequency minimum is greater than shape frequency minimum, then trade least common shapes, as there is more color variety
        if (sameColorCount.values().stream().min(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().min(Integer::compareTo).get()) > 0) {
            System.out.println("trade shape");
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }



        //if color frequency max is greater than shape frequency max, then trade least common shapes
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) > 0) {
            System.out.println("trade least common shape");
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }

        //if color frequency max is less than shape frequency max, then trade least common colors
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) < 0) {
            System.out.println("trade least common color");
            tradeLeastOccurringColors(sameColorCount);
            return;
        }


        //if more variety in color, then trade least common shape
        if (sameColorCount.size() > sameShapeCount.size()) {
            System.out.println("trade least common shape");
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }

        //if more variety in shape, then trade least common color
        if (sameColorCount.size() < sameShapeCount.size()) {
            System.out.println("trade least common color");
            tradeLeastOccurringColors(sameColorCount);
            return;
        }

        System.out.println("trade RANDOM");
        tradeRandomNumTiles();
    }

    private void tradeLeastOccurringShapes(HashMap<Tile.TileShape, Integer> sameShapeCount) {
        HashSet<Tile.TileShape> shapesTraded = new HashSet<>();
        Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
        for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
            Tile.TileShape shape = entry.getKey();
            Integer value1 = entry.getValue();
            if (value1.compareTo(value) == 0 && !shapesTraded.contains(shape)) {
                tradeLeastCommonShape(shape);
                shapesTraded.add(shape);
            }
        }
    }

    private void tradeLeastOccurringColors(HashMap<Tile.TileColor, Integer> sameColorCount) {
        HashSet<Tile.TileColor> colorTraded = new HashSet<>();
        Integer value = sameColorCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();
        for (Map.Entry<Tile.TileColor, Integer> entry : sameColorCount.entrySet()) {
            Tile.TileColor color = entry.getKey();
            Integer value1 = entry.getValue();
            if (value1.compareTo(value) == 0 && !colorTraded.contains(color)) {
                tradeLeastCommonColors(color);
                colorTraded.add(color);
            }
        }
    }

    private void tradeTiles(ArrayList<Tile> tiles) {
        getDeck().trade(getBag(), tiles);
    }


    private void tradeLeastCommonColors(Tile.TileColor leastCommonColor) {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(getDeck().getTilesInDeck().stream().filter(t -> t.getColor() == leastCommonColor).toList());
        tradeTiles(tilesToTrade);
    }

    /**
     * Trade tiles with same shape
     * these are the least common tiles in the deck
     * @param leastCommonShape the least common shape
     */
    private void tradeLeastCommonShape(Tile.TileShape leastCommonShape) {
        ArrayList<Tile> tilesToTrade = new ArrayList<>(getDeck().getTilesInDeck().stream().filter(t -> t.getShape() == leastCommonShape).toList());
        tradeTiles(tilesToTrade);
    }

}

