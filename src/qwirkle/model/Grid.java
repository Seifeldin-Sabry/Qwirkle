package qwirkle.model;

import java.util.*;

import static qwirkle.model.Grid.MoveType.*;

/**
 * @author Seifeldin Ismail
 */
public class Grid {
    public static final int BOARD_SIZE = 91;
    public static final int MID = 45;
    private final Tile[][] grid;

    public Grid() {
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                setTile(row, col, null);
            }
        }

    }

    public Grid(Grid grid) {
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                setTile(row, col, grid.getTile(row, col));
            }
        }
    }

    public Grid getDeepCopy() {
        return new Grid(this);
    }

    public Tile getTile(int row, int col) {
        if (row >= BOARD_SIZE || col >= BOARD_SIZE || row < 0 || col < 0) return null;
        return grid[row][col];
    }


    public int getRowCount() {
        return grid.length;
    }

    public int getColumnCount() {
        return grid[0].length;
    }

    /**
     * @param row to look for
     * @param col to look for
     * @return if a Tile == null (empty)
     */
    public boolean isEmpty(int row, int col) {
        return grid[row][col] == null;
    }


    /**
     * Inverted version of isEmpty()
     *
     * @param row to look for
     * @param col to look for
     * @return if a Tile is not empty
     */
    public boolean isNotEmpty(int row, int col) {
        return grid[row][col] != null;
    }

    public void setTile(int row, int col, Tile fill) {
        grid[row][col] = fill;
    }


    public boolean isValidMove(Move theMove) {
        if (theMove != null && getUsedSpaces() != null) {
            if (isNotEmpty(theMove.getCoordinate().getRow(), theMove.getCoordinate().getColumn())) return false;
            Move.Coordinate moveCoordinate = theMove.getCoordinate();

            if (isEmpty(MID, MID)) {
                return moveCoordinate.getRow() == MID
                        && moveCoordinate.getColumn() == MID;
            }

            //Tile already taken
            if (isNotEmpty(moveCoordinate.getRow(), moveCoordinate.getColumn())) {
                return false;
            }
            int adjacent = 0;
            for (int side = 0; side < 4; side++) {
                Move.Coordinate adjacentCoord = moveCoordinate.getAdjacentCoords()[side];
                if (isNotEmpty(adjacentCoord.getRow(), adjacentCoord.getColumn())) {
                    adjacent++;
                }
            }
            //Means that the Tile is placed on its own. For first move its fine
            // but this is checking after the firstMove
            if (adjacent == 0) {
                return false;
            }
            //Checking if the tile is valid on the row and column
            return isValidInVerticalLine(theMove) && isValidInHorizontalLine(theMove);
        }
        return true;
    }

    public boolean isValidMoves(Turn moveList) {
        MoveType direction = this.determineDirection(moveList);
        switch (direction) {
            case HORIZONTAL -> {
                //make sure the move is on the same row
                int currentCoordRow = moveList.get(0).getCoordinate().getRow();
                for (int coord : moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getRow).toList()) {
                    if (coord != currentCoordRow) {
                        return false;
                    }
                    if (foundEmptyTiles(moveList)) {
                        return false;
                    }
                }

            }
            case VERTICAL -> {
                //make sure the move is on the same column
                int currentCoordColumn = moveList.get(0).getCoordinate().getColumn();
                for (int coord : moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getColumn).toList()) {
                    if (coord != currentCoordColumn) {
                        return false;
                    }
                    if (foundEmptyTiles(moveList)) {
                        return false;
                    }
                }
            }
            case SINGLE -> {
                if (foundEmptyTiles(moveList)) {
                    return false;
                }
                return isValidMove(moveList.get(0));
            }

            case INVALID -> {
                return false;
            }
        }
