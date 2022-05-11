package qwirkle.model.computer;

import qwirkle.data.Database;
import qwirkle.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

import static qwirkle.model.computer.Computer.LevelOfDifficulty.*;

/**
 * @author: Seifeldin Sabry
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class ComputerAI extends Computer implements QwirkleEngineAI {

    private int numberOfConsecutiveTrades = 0;



    public ComputerAI(Bag bag, Grid grid) {
        super(bag, grid);
        setLevelOfDifficulty(AI);

    }

    /**
     * @return the best turn respective of the level of difficulty, null if no turn can be made
     */
    public Turn makeTurn(int turnNo) {
        boolean isFirstTurn = getBoard().getUsedSpaces().size() == 0;
        if (isFirstTurn){
            return makeFirstTurn();
        }
        HashMap<Move, Set<Turn>> allMoves = getMoveValidator().getAllValidMoves(getBoard());
        allMoves = clearAllEmptyTurns(allMoves);
        if (thereAreNoMoves(allMoves)) {
            trade();
            return null;
        }
        if (turnNo > 5 && getBag().getTiles().size() > 0 && numberOfConsecutiveTrades <= 2) {
            allMoves = removeAllTurnsThatCanMakeOpponentQwirkle(allMoves);
            allMoves = removeAllTurnsThatContainLessThanScoreFive(allMoves);
        }
        if (thereAreNoMoves(allMoves)) {
            trade();
            numberOfConsecutiveTrades++;
            return null;
        }
        HashMap<Move, Set<Turn>> multipleRowsOrColumnsTurns = getTurnsThatHaveMultipleRowsOrColumns(allMoves);
        Turn highestAdjacentScoringTurn = getMostProfitableTurn(multipleRowsOrColumnsTurns);
        Turn highestScoringTurn = getMostProfitableTurn(allMoves);
        if (highestScoringTurn != null && highestAdjacentScoringTurn != null) {
            return highestScoringTurn(highestScoringTurn, highestAdjacentScoringTurn);
        }
        else if (highestScoringTurn != null) {
            return highestScoringTurn;
        }
        else if (highestAdjacentScoringTurn != null) {
            return highestAdjacentScoringTurn;
        }
        trade();
        numberOfConsecutiveTrades++;
        return null;

    }

    private Turn highestScoringTurn(Turn highestScoringTurn, Turn highestAdjacentScoringTurn) {
        Grid grid = getBoard().getDeepCopy();
        for(Move move : highestScoringTurn) {
            grid.boardAddMove(move);
        }
        int highestScoringTurnScore = highestScoringTurn.calcScore(grid);
        grid = getBoard().getDeepCopy();
        for (Move move : highestAdjacentScoringTurn) {
            grid.boardAddMove(move);
        }
        int highestAdjacentScoringTurnScore = highestAdjacentScoringTurn.calcScore(grid);
        if (highestScoringTurnScore > highestAdjacentScoringTurnScore) {
            return highestScoringTurn;
        }
        return highestAdjacentScoringTurn;
    }

    private HashMap<Move, Set<Turn>> clearAllEmptyTurns(HashMap<Move, Set<Turn>> allMoves) {
        HashMap<Move, Set<Turn>> newAllMoves = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : allMoves.entrySet()) {
            Move move = entry.getKey();
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                if(turn.size() > 0) {
                    newAllMoves.put(move, turns);
                }
            }
        }
        return newAllMoves;
    }

    private Turn makeFirstTurn() {
        Set<ArrayList<Tile>> combos = getMoveValidator().getLargestCombinations();
        Iterator<ArrayList<Tile>> iterator = combos.iterator();
        ArrayList<Tile> selectedCombo = null;
        while(iterator.hasNext()) {
            ArrayList<Tile> combo = iterator.next();
            if(combo.size() == 5){
                continue;
            }
            if (combo.size() == 6) {
                selectedCombo = combo;
                break;
            }
            if ( selectedCombo == null || selectedCombo.isEmpty() ) {
                selectedCombo = combo;
            }

            if (combo.size() >= selectedCombo.size()) {
                selectedCombo = combo;
            }
        }
        assert selectedCombo != null;
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
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn){
                    grid.boardAddMove(move);
                }
                switch (grid.determineDirection(turn)){
                    case HORIZONTAL -> {
                        if(grid.getConnectedHorizontalArray(turn.get(0).getCoordinate()).size() != 5 && notVerticallyFiveConnected(grid,turn)) {
                            toReturn.get(entry.getKey()).add(turn);
                        }
                    }
                    case VERTICAL -> {
                        if(grid.getConnectedVerticalArray(turn.get(0).getCoordinate()).size() != 5 && notHorizontallyFiveConnected(grid, turn)) {
                            toReturn.get(entry.getKey()).add(turn);
                        }
                    }
                    case SINGLE -> {
                        if(grid.getConnectedHorizontalArray(turn.get(0).getCoordinate()).size() != 5) {
                            toReturn.get(entry.getKey()).add(turn);
                        }
                        if(grid.getConnectedVerticalArray(turn.get(0).getCoordinate()).size() != 5) {
                            toReturn.get(entry.getKey()).add(turn);
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    private boolean notHorizontallyFiveConnected(Grid grid, Turn turn) {
        for (Move move : turn) {
            if(grid.getConnectedHorizontalArray(move.getCoordinate()).size() == 5) {
                return false;
            }
        }
        return true;
    }

    private boolean notVerticallyFiveConnected(Grid grid, Turn turn) {
        for (Move move : turn) {
            if (grid.getConnectedVerticalArray(move.getCoordinate()).size() == 5) {
                return false;
            }
        }
        return true;
    }

    @Override
    public HashMap<Move, Set<Turn>> removeAllTurnsThatContainLessThanScoreFive(HashMap<Move, Set<Turn>> allMoves) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn){
                    grid.boardAddMove(move);
                }
                if(turn.calcScore(grid) > 4){
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;

    }

    @Override
    public HashMap<Move, Set<Turn>> getTurnsThatHaveMultipleRowsOrColumns(HashMap<Move, Set<Turn>> allMoves) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                Grid grid = getBoard().getDeepCopy();
                for (Move move : turn){
                    grid.boardAddMove(move);
                }
                int collisons = 0;
                switch (grid.determineDirection(turn)){
                    //horizontal
                    case HORIZONTAL -> {
                        for (Move move : turn){
                            if(grid.getConnectedVerticalArray(move.getCoordinate()).size() >= 1){
                                collisons++;
                            }
                        }
                    }
                    case VERTICAL -> {
                        for (Move move : turn){
                            if(grid.getConnectedHorizontalArray(move.getCoordinate()).size() >= 1){
                                collisons++;
                            }
                        }
                    }
                    case SINGLE -> {
                        if (grid.getConnectedVerticalArray(turn.get(0).getCoordinate()).size() >=1 && grid.getConnectedHorizontalArray(turn.get(0).getCoordinate()).size() >=1){
                            collisons+=2;
                        }
                    }
                }
                if(collisons >= 2){
                    toReturn.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
                    toReturn.get(entry.getKey()).add(turn);
                }
            }
        }
        return toReturn;
    }

    @Override
    public Turn getMostProfitableTurn(HashMap<Move, Set<Turn>> allMoves) {
        Turn mostProfitable = null;
        for (Map.Entry<Move,Set<Turn>> entry: allMoves.entrySet()) {
            Set<Turn> turns = entry.getValue();
            for (Turn turn : turns) {
                mostProfitable = turn;
                if (mostProfitable != null) {
                    break;
                }
            }
        }
        if (mostProfitable == null) {
            return null;
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


        //if more than 1 of the same tile, then trade 1 of each pair
        if (sameTileCount.values().stream().anyMatch(i -> i > 1)) {
            ArrayList<Tile> tiles =
                    sameTileCount.entrySet()
                            .stream()
                            .filter(e -> e.getValue() > 1)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(ArrayList::new));
            tiles.remove(sameTileCount.entrySet().stream().filter(e -> e.getValue() > 1).findFirst().get().getKey());
            tradeTiles(tiles);
            return;
        }



        //if all maps have same value, and value more than 1, then trade same tiles
        if (sameColorCount.values().stream().allMatch(i -> i > 1)
                && sameShapeCount.values().stream().allMatch(i -> i > 1)
                && sameTileCount.values().stream().allMatch(i -> i > 1)) {
            ArrayList<Tile> tiles =
                    sameTileCount.entrySet()
                            .stream()
                            .filter(e -> e.getValue() > 1)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toCollection(ArrayList::new));
            tiles.remove(sameTileCount.entrySet().stream().filter(e -> e.getValue() > 1).findFirst().get().getKey());
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
            tradeLeastOccurringColors(sameColorCount);
            return;
        }

        //if color frequency minimum is greater than shape frequency minimum, then trade least common shapes, as there is more color variety
        if (sameColorCount.values().stream().min(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().min(Integer::compareTo).get()) > 0) {
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }



        //if color frequency max is greater than shape frequency max, then trade least common shapes
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) > 0) {
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }

        //if color frequency max is less than shape frequency max, then trade least common colors
        if (sameColorCount.values().stream().max(Integer::compareTo).get().compareTo(sameShapeCount.values().stream().max(Integer::compareTo).get()) < 0) {
            tradeLeastOccurringColors(sameColorCount);
            return;
        }


        //if more variety in color, then trade least common shape
        if (sameColorCount.size() > sameShapeCount.size()) {
            tradeLeastOccurringShapes(sameShapeCount);
            return;
        }

        //if more variety in shape, then trade least common color
        if (sameColorCount.size() < sameShapeCount.size()) {
            tradeLeastOccurringColors(sameColorCount);
            return;
        }

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

    @Override
    public void save() {
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_player(player_id, player_name, difficulty)
                                 VALUES (nextval('player_id_seq'),?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setString(1,getName());
            ptsmt.setString(2,getLevelOfDifficulty().toString());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }
    }
}


