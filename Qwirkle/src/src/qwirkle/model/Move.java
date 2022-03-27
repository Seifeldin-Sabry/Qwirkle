package src.qwirkle.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static qwirkle.model.Grid.BOARD_SIZE;

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

    public Move(){
       tile = null;
       Coordinate = null;
    }


    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public Coordinate getCoordinate() {
        return Coordinate;
    }

    public void save(Connection connection, int turn_no, int move_no){
        try {
            Connection conn = connection;
            String sql = """
                         INSERT INTO int_move(playersession_id, turn_no, move_no, x, y, tile_id)
                         VALUES (currval('playersession_id_seq'),?,?,?,?,?);
                         """;
            PreparedStatement ptsmt = connection.prepareStatement(sql);
            ptsmt.setInt(1,turn_no);
            ptsmt.setInt(2,move_no);
            ptsmt.setInt(3,getCoordinate().getX());
            ptsmt.setInt(4,getCoordinate().getY());
            ptsmt.setInt(5,tile.getTile_id());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_move");
        }
    }
    public String toString() {
        if (tile == null) {return "null "+ Coordinate.toString();
        }
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
            //check right
            if (horizontal != BOARD_SIZE) {
                coords[0] = new Coordinate(horizontal + 1, vertical);
            }

            //check left
            if (horizontal != 0) {
                coords[1] = new Coordinate(horizontal - 1, vertical);
            }

            //check top
            if (vertical != BOARD_SIZE) {
                coords[2] = new Coordinate(horizontal, vertical + 1);
            }

            //check bottom
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