//        direction = this.determineDirection(moveList);
        for (Move move : moveList) {
            if (isNotConnected(move, moveList)) return false;
        }
        Grid grid = getDeepCopy();
        for (Move move : moveList) {
            int row = move.getCoordinate().getRow();
            int col = move.getCoordinate().getColumn();
            Tile tile = move.getTile();
            if (grid.isNotEmpty(row, col) && !tile.equals(grid.getTile(row, col))) return false;
            else {
                grid.boardRemoveMove(move);
            }
        }
        for (Move move : moveList) {

            if (!grid.isValidMove(move)) {
                return false;
            }
            grid.boardAddMove(move);
        }
        return true;
    }

    private boolean isNotConnected(Move move, Turn moveList) {
        boolean isConnected = false;
        Move.Coordinate[] moveAdjacents = move.getCoordinate().getAdjacentCoords();
        Move.Coordinate[]
                moveListCoordinates =
                moveList.stream().map(Move::getCoordinate).toArray(Move.Coordinate[]::new);
        for (Move.Coordinate moveAdjacent : moveAdjacents) {
            for (Move.Coordinate moveListCoordinate : moveListCoordinates) {
                isConnected = moveAdjacent.equals(moveListCoordinate);
            }
        }
        return isConnected;
    }


    public boolean isValidMoves(List<Move> moveList) {
        Turn turn = new Turn();
        turn.addAll(moveList);
        MoveType direction = this.determineDirection(turn);
        switch (direction) {
            case HORIZONTAL -> {
                //make sure the move is on the same row
                int currentCoordRow = moveList.get(0).getCoordinate().getRow();
                for (int coord : moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getRow).toList()) {
                    if (coord != currentCoordRow) {
                        return false;
                    }
                    if (foundEmptyTiles(turn)) {
                        return false;
                    }
                }

            }
            case VERTICAL -> {
                //make sure the move is on the same column
                int currentCoordColumn = moveList.get(0).getCoordinate().getColumn();
                for (int coord : moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getColumn).toList()) {
                    if (coord != currentCoordColumn) {
                        return false;
                    }
                    if (foundEmptyTiles(turn)) {
                        return false;
                    }
                }
            }
            case SINGLE -> {
                if (foundEmptyTiles(turn)) {
                    return false;
                }
                return isValidMove(moveList.get(0));
            }

            case INVALID -> {
                return false;
            }
        }
