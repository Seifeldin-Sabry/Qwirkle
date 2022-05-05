package tests;

import qwirkle.model.Grid;
import qwirkle.model.Move;
import qwirkle.model.Tile;
import qwirkle.model.Turn;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static qwirkle.model.Grid.MID;

class GridTest {

    @org.junit.jupiter.api.Test
    void getTile() {
        Grid grid = new Grid();
        grid.setTile(0, 0, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        Tile expectedTIle = new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE);
        assertEquals(expectedTIle,grid.getTile(0,0));
    }

    @org.junit.jupiter.api.Test
    void getRowCount() {
        Grid grid = new Grid();
        int expected = 91;
        assertEquals(expected, grid.getRowCount());
    }

    @org.junit.jupiter.api.Test
    void getColumnCount() {
        Grid grid = new Grid();
        int expected = 91;
        assertEquals(expected, grid.getColumnCount());
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        Grid grid = new Grid();
        assertTrue(grid.isEmpty(MID,MID));
        assertTrue(grid.isEmpty(MID,44));
        assertTrue(grid.isEmpty(MID,43));
        assertTrue(grid.isEmpty(MID,42));
    }

    @org.junit.jupiter.api.Test
    void isNotEmpty() {
        Grid grid = new Grid();
        grid.setTile(0, 0, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        assertTrue(grid.isNotEmpty(0,0));
    }

    @org.junit.jupiter.api.Test
    void setTile() {
        Grid grid = new Grid();
        Tile before = grid.getTile(MID, MID);
        grid.setTile(MID, MID, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        Tile after = grid.getTile(MID,MID);
        assertNotEquals(before,after);
    }

    @org.junit.jupiter.api.Test
    void isValidMove() {
        Grid grid = new Grid();
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.EIGHT_POINT_STAR),new Move.Coordinate(MID,MID)));
        Move moveToInspect = new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(45,46));
        assertTrue(grid.isValidMove(moveToInspect));
    }

    @org.junit.jupiter.api.Test
    void isValidMoves() {
        Grid grid = new Grid();
        Move move1 = new Move(new Tile(Tile.TileColor.RED, Tile.TileShape.CIRCLE),new Move.Coordinate(MID,MID));
        Move move2 = new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(MID,MID + 1));
        assertTrue(grid.isValidMove(move1));
        grid.boardAddMove(move1);
        List<Move> moves = new ArrayList<>();
        moves.add(move1);
        moves.add(move2);
        assertTrue(grid.isValidMoves(moves));
    }

    @org.junit.jupiter.api.Test
    void determineDirection() {
        Grid grid = new Grid();
        Move move1 = new Move(new Tile(Tile.TileColor.RED, Tile.TileShape.CIRCLE),new Move.Coordinate(4,4));
        Move move2 = new Move(new Tile(Tile.TileColor.RED, Tile.TileShape.CIRCLE),new Move.Coordinate(4,5));
        Turn turn = new Turn();
        turn.add(move1);
        turn.add(move2);
        int directionExpectedTest1 = 0;
        assertEquals(directionExpectedTest1,grid.determineDirection(turn),0);
        int directionExpectedOneTile = -1;
        assertEquals(directionExpectedOneTile,grid.determineDirection(new Turn(List.of(move1))));
    }

    @org.junit.jupiter.api.Test
    void boardAddMove() {
        Grid grid = new Grid();
        Tile tileBefore = grid.getTile(MID,MID);
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE), new Move.Coordinate(MID,MID)));
        Tile after = grid.getTile(MID,MID);
        assertNotEquals(tileBefore,after);
    }

    @org.junit.jupiter.api.Test
    void getUsedSpaces() {
        Grid grid = new Grid();
        grid.setTile(0, 0, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        grid.setTile(0, 1, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        grid.setTile(0, 2, new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE));
        Set<Move> tiles = new HashSet<>();
        Tile expectedTIle = new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE);
        tiles.add(new Move(expectedTIle,new Move.Coordinate(0,0)));
        tiles.add(new Move(expectedTIle,new Move.Coordinate(0,1)));
        tiles.add(new Move(expectedTIle,new Move.Coordinate(0,2)));
        Set<Move> used = grid.getUsedSpaces();
        List<Move> sortedMoves = tiles
                        .stream()
                        .sorted(Comparator.comparingInt(coord -> coord.getCoordinate().getColumn()))
                        .toList();
        List<Move> sortedUsed = used
                .stream()
                .sorted(Comparator.comparingInt(coord -> coord.getCoordinate().getColumn()))
                .toList();
        assertEquals(sortedMoves,sortedUsed);
    }

    @org.junit.jupiter.api.Test
    void getAllEdges() {
        Grid grid = new Grid();
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,5)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,6)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,7)));
        Set<Move> tiles = new HashSet<>();
        tiles.add(new Move(null,new Move.Coordinate(5,4)));
        tiles.add(new Move(null,new Move.Coordinate(5,8)));
        tiles.add(new Move(null,new Move.Coordinate(6,5)));
        tiles.add(new Move(null,new Move.Coordinate(4,5)));
        tiles.add(new Move(null,new Move.Coordinate(4,6)));
        tiles.add(new Move(null,new Move.Coordinate(6,6)));
        tiles.add(new Move(null,new Move.Coordinate(6,7)));
        tiles.add(new Move(null,new Move.Coordinate(4,7)));
        Set<Move> used = grid.getAllEdges();
        List<Move> usedSorted = new ArrayList<>(used.stream().toList());
        Collections.sort(usedSorted);
        List<Move> myMoves = new ArrayList<>(tiles.stream().toList());
        Collections.sort(myMoves);
        assertEquals(myMoves,usedSorted);
    }

    @org.junit.jupiter.api.Test
    void getAllOccupiedEdges() {
        Grid grid = new Grid();
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,0)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,1)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,2)));
        Set<Move> tiles = new HashSet<>();
        Tile expectedTIle = new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE);
        tiles.add(new Move(expectedTIle,new Move.Coordinate(5,0)));
        tiles.add(new Move(expectedTIle,new Move.Coordinate(5,1)));
        tiles.add(new Move(expectedTIle,new Move.Coordinate(5,2)));
        Set<Move> used = grid.getAllOccupiedEdges();
        List<Move> sortedMoves = tiles
                .stream()
                .sorted(Comparator.comparingInt(coord -> coord.getCoordinate().getColumn()))
                .toList();
        List<Move> sortedUsed = used
                .stream()
                .sorted(Comparator.comparingInt(coord -> coord.getCoordinate().getColumn()))
                .toList();
        assertEquals(sortedMoves,sortedUsed);
    }

    //note for the last two methods, they get the arraylist of the row/column excluding the tile selected
    @org.junit.jupiter.api.Test
    void getConnectedVerticalArray() {
        Grid grid = new Grid();
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(4,0)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(3,0)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(2,0)));
        assertEquals(2, grid.getConnectedVerticalArray(new Move.Coordinate(4,0)).size());
    }

    @org.junit.jupiter.api.Test
    void getConnectedHorizontalArray() {
        Grid grid = new Grid();
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,0)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,1)));
        grid.boardAddMove(new Move(new Tile(Tile.TileColor.BLUE, Tile.TileShape.CIRCLE),new Move.Coordinate(5,2)));
        assertEquals(2, grid.getConnectedHorizontalArray(new Move.Coordinate(5,2)).size());
    }
}