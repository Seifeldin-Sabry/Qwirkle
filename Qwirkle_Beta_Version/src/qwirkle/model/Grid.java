package qwirkle.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Seifeldin Ismail
 */
public class Grid {
    public static final int BOARD_SIZE = 91;
    public static final int MID = 45;
    private static final int QWIRKLE = 6;
    private final Tile[][] grid;

    public Grid() {
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                setTile(row,col,null);
            }
        }

    }

    public Grid(Grid grid) {
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                setTile(row,col,grid.getTile(row,col));
            }
        }
    }

    public Grid getDeepCopy() {
        return new Grid(this);
    }

    public Tile getTile(int row, int col) {
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
            Move.Coordinate moveCoordinate = theMove.getCoordinate();

            if (isEmpty(MID,MID)){
                return  moveCoordinate.getRow() == MID
                        && moveCoordinate.getColumn() == MID;
            }

            //Tile already taken
            if (isNotEmpty(moveCoordinate.getRow(), moveCoordinate.getColumn())){
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

    public boolean isValidMove(Turn moveList) {
        int direction = this.determineDirection(moveList);
        switch (direction) {
            case 0 -> {
                //make sure the move is on the same row
                int currentCoordRow = moveList.get(0).getCoordinate().getRow();
                for (int coord:moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getRow).toList()) {
                    if (coord != currentCoordRow) {
                        return false;
                    }
                }

            }
            case 1 -> {
                //make sure the move is on the same column
                int currentCoordColumn = moveList.get(0).getCoordinate().getColumn();
                for (int coord:moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getColumn).toList()) {
                    if (coord != currentCoordColumn) {
                        return false;
                    }
                }
            }
            case -1 -> {
                return isValidMove(moveList.get(0));
            }

            case -100,-2 -> {
                return false;
            }
        }
//        direction = this.determineDirection(moveList);
        for (Move move:moveList) {
            if(isNotConnected(move,moveList))return false;
        }
        Grid grid = getDeepCopy();
        for (Move move:moveList) {
            int row = move.getCoordinate().getRow();
            int col = move.getCoordinate().getColumn();
            if (grid.isNotEmpty(row,col)) {
                grid.boardRemoveMove(move);
            }
        }
        for (Move move:moveList) {

            if (!grid.isValidMove(move)) {
                return false;
            }
            grid.boardAddMove(move);
        }

        return true;
    }

    private boolean isNotConnected(Move move, Turn moveList) {
        Move.Coordinate[] adjacents = move.getCoordinate().getAdjacentCoords();
        for (Move.Coordinate adjacent:adjacents) {
            Move move1 = new Move(getTile(adjacent.getRow(),adjacent.getColumn()),adjacent);
            if(move1.getTile()!=null && moveList.contains(move1))return true;
        }
        return false;
    }


    public boolean isValidMove(List<Move> moveList) {
        Turn turn = new Turn();
        turn.addAll(moveList);
        int direction = this.determineDirection(turn);
        switch (direction) {
            case 0 -> {
                //make sure the move is on the same row
                int currentCoordRow = moveList.get(0).getCoordinate().getRow();
                for (int coord:moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getRow).toList()) {
                    if (coord != currentCoordRow) {
                        return false;
                    }
                }

            }
            case 1 -> {
                //make sure the move is on the same column
                int currentCoordColumn = moveList.get(0).getCoordinate().getColumn();
                for (int coord:moveList.stream().map(Move::getCoordinate).map(Move.Coordinate::getColumn).toList()) {
                    if (coord != currentCoordColumn) {
                        return false;
                    }
                }
            }
            case -1 -> {
                return isValidMove(moveList.get(0));
            }

            case -100,-2 -> {
                return false;
            }
        }
        Grid grid = getDeepCopy();
        for (Move move:moveList) {
            int row = move.getCoordinate().getRow();
            int col = move.getCoordinate().getColumn();
            if (grid.isNotEmpty(row,col)) {
                grid.boardRemoveMove(move);
            }
        }
        for (Move move:moveList) {
            if (!grid.isValidMove(move)) {
                return false;
            }
            grid.boardAddMove(move);
        }
        return true;
    }

    private void boardRemoveMove(Move move) {
        setTile(move.getCoordinate().getRow(),move.getCoordinate().getColumn(),null);
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
            if (tileToCompare.isSameShape(tiles.get(0))
                    && tileToCompare.isSameColor(tiles.get(0))) {
                return false;
            }
            //if either color or shape is used then
            for (Tile tile : tiles) { //comparing every color or shape for matching condition
                //if same color and same shape
                if (tileToCompare.isSameColor(tile)
                        && tileToCompare.isSameShape(tile)) {
                    return false;
                }
                //if not same color and not the same shape
                if (!tileToCompare.isSameColor(tile) && !tileToCompare.isSameShape(tile)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return 0 if horizontal, 1 if vertical, -1 if 1 tile, -2 if no tiles, -100 if invalid(null)
     */
    public int determineDirection(Turn turn){
        switch (turn.size()){
            case 0 -> {
                return -2;
            }
            case 1 -> {
                return -1;
            }

        }
        Move move1 = turn.get(0);
        Move move2 = turn.get(1);
        if (move1.getCoordinate().getRow() == move2.getCoordinate().getRow()) return 0;
        if (move1.getCoordinate().getColumn() == move2.getCoordinate().getColumn()) return 1;
//        System.out.println("determineDirection: " + turn.size());
        //completely invalid move
        return -100;
    }


    private boolean isValidInHorizontalLine(Move move) {
        Move.Coordinate coordinate = move.getCoordinate();
        Tile tileToCompare = move.getTile();
        ArrayList<Tile> tiles = getConnectedHorizontalArray(coordinate);

        if (tiles.size() > 5) return false;
        if (!tiles.isEmpty()) {
            if (tileToCompare.isSameShape(tiles.get(0))
                    && tileToCompare.isSameColor(tiles.get(0))) {
                return false;
            }
            //if either color or shape is used then
            for (Tile tile : tiles) { //comparing every color or shape for matching condition
                //if same color and same shape
                if (tileToCompare.isSameColor(tile)
                        && tileToCompare.isSameShape(tile)) {
                    return false;
                }
                //if not same color and not the same shape
                if (!tileToCompare.isSameColor(tile) && !tileToCompare.isSameShape(tile)) {
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
    public void boardAddMove(Move move)  {
        if (move != null) {
            setTile(move.getCoordinate().getRow(),move.getCoordinate().getColumn(),move.getTile());
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
    public Set<Move> getAllEdges(){
        Set<Move> usedSpaces = getUsedSpaces();
        Set<Move> emptySpaces = new HashSet<>();
        for (Move move:usedSpaces) {
            for (int side = 0; side < 4; side++) {
                Move.Coordinate coordinate = move.getCoordinate().getAdjacentCoords()[side];
                int x = coordinate.getRow();
                int y = coordinate.getColumn();
                if (isEmpty(x, y)) emptySpaces.add(new Move(null,coordinate));
            }
        }
        return emptySpaces;
    }
    /**
     * @return A set of edges (all squares that have an empty spot next to them adjacent of a @nonnull tile)
     */
    public Set<Move> getAllOccupiedEdges(){
        Set<Move> usedSpaces = getUsedSpaces();
        Set<Move> edgeSpaces = new HashSet<>();
        for (Move move: usedSpaces) {
            if (isAnEdge(move.getCoordinate().getRow(),move.getCoordinate().getColumn())) edgeSpaces.add(move);
        }
        return edgeSpaces;
    }

    private boolean isAnEdge(int row, int column) {
        if (isEmpty(row, column)) return false;
        Move move = new Move(getTile(row,column), new Move.Coordinate(row, column));
        for (int side = 0; side < 4; side++) {
            Move.Coordinate coordinate = move.getCoordinate().getAdjacentCoords()[side];
            if (isEmpty(coordinate.getRow(), coordinate.getColumn())) return true;
        }
        return false;
    }




    /**
     * @param coordinate coordinates of the tile to check for
     * @return all Tiles that are connected to the given coordinates coordinate
     *         vertically
     */
    ArrayList<Tile> getConnectedVerticalArray(Move.Coordinate coordinate) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int x = coordinate.getRow();
        int y = coordinate.getColumn();

        for (int i = 1; i < QWIRKLE; i++) {// testing for 5 NOT 6 because our tile might be the sixth
            Tile tile = null;
            try {
                tile = getTile(x + i, y);
            }catch (ArrayIndexOutOfBoundsException ignored){
            }
            if (tile == null) {
                break;
            }
            tiles.add(tile);
        }
        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = null;
            try {
                tile = getTile(x - i, y);
            }catch (ArrayIndexOutOfBoundsException ignored){
            }
            if (tile == null) {
                break;
            }
            tiles.add(tile);
        }
        return tiles;
    }

    /**
     * @param coordinate coordinates of the Tile to check if connected
     * @return all Tiles that are connected to the given coordinates c
     *         horizontal
     */
    public ArrayList<Tile> getConnectedHorizontalArray(Move.Coordinate coordinate) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int x = coordinate.getRow();
        int y = coordinate.getColumn();

        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = null;
            try {
                tile = getTile(x, y + i);
            }catch (ArrayIndexOutOfBoundsException ignored){
            }
            if (tile == null) {
                break;
            }
            else tiles.add(tile);
        }
        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = null;
            try {
                tile = getTile(x , y - i);
            }catch (ArrayIndexOutOfBoundsException ignored){
            }
            if (tile == null) {
                break;
            }
            else tiles.add(tile);
        }
        return tiles;
    }

}
