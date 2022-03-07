package qwirkle.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class Board {
    public static final int BOARD_SIZE = 21;
    public static final int MID = 10;
    private static final int QWIRKLE = 6;
    private Tile[][] grid;
    private Set<Move> usedSpaces;

    public Board() {
        this.grid = new Tile[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColumnCount(); col++) {
                setTile(row,col,null);

            }
        }
        usedSpaces = new HashSet<>();
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


    public boolean validMove(Move theMove, List<Move> movesMade) {
        boolean answer = true;
        if (theMove != null && movesMade != null) {
            Move.Coordinate moveCoordinate = theMove.getCoordinate();
            boolean firstMove = !isNotEmpty(MID,MID); //check if empty (double negating)
            boolean oldY = true;
            boolean oldX = true;
            if (!firstMove) {
                for (Move move : movesMade) {
                    if (move.getCoordinate().getX() != moveCoordinate.getX()) {
                        oldX = false;
                    }
                    if (move.getCoordinate().getY() != moveCoordinate.getY()) {
                        oldY = false;
                    }
                }
                //means that the move is listed twice
                if (!oldX && !oldY) {
                    answer = false;
                }
                //Tile already taken
                if (isNotEmpty(moveCoordinate.getX(), moveCoordinate.getY())){
                    answer = false;
                }
                int adjacent = 0;
                for (int side = 0; side < 4; side++) {
                    Move.Coordinate c = moveCoordinate.getAdjacentCoords()[side];
                    if (isNotEmpty(c.getX(), c.getY())) {
                        adjacent++;
                    }
                }
                //Means that the Tile is placed on its own. For first move its fine
                // but this is checking after the firstMove
                if (adjacent == 0) {
                    answer = false;
                }
                //Checking if the tile is valid on the row and column
                if (!(isValidInVerticalLine(theMove) && isValidInHorizontalLine(theMove))) {
                    answer = false;
                }
            }
            //If the first move is not in the middle, then false
            else if (moveCoordinate.getX() != MID
                    || moveCoordinate.getY() != MID) {
                answer = false;
            }
        }
        return answer;
    }



    /**
     * Checks if the Move move is valid on the Y axis.
     *
     * @param move to validate
     * @return if the Move move is valid on the Y axis
     */
    private boolean isValidInVerticalLine(Move move) {
        Move.Coordinate c = move.getCoordinate();
        Tile tileToCompare = move.getTile();
        //Gets the tiles connected on the Y axis (Vertically)
        ArrayList<Tile> tiles = getConnectedYArray(c);
        boolean answer = true;
        if (!tiles.isEmpty()) {//if array is not empty
            boolean shapeRelation = (tileToCompare.isSameShape(tiles.get(0))); //checking if the same color is used
            boolean colorRelation = (tileToCompare.isSameColor(tiles.get(0))); //checking if the same color is used
            if (shapeRelation == colorRelation) {
                answer = false;
            }
            if (answer) { //if either color or shape is used then
                for (Tile tile : tiles) { //comparing every color or shape for matching tile
                    //if same color and same shape
                    if (tileToCompare.isSameColor(tile)
                            && tileToCompare.isSameShape(tile)) {
                        answer = false;
                        break;
                    }

                    //if not same color and not the same shape
                    if (!tileToCompare.isSameColor(tile) && !shapeRelation) {
                        answer = false;
                        break;
                    }

                    //if not same shape and not same color
                    if (!tileToCompare.isSameShape(tile) && !colorRelation) {
                        answer = false;
                        break;
                    }
                }
            }
        }
        return answer;
    }

    /**
     * Checks if the Move move is valid on the X axis.
     *
     * @param move to validate
     * @return if the Move move is valid on the X axis
     */
    private boolean isValidInHorizontalLine(Move move) {
        Move.Coordinate c = move.getCoordinate();
        Tile tileToCompare = move.getTile();
        //Tiles connected on the X axis (Horizontal)
        ArrayList<Tile> tiles = getConnectedXArray(c);
        boolean answer = true;
        if (!tiles.isEmpty()) {
            boolean shapeRelation = (tileToCompare.isSameShape(tiles.get(0))); //checking if the same color is used
            boolean colorRelation = (tileToCompare.isSameColor(tiles.get(0)));
            if (shapeRelation == colorRelation) {//if true both is invalid as game rules state, if both false then definitely invalid too
                answer = false;
            }
            if (answer) {
                for (Tile tile : tiles) {
                    if (tile.equals(tileToCompare)) {
                        answer = false;
                        break;
                    }
                    if (!tileToCompare.isSameColor(tile) && !shapeRelation) {
                        answer = false;
                        break;
                    }
                    if (!tileToCompare.isSameShape(tile) && !colorRelation) {
                        answer = false;
                        break;
                    }
                }
            }
        }
        return answer;
    }



    /**
     * Adds the Move to the board. The Tile in move will be placed on the
     * board at the Coordinate of move.
     *
     * @param move to add to grid
     */
    public void boardAddMove(Move move)  {
        if (move != null) {
            setTile(move.getCoordinate().getX(),move.getCoordinate().getY(),move.getTile());
            this.usedSpaces = getUsedSpaces();
        }
    }

    /**
     * Adds multiple Moves to the board by executing the boardAddMove (for one
     * Move) method for each of the moves inside the set move.
     *
     * @param moves
     */
    public void boardAddMove(Set<Move> moves){
        for (Move move : moves) {
            boardAddMove(move);
        }
    }

    /**
     * @param coordinate for resetting a tile to null
     */
    public void boardRemove(Move.Coordinate coordinate) {
        setTile(coordinate.getX(),coordinate.getY(),null);
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


    public Tile[][] getGrid() {
        return grid;
    }

    /**
     * @param c coordinates of the tile to check for
     * @return all Tiles that are connected to the given coordinates c
     *         horizontally
     */
    private ArrayList<Tile> getConnectedXArray(Move.Coordinate c) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int x = c.getX();
        int y = c.getY();

        for (int i = 1; i < QWIRKLE; i++) {// testing for 5 NOT 6 because our tile might be the sixth
            Tile tile = getTile(x + i,y);
            if (tile == null) {
                break;
            }
            tiles.add(tile);
        }
        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = getTile(x - i, y);
            if (tile == null) {
                break;
            }
             tiles.add(tile);
        }
        return tiles;
    }

    /**
     * @param c coordinates of the Tile to check if connected
     * @return all Tiles that are connected to the given coordinates c
     *         vertically
     */
    private ArrayList<Tile> getConnectedYArray(Move.Coordinate c) {
        ArrayList<Tile> tiles = new ArrayList<>();
        int x = c.getX();
        int y = c.getY();

        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = getTile(x,y + i);
            if (tile == null) {
                break;
            }
            else tiles.add(tile);
        }
        for (int i = 1; i < QWIRKLE; i++) {
            Tile tile = getTile(x,y - i);
            if (tile == null) {
                break;
            }
            else tiles.add(tile);
        }
        return tiles;
    }

}
