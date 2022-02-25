package qwirkle.model;

public enum TileColor {
    RED(0xFF0000),
    GREEN(0x008000),
    BLUE(0x0000FF),
    PURPLE(0x7F00FF),
    ORANGE(0xFF9933),
    YELLOW(0xFFFF00);

    /**
     * Empty Tile
     */
    private final int RGB;

    TileColor( int RGB) {
        this.RGB = RGB;
    }

    public String getRGBHex() {
        return Integer.toHexString(RGB);
    }
}
