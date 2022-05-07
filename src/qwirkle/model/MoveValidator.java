package qwirkle.model;

import java.util.*;

import static qwirkle.model.MoveValidator.Direction.*;

/**
 * @author: Seifeldin Sabry
 */
public class MoveValidator {
    private final Deck deck;

    public MoveValidator(Deck deck) {
        this.deck = deck;
    }

    public HashMap<Move, Set<Turn>> getAllValidMoves(Grid grid) {

        HashMap<Move, Set<Turn>> validMoves = new HashMap<>();
        List<ArrayList<Tile>> allCombinations = getAllPossibleCombinationAndPermutations();
        Set<Move> edges = grid.getAllOccupiedEdges();

        ArrayList<Move> moves = new ArrayList<>();

        for (Move edge : edges) {
            validMoves.computeIfAbsent(edge, k -> new HashSet<>());
            Move.Coordinate coordinate = edge.getCoordinate();
            for (ArrayList<Tile> combo : allCombinations) {
                int column = coordinate.getColumn();
                int row = coordinate.getRow();

                boolean isEmptyUp = grid.isEmpty(row - 1, column);
                boolean isEmptyDown = grid.isEmpty(row + 1, column);
                boolean isEmptyLeft = grid.isEmpty(row, column - 1);
                boolean isEmptyRight = grid.isEmpty(row - 1, column + 1);

                boolean isEmptyUpOneAndOneRight = isEmptyUp && grid.isEmpty(row - 1, column + 1);
                boolean isEmptyUpOneAndOneLeft = isEmptyUp && grid.isEmpty(row - 1, column - 1);

                boolean isEmptyRightAndOneUp = isEmptyRight && grid.isEmpty(row + 1, column + 1);
                boolean isEmptyRightOneAndOneDown = isEmptyRight && grid.isEmpty(row - 1, column + 1);

                boolean isEmptyLeftOneAndOneDown = isEmptyLeft && grid.isEmpty(row + 1, column - 1);
                boolean isEmptyLeftOneAndOneUp = isEmptyLeft && grid.isEmpty(row - 1, column - 1);

                boolean isEmptyDownOneAndOneLeft = isEmptyDown && grid.isEmpty(row + 1, column - 1);
                boolean isEmptyDownOneAndOneRight = isEmptyDown && grid.isEmpty(row + 1, column + 1);

                if (isEmptyUp) {
                    scan(moves, combo, column, row, grid, UP);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyDown) {
                    scan(moves,combo,column,row,grid,DOWN);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyLeft) {
                    scan(moves,combo,column,row,grid,LEFT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyRight) {
                    scan(moves,combo,column,row,grid,RIGHT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyUpOneAndOneRight){
                    //for loop for 1up then right example: |->>>
                    scan(moves,combo,column,row,grid,UPRIGHT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyUpOneAndOneLeft){
                    //for loop for 1up then left example: |->>>
                    scan(moves,combo,column,row,grid,UPLEFT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyLeftOneAndOneUp){
                    //for loop for 1left then up example: |->>>
                    scan(moves,combo,column,row,grid,LEFTUP);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyLeftOneAndOneDown){
                    //for loop for 1left then down example: |->>>
                    scan(moves,combo,column,row,grid,LEFTDOWN);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyRightAndOneUp){
                    //for loop for 1right then up example: |->>>
                    scan(moves,combo,column,row,grid,RIGHTUP);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyRightOneAndOneDown){
                    //for loop for 1right then down example: |->>>
                    scan(moves,combo,column,row,grid,RIGHTDOWN);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyDownOneAndOneLeft){
                    //for loop for 1down then left example: |->>>
                    scan(moves,combo,column,row,grid,DOWNLEFT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyDownOneAndOneRight){
                    //for loop for 1down then right example: |->>>
                    scan(moves,combo,column,row,grid,DOWNRIGHT);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
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
                if (turn.size() >= 1) {
                    toAdd.add(turn);
                }
            }
            toReturn.put(entry.getKey(), toAdd);
        }
        toReturn = removeEmptyTurns(toReturn);
        toReturn = removeDuplicateTurns(toReturn);

        return toReturn;
    }

    private HashMap<Move, Set<Turn>> removeDuplicateTurns(HashMap<Move, Set<Turn>> toReturn) {
        HashMap<Move, Set<Turn>> toReturn2 = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : toReturn.entrySet()) {
            Set<Turn> turns = entry.getValue();
            Set<Turn> toAdd = new HashSet<>();
            for (Turn turn : turns) {
                if (!toAdd.contains(turn)) {
                    toAdd.add(turn);
                }
            }
            toReturn2.put(entry.getKey(), toAdd);
        }
        return toReturn2;
    }

    private HashMap<Move, Set<Turn>> removeEmptyTurns(HashMap<Move, Set<Turn>> validMoves) {
        HashMap<Move, Set<Turn>> toReturn = new HashMap<>();
        for (Map.Entry<Move, Set<Turn>> entry : validMoves.entrySet()) {
            Move move = entry.getKey();
            Set<Turn> turns = entry.getValue();
            Set<Turn> toAdd = new HashSet<>();
            if (turns.isEmpty()) {
                continue;
            }
            for (Turn turn : turns) {
                if (turn.size() >= 1) {
                    toAdd.add(turn);
                }
            }
            toReturn.put(move, toAdd);
        }
        return toReturn;
    }

    private void AddTurnIfNotEmpty(HashMap<Move, Set<Turn>> validMoves, ArrayList<Move> moves, Move edge) {
        if (moves.size() > 0) {
            validMoves.get(edge).add(new Turn(moves));
            moves.clear();
        }
    }


    private void scan(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid, Direction direction){
        boolean firstTileFlag;
        int columnNewMove;
        int rowNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
        switch (direction){
            case UP -> {
                for (int i = 0; i < combo.size(); i++) {
                    rowNewMove = row - (i + 1);
                    Move.Coordinate newCoordinate = new Move.Coordinate(rowNewMove, column);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case UPRIGHT -> {
                for (int i = 0; i < combo.size(); i++) {
                    rowNewMove = row - 1;
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(rowNewMove, column);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    columnNewMove = column + (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case UPLEFT -> {
                for (int i = 0; i < combo.size(); i++) {
                    rowNewMove = row - 1;
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(rowNewMove, column);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    columnNewMove = column - (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case RIGHT -> {
                for (int i = 0; i < combo.size(); i++) {
                    columnNewMove = column + (i + 1);
                    Move.Coordinate newCoordinate = new Move.Coordinate(row, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case RIGHTDOWN -> {
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    columnNewMove = column + 1;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(row, columnNewMove);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    rowNewMove = row + (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case RIGHTUP -> {
                for (int i = 0; i < combo.size(); i++) {
                    columnNewMove = column + 1;
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(row, columnNewMove);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    rowNewMove = row - (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case DOWN -> {
                for (int i = 0; i < combo.size(); i++) {
                    rowNewMove = row + (i + 1);
                    Move.Coordinate newCoordinate = new Move.Coordinate(rowNewMove, column);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case DOWNRIGHT -> {
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    rowNewMove = row - 1;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(rowNewMove, column);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    columnNewMove = column + (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case DOWNLEFT -> {
                for (int i = 0; i < combo.size(); i++) {
                    Move.Coordinate newCoordinate;
                    rowNewMove = row + 1;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(rowNewMove, column);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, column)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    columnNewMove = column - (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case LEFT -> {
                for (int i = 0; i < combo.size(); i++) {
                    columnNewMove = column - (i + 1);
                    Move.Coordinate newCoordinate = new Move.Coordinate(row, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case LEFTDOWN -> {
                for (int i = 0; i < combo.size(); i++) {
                    columnNewMove = column - 1;
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(row, columnNewMove);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    rowNewMove = row + (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
            case LEFTUP -> {
                for (int i = 0; i < combo.size(); i++) {
                    columnNewMove = column - 1;
                    Move.Coordinate newCoordinate;
                    if (firstTileFlag) {
                        newCoordinate = new Move.Coordinate(row, columnNewMove);
                        Move move = new Move(combo.get(i), newCoordinate);
                        moves.add(move);
                        if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(row, columnNewMove)) {
                            moves.clear();
                            break;
                        }
                        firstTileFlag = false;
                        continue;
                    }
                    rowNewMove = row - (i);
                    //not (i+1) because the first iteration is already done
                    newCoordinate = new Move.Coordinate(rowNewMove, columnNewMove);
                    Move move = new Move(combo.get(i), newCoordinate);
                    moves.add(move);
                    if (!gridCopy.isValidMoves(moves) || gridCopy.isNotEmpty(rowNewMove, columnNewMove)) {
                        moves.clear();
                        break;
                    }
                }
            }
        }
    }

    private List<ArrayList<Tile>> getAllPossibleCombinationAndPermutations() {
        Set<ArrayList<Tile>> setOfCombinations = getLargestCombinations();
        List<ArrayList<Tile>> allCombinations = new ArrayList<>();
        for (ArrayList<Tile> combo : setOfCombinations) {
            allCombinations.addAll(getAllPermutations(combo));
        }
        return allCombinations;
    }


    /**
     * @return the largest combinations of tiles. this is used along with
     * <code>Method</code>> getAllCombinations and getAllPermutations to get all combos, and all orders of the combos
     */
    public Set<ArrayList<Tile>> getLargestCombinations() {
        Set<ArrayList<Tile>> allTileCombinations = new HashSet<>();
        Set<ArrayList<Tile>> toReturn = new HashSet<>();
        List<Tile> tilesInDeck = deck.getTilesInDeck();
        for (Tile tile : tilesInDeck) {
            ArrayList<Tile> tileShapeCombination = new ArrayList<>();
            ArrayList<Tile> tileColorCombination = new ArrayList<>();
            if(!tileColorCombination.contains(tile)){
                tileColorCombination.add(tile);
            }
            if(!tileShapeCombination.contains(tile)){
                tileShapeCombination.add(tile);
            }
            for (Tile t : tilesInDeck) {
                if (t.equals(tile)) {
                    continue;
                }
                if (t.isSameShape(tile) && !t.isSameColor(tile) && !tileShapeCombination.contains(t)) {
                    tileShapeCombination.add(t);
                    continue;
                }
                if (!t.isSameShape(tile) && t.isSameColor(tile) && !tileColorCombination.contains(t)) {
                    tileColorCombination.add(t);
                }
            }
            allTileCombinations.add(tileShapeCombination);
            allTileCombinations.add(tileColorCombination);
        }

        //after getting the highest tile combo
        //we need to make lists out of every combo into smaller ones
        //that way we can get all the combos
        for (ArrayList<Tile> list : allTileCombinations) {
            toReturn.addAll(getAllCombinations(list));
        }
        toReturn.removeIf(x -> x.size() == 0);

        return toReturn;
    }

    /**
     * This method returns all the possible combinations of a given list of tiles
     * uses the power of recursion
     *
     * @param list the list of tiles to get all the possible combinations of
     * @return a list of all the possible combinations of the given list of tiles
     */
    private Set<ArrayList<Tile>> getAllCombinations(ArrayList<Tile> list) {
        Set<ArrayList<Tile>> toReturn = new HashSet<>();
        if (list.isEmpty()) {
            toReturn.add(new ArrayList<>());
            return toReturn;
        }
        ArrayList<Tile> listCopy = new ArrayList<>(list);
        Tile head = listCopy.get(0);
        ArrayList<Tile> rest = new ArrayList<>(listCopy.subList(1, listCopy.size()));

        Set<ArrayList<Tile>> combosWithoutFirst = getAllCombinations(rest);

        for (ArrayList<Tile> set : combosWithoutFirst) {
            ArrayList<Tile> comboWithFirst = new ArrayList<>();
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
     * @param list the list of tiles to get all the possible permutations of
     * @return a list of all the possible orders of the given list of tiles
     */
    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public List<ArrayList<Tile>> getAllPermutations(ArrayList<Tile> list) {
        List<ArrayList<Tile>> toReturn = new ArrayList<>();
        if (list.size() == 1) {
            toReturn.add(list);
            return toReturn;
        }
        for (int i = 0; i < list.size(); i++) {
            Tile currentTile = list.get(i);
            ArrayList<Tile> remainingTiles = new ArrayList<>(list);
            remainingTiles.remove(i);
            List<ArrayList<Tile>> permutations = getAllPermutations(remainingTiles);
            for (ArrayList<Tile> permutation : permutations) {
                ArrayList<Tile> newList = new ArrayList<>();
                newList.add(currentTile);
                newList.addAll(permutation);
                toReturn.add(newList);
            }
        }
        return toReturn;
    }

    public enum Direction {
        UP
        ,RIGHT
        ,LEFT
        ,DOWN
        ,UPRIGHT,UPLEFT
        ,DOWNRIGHT, DOWNLEFT
        ,RIGHTUP,RIGHTDOWN
        ,LEFTUP,LEFTDOWN
        //YES THEY ARE ALL DIFFERENT!
    }
}
