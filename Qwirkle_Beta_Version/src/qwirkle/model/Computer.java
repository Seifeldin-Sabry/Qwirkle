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
            case EASY -> {
                tradeRandomNumTiles();
            }
            case AI ->{
                //find the tile that has no color occurence, nor shape occurence
                //if there are multiple tiles with no correlation to others, then trade them all
                tradeAI();
            }
        }
    }

    private void tradeAI() {
        HashMap<Tile, Integer> sameTileCount = new HashMap<>();
        HashMap<Tile.TileColor, Integer> sameColorCount = new HashMap<>();
        HashMap<Tile.TileShape, Integer> sameShapeCount = new HashMap<>();
        HashSet<Tile> tilesToTrade = new HashSet<>();

        for (Tile tile: getDeck().getTilesInDeck()) {
            sameTileCount.put(tile, getDeck().getTilesInDeck().stream().filter(t -> t.equals(tile)).toList().size());
            sameColorCount.put(tile.getColor(),getDeck().getTilesInDeck().stream().filter(t -> t.isSameColor(tile)).toList().size());
            sameShapeCount.put(tile.getShape(),getDeck().getTilesInDeck().stream().filter(t -> t.isSameShape(tile)).toList().size());
        }

        System.out.println(sameTileCount);
        System.out.println(sameColorCount);
        System.out.println(sameShapeCount);


        //if more than 1 of the same tile, then trade all sets of same tiles
        if (sameTileCount.values().stream().anyMatch(i -> i > 1)) {
            System.out.println("trade all same tiles");
            for(Tile tile: sameTileCount.keySet()) {
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
            for(Tile tile: sameTileCount.keySet()) {
                if (sameTileCount.get(tile) > 1) {
                    tilesToTrade.add(tile);
                }
            }
            tradeTiles(tilesToTrade);
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
                Integer value = sameShapeCount.entrySet().stream().min(Map.Entry.comparingByValue()).get().getValue();

                for (Map.Entry<Tile.TileShape, Integer> entry : sameShapeCount.entrySet()) {
                    Tile.TileShape key = entry.getKey();
                    Integer value1 = entry.getValue();
                    if (value1.compareTo(value) == 0) {
                        tilesToTrade.addAll(tradeSameShape(key));
                    }
                }
                tradeTiles(tilesToTrade);
            }
            else {

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
        getDeck().trade(getBag(),tilesToTrade);
    }

    public HashMap<Move, Set<Turn>> getAllValidMoves() {
        HashMap<Move, Set<Turn>> validMoves = new HashMap<>();
        Set<Set<Tile>> combos = allDeckTileCombinations();
        Set<Move> edges = getBoard().getAllOccupiedEdges();
        for(Move edge: edges){
            validMoves.computeIfAbsent(edge, k -> new HashSet<>());
            Move.Coordinate coordinate = edge.getCoordinate();
            for(Set<Tile> combo: combos){
                ArrayList<Tile> tiles = new ArrayList<>(combo);
                boolean isEmptyUp = getBoard().isEmpty(coordinate.getRow()-1,coordinate.getColumn());
                boolean isEmptyDown = getBoard().isEmpty(coordinate.getRow()+1,coordinate.getColumn());
                boolean isEmptyLeft = getBoard().isEmpty(coordinate.getRow(),coordinate.getColumn()-1);
                boolean isEmptyRight = getBoard().isEmpty(coordinate.getRow()-1,coordinate.getColumn()+1);

                if (isEmptyUp) {
                    Turn turn = new Turn();
                    turn.add(edge);
                    for (int i = 0; i < tiles.size(); i++) {
                        turn.add(new Move(tiles.get(i),new Move.Coordinate(coordinate.getRow() - (1-i), coordinate.getColumn())));
                        if(!getBoard().isValidMove(turn)){
                            turn.removeLast();
                        }
                    }
                    if(turn.size() > 1){
                        validMoves.get(edge).add(turn);
                    }
                }

                if (isEmptyDown) {
                    Turn turn = new Turn();
                    turn.add(edge);
                    for (int i = 0; i < tiles.size(); i++) {
                        turn.add(new Move(tiles.get(i),new Move.Coordinate(coordinate.getRow() + (1+i), coordinate.getColumn())));
                        if(!getBoard().isValidMove(turn)){
                            turn.removeLast();
                        }
                    }
                    if(turn.size() > 1){
                        validMoves.get(edge).add(turn);
                    }
                }

                if (isEmptyLeft) {
                    Turn turn = new Turn();
                    turn.add(edge);
                    for (int i = 0; i < tiles.size(); i++) {
                        turn.add(new Move(tiles.get(i),new Move.Coordinate(coordinate.getRow() , coordinate.getColumn() - (1 - i))));
                        if(!getBoard().isValidMove(turn)){
                            turn.removeLast();
                        }
                    }
                    if(turn.size() > 1){
                        validMoves.get(edge).add(turn);
                    }
                }

                if (isEmptyRight) {
                    Turn turn = new Turn();
                    turn.add(edge);
                    for (int i = 0; i < tiles.size(); i++) {
                        turn.add(new Move(tiles.get(i),new Move.Coordinate(coordinate.getRow() , coordinate.getColumn() + (i+1))));
                        if(!getBoard().isValidMove(turn)){
                            turn.removeLast();
                        }
                    }
                    if(turn.size() > 1){
                        validMoves.get(edge).add(turn);
                    }
                }
            }
        }
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();

       validMoves.entrySet().removeIf(entry -> entry.getValue().isEmpty());
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
                if(t.equals(tile)) {
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
    public void save(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_player(player_id, player_name, difficulty)
                                 VALUES (nextval('player_id_seq'),?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setString(1,getName());
            ptsmt.setString(2,levelOfDifficulty.toString());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }
    }

}