//        direction = this.determineDirection(moveList);
        for (Move move : moveList) {
            if (isNotConnected(move, turn)) return false;
        }
        Grid grid = getDeepCopy();
        for (Move move : moveList) {
            int row = move.getCoordinate().getRow();
            int col = move.getCoordinate().getColumn();
            if (grid.isNotEmpty(row, col)) {
                grid.boardRemoveMove(move);
            }
        }
        for (Move move : moveList) {

            if (!grid.isValidMove(move)) {
                return false;
            }
            grid.boardAddMove(move);
        }
        return true;
    }

    private void boardRemoveMove(Move move) {
        setTile(move.getCoordinate().getRow(), move.getCoordinate().getColumn(), null);
    }


    /**
     * Checks if the Move move is valid on the Y axis.
     *
     * @param move to validate
     * @return if the Move move is valid on the Y axis
     */
    private boolean isValidInVerticalLine(Move move) {
        Move.Coordinate coordinate = move.getCoordinate();
        Tile tileToCompare = move.getTile();
        //Gets the tiles connected on the Y axis (Vertically)
        ArrayList<Tile> tiles = getConnectedVerticalArray(coordinate);

        if (tiles.size() > 5) return false;
        if (!tiles.isEmpty()) {

            //if the tile to compare is not the same whether it is shape or color as the first tile in the list
            //then the move is invalid
            boolean sameShape = tiles.get(0).isSameShape(tileToCompare);
            boolean sameColor = tiles.get(0).isSameColor(tileToCompare);

            if (sameShape && sameColor) return false;

            //if either color or shape is used then
            for (Tile tile : tiles) { //comparing every color or shape for matching condition
                //if same color and same shape
                if (tileToCompare.isSameColor(tile) && sameShape) return false;

                //if not same color and not the same shape
                if (!tileToCompare.isSameColor(tile) && !sameShape) {
                    return false;
                }

                if (tileToCompare.isSameShape(tile) && sameColor) return false;

                if (!tileToCompare.isSameShape(tile) && !sameColor) {
                    return false;
                }

            }
        }
        return true;
    }

    /**
     * @return 0 if horizontal, 1 if vertical, -1 if 1 tile, -2 if no tiles, -100 if invalid(null)
     */
    public MoveType determineDirection(Turn turn) {
        switch (turn.size()) {
            case 0 -> {
                return INVALID;
            }
            case 1 -> {
                return SINGLE;
            }

        }
        Move move1 = turn.get(0);
        Move move2 = turn.get(1);
        if (move1.getCoordinate().getRow() == move2.getCoordinate().getRow()) return HORIZONTAL;
        if (move1.getCoordinate().getColumn() == move2.getCoordinate().getColumn()) return VERTICAL;
//        System.out.println("determineDirection: " + turn.size());
        //completely invalid move
        return INVALID;
    }


    private boolean isValidInHorizontalLine(Move move) {
        Move.Coordinate coordinate = move.getCoordinate();
        Tile tileToCompare = move.getTile();
        ArrayList<Tile> tiles = getConnectedHorizontalArray(coordinate);

        if (tiles.size() > 5) return false;
        if (!tiles.isEmpty()) {

            boolean sameShape = tiles.get(0).isSameShape(tileToCompare);
            boolean sameColor = tiles.get(0).isSameColor(tileToCompare);

            if (sameShape && sameColor) return false;
            //if either color or shape is used then
            for (Tile tile : tiles) { //comparing every color or shape for matching condition
                //if same color and same shape


                if (tileToCompare.isSameColor(tile) && sameShape) return false;

                //if not same color and not the same shape
                if (!tileToCompare.isSameColor(tile) && !sameShape) {
                    return false;
                }

                if (tileToCompare.isSameShape(tile) && sameColor) return false;

                if (!tileToCompare.isSameShape(tile) && !sameColor) {
                    return false;
                }

            }
        }
        return true;
    }


    /**
     * Adds the Move to the board. The Tile in move will be placed on the
     * board at the Coordinate of move.
     *
     * @param move to add to grid
     */
    public void boardAddMove(Move move) {
        if (move != null) {
            setTile(move.getCoordinate().getRow(), move.getCoordinate().getColumn(), move.getTile());
        }
    }

    /**
     * @return A set of moves that exist on the board
     */
    public Set<Move> getUsedSpaces() {
        Set<Move> result = new HashSet<>();
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (isNotEmpty(i, j)) {
                    result.add(new Move(grid[i][j], new Move.Coordinate(i, j)));
                }
            }
        }
        return result;
    }

    /**
     * @return A set of edges (all empty adjacent of a @nonnull tile)
     */
    public Set<Move> getAllEdges() {
        Set<Move> usedSpaces = getUsedSpaces();
        Set<Move> emptySpaces = new HashSet<>();
        for (Move move : usedSpaces) {
            for (int side = 0; side < 4; side++) {
                Move.Coordinate coordinate = move.getCoordinate().getAdjacentCoords()[side];
                int x = coordinate.getRow();
                int y = coordinate.getColumn();
                if (isEmpty(x, y)) emptySpaces.add(new Move(null, coordinate));
            }
        }
        return emptySpaces;
    }

    /**
     * @return A set of edges (all squares that have an empty spot next to them adjacent of a @nonnull tile)
     */
    public Set<Move> getAllOccupiedEdges() {
        Set<Move> usedSpaces = getUsedSpaces();
        Set<Move> edgeSpaces = new HashSet<>();
        for (Move move : usedSpaces) {
            if (isAnEdge(move.getCoordinate().getRow(), move.getCoordinate().getColumn())) edgeSpaces.add(move);
        }
        return edgeSpaces;
    }

    private boolean isAnEdge(int row, int column) {
        if (isEmpty(row, column)) return false;
        Move move = new Move(getTile(row, column), new Move.Coordinate(row, column));
        for (int side = 0; side < 4; side++) {
            Move.Coordinate coordinate = move.getCoordinate().getAdjacentCoords()[side];
            if (isEmpty(coordinate.getRow(), coordinate.getColumn())) return true;
        }
        return false;
    }


    /**
     * @param coordinate coordinates of the tile to check for
     * @return all Tiles that are connected to the given coordinates coordinate
     * vertically
     */
    public ArrayList<Tile> getConnectedVerticalArray(Move.Coordinate coordinate) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        int count = 1;
        while (true) {
            Tile tile = getTile(row + count, column);
            if (tile == null) {
                break;
            }
            tiles.add(tile);
            count++;
        }

        count = 1;
        while (true) {
            Tile tile = getTile(row - count, column);
            if (tile == null) {
                break;
            }
            tiles.add(tile);
            count++;
        }
        return tiles;
    }


    /**
     * @param coordinate coordinates of the Tile to check if connected
     * @return all Tiles that are connected to the given coordinates c
     * horizontal
     */
    public ArrayList<Tile> getConnectedHorizontalArray(Move.Coordinate coordinate) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        //Check horizontally first forward then backwards
        int count = 1;
        while (true) {
            Tile tile = getTile(row, column + count);
            if (tile == null) {
                break;
            }
            tiles.add(tile);
            count++;
        }

        count = 1;
        while (true) {
            Tile tile = getTile(row, column - count);
            if (tile == null) {
                break;
            }
            tiles.add(tile);
            count++;
        }
        return tiles;
    }


    //Checks for empty tiles if in between both vertically and horizontally in a moveList of a turn
    private boolean foundEmptyTiles(Turn turn) {
        LinkedList<Move> moves = new LinkedList<>(turn.getMoves());
        Collections.sort(moves);
        int firstMoveRow = moves.getFirst().getCoordinate().getRow();
        int lastMoveRow = moves.getLast().getCoordinate().getRow();
        int rowLength = firstMoveRow - lastMoveRow;
        rowLength = Math.abs(rowLength) + 1;
        int firstMoveColumn = moves.getFirst().getCoordinate().getColumn();
        int lastMoveColumn = moves.getLast().getCoordinate().getColumn();
        int columnLength = firstMoveColumn - lastMoveColumn;
        columnLength = Math.abs(columnLength) + 1;
        Grid grid = getDeepCopy();
        for (Move move : moves) {
            if (grid.isValidMove(move)) {
                grid.boardAddMove(move);
            }
        }
        //Horizontally per column
        int col = firstMoveColumn;
        for (; col < firstMoveColumn + columnLength; col++) {
            Tile tile = grid.getTile(firstMoveRow, col);
            if (tile == null) {
                return true;
            }
        }
        col = lastMoveColumn;
        for (; col > firstMoveColumn + columnLength; col--) {
            Tile tile = grid.getTile(firstMoveRow, col);
            if (tile == null) {
                return true;
            }
        }
        //Vertically per row
        int row = firstMoveRow;
        for (; row < firstMoveRow + rowLength; row++) {
            Tile tile = grid.getTile(row, firstMoveColumn);
            if (tile == null) {
                return true;
            }
        }
        row = lastMoveRow;
        for (; row > firstMoveRow + rowLength; row--) {
            Tile tile = grid.getTile(row, firstMoveColumn);
            if (tile == null) {
                return true;
            }
        }
        return false;
    }

    public enum MoveType {
        VERTICAL, SINGLE, HORIZONTAL, INVALID
    }

}
