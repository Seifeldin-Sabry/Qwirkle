package qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

import static qwirkle.model.Grid.BOARD_SIZE;

/**
 * @author Seifeldin Ismail
 * Purpose: Store player Moves in board for future validation
 */
public class Move implements Comparable<Move>{

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


    public String toString() {
        if (tile == null) {return "null "+ Coordinate.toString();
        }
        return tile.toString() + " " + Coordinate.toString();
    }

    @Override
    public int compareTo(Move other) {
        int rows = getCoordinate().row - other.getCoordinate().row;
        int columns = getCoordinate().column - other.getCoordinate().column;
        if (rows == 0) {
            return columns;
        } else return rows;
    }

    /**
     * A helper class to provide adjacents, coordinates to a certian Move(a move contains Tile)
     */
    public static class Coordinate{

        private int row;
        private int column;

        /**
         * constructor for Coordinate, has a horizontal axis 'column' and a vertical axis value 'row'.
         * @param row
         * @param column
         */
        public Coordinate(int row, int column) {
            this.row = row;
            this.column = column;
        }


        public int getRow() {
            return row;
        }


        public int getColumn() {
            return column;
        }

        /**
         * creates an array of 4 coordinates adjacent to the given Coordinate.
         * Main use is to get Adjacent tiles and test for null values
         * @return Coordinate[]
         */
        public Move.Coordinate[] getAdjacentCoords() {
            Move.Coordinate[] coords = new Coordinate[4];
            //check top
            if (row != BOARD_SIZE) {
                coords[0] = new Coordinate(row + 1, column);
            }

            //check bottom
            if (row != 0) {
                coords[1] = new Coordinate(row - 1, column);
            }

            //check right
            if (column != BOARD_SIZE) {
                coords[2] = new Coordinate(row, column + 1);
            }

            //check left
            if (column != 0) {
                coords[3] = new Coordinate(row, column - 1);
            }
            return coords;

        }


        public String toString() {
            return row + " " + column;
        }
    }


    /**
     * Saving a move to database
     * @param turn_no
     * @param move_no
     */
    public void save(int turn_no, int move_no){
        try {
            Connection conn = Database.getInstance().getConnection();

            String sql = """
                         INSERT INTO int_move(playersession_id, turn_no, move_no, row, "column", tile_id)
                         VALUES (currval('playersession_id_seq'),?,?,?,?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setInt(1,turn_no);
            ptsmt.setInt(2,move_no);
            ptsmt.setInt(3,getCoordinate().getRow());
            ptsmt.setInt(4,getCoordinate().getColumn());
            ptsmt.setInt(5,tile.getTile_id());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_move");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) return false;
        boolean rowsEqual = Objects.equals(this.getCoordinate().getRow(), ((Move) other).getCoordinate().getRow());
        boolean columnsEqual = Objects.equals(this.getCoordinate().getColumn(), ((Move) other).getCoordinate().getColumn());
        return rowsEqual && columnsEqual;
    }
    @Override
    public int hashCode() {
        return Objects.hash(getCoordinate(), getTile());
    }
}