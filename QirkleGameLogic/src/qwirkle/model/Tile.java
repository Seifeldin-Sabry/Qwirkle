package qwirkle.model;


/**
 * Tile class stores information about a game tile
 * Modelled for the game Qwirkle
 * Purpose: Storing information about a Qwirkle Tile
 * @author Seifeldin Sabry
 */
public class Tile {
    private final TileColor color;
    private final TileShape shape;



    public Tile(TileColor color, TileShape shape) {
        this.color = color;
        this.shape = shape;
    }



    public TileColor getColor() {
        return color;
    }

    public TileShape getShape() {
        return shape;
    }

    public boolean isSameColor(Tile otherTile){
        return color == otherTile.getColor();
    }

    public boolean isSameShape(Tile otherTile){
        return shape == otherTile.getShape();
    }

    @Override
    public String toString() {
        return color.name() + " " +shape.name();
    }

}

//    private void setColor() {
//        switch (icon.getColor()){
//            case "p" -> color = TileColor.PURPLE;
//            case "r" -> color = TileColor.RED;
//            case "g" -> color = TileColor.GREEN;
//            case "b" -> color = TileColor.BLUE;
//            case "o" -> color = TileColor.ORANGE;
//            case "y" -> color = TileColor.YELLOW;
//        }
//    }
//
//    private void setShape() {
//        switch (icon.getShape()){
//            case "diamond" -> shape = TileShape.DIAMOND;
//            case "clover" -> shape = TileShape.CLOVER;
//            case "circle" -> shape = TileShape.CIRCLE;
//            case "eight" -> shape = TileShape.EIGHT_POINT_STAR;
//            case "four" -> shape = TileShape.FOUR_POINT_STAR;
//            case "square" -> shape = TileShape.SQUARE;
//        }
//    }
