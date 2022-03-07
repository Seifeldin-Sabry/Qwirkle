package qwirkle.model;


import javafx.scene.paint.Color;

public enum TileColor {
    RED(Color.RED),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE),
    PURPLE(Color.GRAY),
    ORANGE(Color.ORANGE),
    YELLOW(Color.YELLOW);

    private final Color color;

    TileColor( Color color) {
        this.color = color;
    }


}
