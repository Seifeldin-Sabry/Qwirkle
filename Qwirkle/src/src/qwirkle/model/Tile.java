package src.qwirkle.model;


import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.Objects;


/**
 * Tile class stores information about a game tile
 * Modelled for the game Qwirkle
 * Purpose: Storing information about a Qwirkle Tile
 * @author Seifeldin Sabry
 */
public class Tile {
    private final TileColor color;
    private final TileShape shape;
    private TileIcon icon;
    private int tile_id;



    public Tile(TileColor color, TileShape shape, int tile_id) {
        this(color,shape);
        this.tile_id = tile_id;
        setIcon();
    }

    public Tile(TileColor color, TileShape shape) {
        this.color = color;
        this.shape = shape;
        setIcon();
    }
    private void setIcon() {
        StringBuilder sb = new StringBuilder();
        switch (color){
            case YELLOW -> sb.append("yellow");
            case RED -> sb.append("red");
            case BLUE -> sb.append("blue");
            case GREEN -> sb.append("green");
            case PURPLE -> sb.append("purple");
            case ORANGE -> sb.append("orange");
        }
        sb.append("_");
        switch (shape){
            case EIGHT_POINT_STAR -> sb.append("eight_points_star");
            case FOUR_POINT_STAR -> sb.append("four_points_star");
            case CIRCLE -> sb.append("circle");
            case SQUARE -> sb.append("square");
            case CLOVER -> sb.append("clover");
            case DIAMOND -> sb.append("diamond");
        }
        icon = TileIcon.valueOf(sb.toString());
    }

    public int getTile_id() {
        return tile_id;
    }

    public ImagePattern getIconImage() {
        return icon.getImagePattern();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return getColor() == tile.getColor() && getShape() == tile.getShape();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getColor(), getShape());
    }

    @Override
    public String toString() {
        return color.name() + " " +shape.name();
    }

    public enum TileColor {
        RED,
        GREEN,
        BLUE,
        PURPLE,
        ORANGE,
        YELLOW;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public enum TileIcon {
        red_clover("/icons/red_clover.png"),
        red_circle("/icons/red_circle.png"),
        red_square("/icons/red_square.png"),
        red_four_points_star("/icons/red_four_points_star.png"),
        red_eight_points_star("/icons/red_eight_points_star.png"),
        red_diamond("/icons/red_diamond.png"),
        green_clover("/icons/green_clover.png"),
        green_circle("/icons/green_circle.png"),
        green_square("/icons/green_square.png"),
        green_four_points_star("/icons/green_four_points_star.png"),
        green_eight_points_star("/icons/green_eight_points_star.png"),
        green_diamond("/icons/green_diamond.png"),
        yellow_clover("/icons/yellow_clover.png"),
        yellow_circle("/icons/yellow_circle.png"),
        yellow_square("/icons/yellow_square.png"),
        yellow_four_points_star("/icons/yellow_four_points_star.png"),
        yellow_eight_points_star("/icons/yellow_eight_points_star.png"),
        yellow_diamond("/icons/yellow_diamond.png"),
        orange_clover("/icons/orange_clover.png"),
        orange_circle("/icons/orange_circle.png"),
        orange_square("/icons/orange_square.png"),
        orange_four_points_star("/icons/orange_four_points_star.png"),
        orange_eight_points_star("/icons/orange_eight_points_star.png"),
        orange_diamond("/icons/orange_diamond.png"),
        blue_clover("/icons/blue_clover.png"),
        blue_circle("/icons/blue_circle.png"),
        blue_square("/icons/blue_square.png"),
        blue_four_points_star("/icons/blue_four_points_star.png"),
        blue_eight_points_star("/icons/blue_eight_points_star.png"),
        blue_diamond("/icons/blue_diamond.png"),
        purple_clover("/icons/purple_clover.png"),
        purple_circle("/icons/purple_circle.png"),
        purple_square("/icons/purple_square.png"),
        purple_four_points_star("/icons/purple_four_points_star.png"),
        purple_eight_points_star("/icons/purple_eight_points_star.png"),
        purple_diamond("/icons/purple_diamond.png");

        final String tileLocation;

        TileIcon(String tileLocation) {
            this.tileLocation = tileLocation;
        }

        public ImagePattern getImagePattern() {
            Image img = new Image(tileLocation);
            return new ImagePattern(img);
        }

    }

    public enum TileShape {
        CLOVER,
        FOUR_POINT_STAR,
        EIGHT_POINT_STAR,
        SQUARE,
        CIRCLE,
        DIAMOND;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }
}

    
