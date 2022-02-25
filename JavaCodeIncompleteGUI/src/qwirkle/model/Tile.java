package qwirkle.model;

import javafx.scene.shape.Rectangle;

public class Tile{
    public static final int TILE_SIZE = 50;
    private final TileIcon icon;
    private TileColor color;
    private TileShape shape;
    private Rectangle rectangle;

    private double x = 0;
    private double y = 0;

    public Tile(TileIcon icon) {
        this.icon = icon;
        rectangle = new Rectangle();
//        rectangle = icon.getImagePattern();
        setColor();
        setShape();
//        rectangle.setFitWidth(TILE_SIZE);
//        rectangle.setFitHeight(TILE_SIZE);
        rectangle.setWidth(TILE_SIZE);
        rectangle.setHeight(TILE_SIZE);
//        rectangle.setFill(icon.getImagePattern());
    }

    public TileColor getColor() {
        return color;
    }

    public TileShape getShape() {
        return shape;
    }

    private void setColor() {
        switch (icon.getColor()){
            case "p" -> color = TileColor.PURPLE;
            case "r" -> color = TileColor.RED;
            case "g" -> color = TileColor.GREEN;
            case "b" -> color = TileColor.BLUE;
            case "o" -> color = TileColor.ORANGE;
            case "y" -> color = TileColor.YELLOW;
        }
    }

    private void setShape() {
        switch (icon.getShape()){
            case "diamond" -> shape = TileShape.DIAMOND;
            case "clover" -> shape = TileShape.CLOVER;
            case "circle" -> shape = TileShape.CIRCLE;
            case "eight" -> shape = TileShape.EIGHT_POINT_STAR;
            case "four" -> shape = TileShape.FOUR_POINT_STAR;
            case "square" -> shape = TileShape.SQUARE;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
//    public ImageView getRectangle(){
//        return rectangle;
//    }

    public void draw(){
        rectangle.setTranslateX(x);
        rectangle.setTranslateY(y);
        rectangle.setWidth(TILE_SIZE);
//        rectangle.setFitHeight(TILE_SIZE);
//        rectangle.setFitWidth(TILE_SIZE);
    }

    public boolean isSameColor(Tile otherTile){
        return this.color == otherTile.getColor();
    }

    public boolean isSameShape(Tile otherTile){
        return this.shape == otherTile.getShape();
    }
    // necessary getters and setters

    // methods with business logic
}
