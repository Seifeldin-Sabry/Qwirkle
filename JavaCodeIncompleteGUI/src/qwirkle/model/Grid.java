package qwirkle.model;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Grid extends GridPane {
    private static final int INITIAL_SIZE_GRID = 11;
    private final int BOARD_MID = 5;
    private final List<List<GridSquare>> grid;

    public Grid() {
        this.grid = new ArrayList<>();
        for (int row = 0; row < INITIAL_SIZE_GRID; row++) {
            grid.add(new ArrayList<>());
            for (int col = 0; col < INITIAL_SIZE_GRID ; col++) {
                GridSquare square = new GridSquare(row, col);
                grid.get(row).add(square);
                this.add(square,row,col);
            }
        }
        this.setAlignment(Pos.CENTER);
    }

    /**
     *
     * @return: A Set with max 4 elements <code>GridSquare</code>
     * which are neighbouring an occupied square
     */
    public HashSet<GridSquare> getEmptyNeighbouringSquares(){
        HashSet<GridSquare> emptySquares = new HashSet<>();
        for (List<GridSquare> row : grid) {
            for (GridSquare square:row) {
                int x = square.getXPos();
                int y = square.getYPos();

                if (!square.isEmpty()){
                    //null check is checking for edges cases
                    if (getGridSquare(x,y+1) != null && getGridSquare(x,y+1).isEmpty()){//top
                        emptySquares.add(new GridSquare(getGridSquare(x,y+1).getXPos(),getGridSquare(x,y+1).getYPos()));
                    }
                    if (getGridSquare(x,y-1) != null && getGridSquare(x,y+1).isEmpty()){//below
                        emptySquares.add(new GridSquare(getGridSquare(x,y+1).getXPos(),getGridSquare(x,y+1).getYPos()));
                    }
                    if (getGridSquare(x-1,y) != null && getGridSquare(x,y+1).isEmpty()){//left
                        emptySquares.add(new GridSquare(getGridSquare(x,y+1).getXPos(),getGridSquare(x,y+1).getYPos()));
                    }
                    if (getGridSquare(x+1,y) != null && getGridSquare(x,y+1).isEmpty()){//right
                        emptySquares.add(new GridSquare(getGridSquare(x,y+1).getXPos(),getGridSquare(x,y+1).getYPos()));
                    }
                }
            }
        }
        return emptySquares;
    }

    public GridSquare getGridSquare(int row, int col){
        return grid.get(row).get(col);
    }

    public List<List<GridSquare>> getGrid() {
        return grid;
    }
    
    public void updateGrid(){
        for (int i = 0; i < grid.size(); i++) {
            for (int j = 0; j < grid.size(); j++) {
                GridSquare sq = getGridSquare(i,j);
                if (i == 0 && sq.isEmpty()) {//changed for testing purposes
                    shiftIndexRow();
                    makeRowInTheBeginning();
                }
            }
        }
    }



    private void shiftIndexRow(){
        for (List<GridSquare> row : grid) {
            for (GridSquare square:row ) {
                square.setXPos(square.getXPos()+1);
            }
        }
    }

    public void getTileRow(GridSquare square){
        for (int row = 0; row < grid.size(); row++) {
                        
        }
    }





    private void shiftIndexColumn(){
        for (List<GridSquare> row : grid) {
            for (GridSquare square:row ) {
                square.setYPos(square.getYPos()+1);
            }
        }
    }

    private void makeRowInTheBeginning(){
        List<GridSquare> newRow = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            GridSquare square = new GridSquare(0, i);
            newRow.add(square);
            addRow(i,square);
        }
    }

    private void makeRowEnd(){
        List<GridSquare> newRow = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            newRow.add(new GridSquare(grid.size()-1,i));
        }
    }

//    public int countScore(Move move){
//
//    }







    


    
}

