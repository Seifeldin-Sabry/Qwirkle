package qwirkle.model;

import java.util.*;

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
                    scanUp(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyDown) {
                    scanDown(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyLeft) {
                    scanLeft(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyRight) {
                    scanRight(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyUpOneAndOneRight){
                    //for loop for 1up then right example: |->>>
                    scanUpOneThenRight(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyUpOneAndOneLeft){
                    //for loop for 1up then left example: |->>>
                    scanUpOneThenLeft(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyLeftOneAndOneUp){
                    //for loop for 1left then up example: |->>>
                    scanLeftOneThenUp(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyLeftOneAndOneDown){
                    //for loop for 1left then down example: |->>>
                    scanLeftOneThenDown(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyRightAndOneUp){
                    //for loop for 1right then up example: |->>>
                    scanRightOneThenUp(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyRightOneAndOneDown){
                    //for loop for 1right then down example: |->>>
                    scanRightOneThenDown(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }


                if (isEmptyDownOneAndOneLeft){
                    //for loop for 1down then left example: |->>>
                    scanDownOneThenLeft(moves, combo, column, row, grid);
                    AddTurnIfNotEmpty(validMoves, moves, edge);
                }

                if (isEmptyDownOneAndOneRight){
                    //for loop for 1down then right example: |->>>
                    scanDownOneThenRight(moves, combo, column, row, grid);
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
        return toReturn;
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

    private void scanUp(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int rowNewMove;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanDown(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int rowNewMove;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanLeft(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int columnNewMove;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanUpOneThenRight(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int columnNewMove;
        int rowNewMove;
        boolean firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanUpOneThenLeft(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        boolean firstTileFlag;
        int rowNewMove;
        int columnNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanLeftOneThenUp(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        boolean firstTileFlag;
        int rowNewMove;
        int columnNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanLeftOneThenDown(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int columnNewMove;
        boolean firstTileFlag;
        int rowNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanRightOneThenUp(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        boolean firstTileFlag;
        int columnNewMove;
        int rowNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanRightOneThenDown(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int rowNewMove;
        boolean firstTileFlag;
        int columnNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanDownOneThenRight(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int rowNewMove;
        boolean firstTileFlag;
        int columnNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanDownOneThenLeft(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        boolean firstTileFlag;
        int columnNewMove;
        int rowNewMove;
        firstTileFlag = true;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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

    private void scanRight(ArrayList<Move> moves, List<Tile> combo, int column, int row, Grid grid) {
        int columnNewMove;
        Grid gridCopy = grid;
        gridCopy = gridCopy.getDeepCopy();
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
            tileColorCombination.add(tile);
            tileShapeCombination.add(tile);
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
}