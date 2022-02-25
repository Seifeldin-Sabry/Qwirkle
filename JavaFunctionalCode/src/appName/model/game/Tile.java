package appName.model.game;


public class Tile {
    enum TileIcon {
        red_clover("icons/red_clover.png"),
        red_circle("icons/red_circle.png"),
        red_square("icons/red_square.png"),
        red_four_points_star("icons/red_four_points_star.png"),
        red_eight_points_star("icons/red_eight_points_star.png"),
        red_diamond("icons/red_diamond.png"),
        green_clover("icons/green_clover.png"),
        green_circle("icons/green_circle.png"),
        green_square("icons/green_square.png"),
        green_four_points_star("icons/green_four_points_star.png"),
        green_eight_points_star("icons/green_eight_points_star.png"),
        green_diamond("icons/green_diamond.png"),
        yellow_clover("icons/yellow_clover.png"),
        yellow_circle("icons/yellow_circle.png"),
        yellow_square("icons/yellow_square.png"),
        yellow_four_points_star("icons/yellow_four_points_star.png"),
        yellow_eight_points_star("icons/yellow_eight_points_star.png"),
        yellow_diamond("icons/yellow_diamond.png"),
        orange_clover("icons/orange_clover.png"),
        orange_circle("icons/orange_circle.png"),
        orange_square("icons/orange_square.png"),
        orange_four_points_star("icons/orange_four_points_star.png"),
        orange_eight_points_star("icons/orange_eight_points_star.png"),
        orange_diamond("icons/orange_diamond.png"),
        blue_clover("icons/blue_clover.png"),
        blue_circle("icons/minified/blue_circle.png"),
        blue_square("icons/minified/blue_square.png"),
        blue_four_points_star("icons/blue_four_points_star.png"),
        blue_eight_points_star("icons/blue_eight_points_star.png"),
        blue_diamond("icons/blue_diamond.png"),
        purple_clover("icons/purple_clover.png"),
        purple_circle("icons/purple_circle.png"),
        purple_square("icons/minified/purple_square.png"),
        purple_four_points_star("icons/purple_four_points_star.png"),
        purple_eight_points_star("icons/purple_eight_points_star.png"),
        purple_diamond("icons/purple_diamond.png");

        private final String filePath;

        TileIcon(String filePath) {
            this.filePath = filePath;
        }

        public String getFilePath() {
            return this.filePath;
        }

        @Override
        public String toString() {
            return getFilePath();
        }

    }
    enum TileColor{
        RED,
        GREEN,
        YELLOW,
        ORANGE,
        BLUE,
        PURPLE;
    }
    enum TileShape{
        CLOVER,
        CIRCLE,
        SQUARE,
        FOUR_POINTS_STAR,
        EIGHT_POINTS_STAR,
        DIAMOND;
    }

    private String keyName;
    private TileIcon iconPath;
    private TileColor color;
    private TileShape shape;
    private int x;
    private int y;

    public Tile(){

    }
    //Constructor
    public Tile(String keyname, TileIcon icon, TileColor color, TileShape shape) {
        this.keyName = keyname;
        this.iconPath = icon;
        this.color = color;
        this.shape = shape;
    }

    //Getters & Setters

    public String getKeyName() {
        return keyName;
    }

    public TileIcon getIconPath() {
        return iconPath;
    }

    public TileColor getColor(){
        return this.color;
    }

    public TileShape getShape(){
        return this.shape;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setIconPath(TileIcon iconPath) {
        this.iconPath = iconPath;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    //Methods

    @Override
    public String toString() {
        return String.format("tile: %s, file: %s\n", getKeyName(), getIconPath());
    }

}
