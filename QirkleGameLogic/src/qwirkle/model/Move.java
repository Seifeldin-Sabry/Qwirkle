package qwirkle.model;

import static qwirkle.model.Board.BOARD_SIZE;

/**
 * @author Seifeldin Ismail
 * Purpose: Store player Moves in board for future validation
 */
public class Move {

    private Tile tile;
    private Coordinate Coordinate;

    /**
     * constructor for Move, which requires the Tile and the Coordinate of the Move.
     * @param tile
     * @param Coordinate
     */
    public Move(Tile tile, Coordinate Coordinate) {
        this.tile = tile;
        this.Coordinate = Coordinate;
    }


    public Tile getTile() {
        return tile;
    }


    public Coordinate getCoordinate() {
        return Coordinate;
    }


    public String toString() {
        return tile.toString() + " " + Coordinate.toString();
    }

    /**
     * A helper class to provide adjacents, coordinates to a certian Move(a move contains Tile)
     */
    public static class Coordinate {

        private int horizontal;
        private int vertical;

        /**
         * constructor for Coordinate, has a horizontal axis 'x' and a vertical axis value 'y'.
         * @param x
         * @param y
         */
        public Coordinate(int x, int y) {
            horizontal = x;
            vertical = y;
        }


        public int getX() {
            return horizontal;
        }


        public int getY() {
            return vertical;
        }

        /**
         * creates an array of 4 coordinates adjacent to the given Coordinate.
         * Main use is to get Adjacent tiles and test for null values
         * @return Coordinate[]
         */
        public Move.Coordinate[] getAdjacentCoords() {
            Move.Coordinate[] coords = new Coordinate[4];
            if (horizontal != BOARD_SIZE) {
                coords[0] = new Coordinate(horizontal + 1, vertical);
            }
            if (horizontal != 0) {
                coords[1] = new Coordinate(horizontal - 1, vertical);
            }
            if (vertical != BOARD_SIZE) {
                coords[2] = new Coordinate(horizontal, vertical + 1);
            }
            if (vertical != 0) {
                coords[3] = new Coordinate(horizontal, vertical - 1);
            }
            return coords;

        }


        public String toString() {
            return horizontal + " " + vertical;
        }
    }
}
