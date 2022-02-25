package qwirkle.model;

public enum PlayerColor {
    RED(0xFF0000),
    BLUE(0x0000FF);

    private final int RGB;

    PlayerColor(int RGB) {
        this.RGB = RGB;
    }

    @Override
    public String toString() {
        return Integer.toHexString(RGB);
    }
}
