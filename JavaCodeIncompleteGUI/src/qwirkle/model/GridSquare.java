package qwirkle.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Seifeldin Ismail
 */

//We are working on a work around for this class
//Trying to only make objects of type Tile to be used in the grid
//So proper indexing is required and better constructor in the Tile class
//Working on it
public class GridSquare extends Rectangle {
    private int squareSize = 50;
    private int xPos;
    private int yPos;
    private Tile tile;

    public GridSquare(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.tile = null;
        setWidth(squareSize);
        setHeight(squareSize);
        setStroke(Color.BLACK);
        setFill(Color.WHITE);
    }
    


    public boolean isEmpty(){
        return tile == null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSquareSize() {
        return squareSize;
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
    }


}
