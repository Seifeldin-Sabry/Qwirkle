package qwirkle.model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public enum TileIcon {
    red_clover("file:images/red_clover.png"),
    red_circle("file:images/red_circle.png"),
    red_square("file:images/red_square.png"),
    red_four_points_star("file:images/red_four_points_star.png"),
    red_eight_points_star("file:images/red_eight_points_star.png"),
    red_diamond("file:images/red_diamond.png"),
    green_clover("file:images/green_clover.png"),
    green_circle("file:images/green_circle.png"),
    green_square("file:images/green_square.png"),
    green_four_points_star("file:images/green_four_points_star.png"),
    green_eight_points_star("file:images/green_eight_points_star.png"),
    green_diamond("file:images/green_diamond.png"),
    yellow_clover("file:images/yellow_clover.png"),
    yellow_circle("file:images/yellow_circle.png"),
    yellow_square("file:images/yellow_square.png"),
    yellow_four_points_star("file:images/yellow_four_points_star.png"),
    yellow_eight_points_star("file:images/yellow_eight_points_star.png"),
    yellow_diamond("file:images/yellow_diamond.png"),
    orange_clover("file:images/orange_clover.png"),
    orange_circle("file:images/orange_circle.png"),
    orange_square("file:images/orange_square.png"),
    orange_four_points_star("file:images/orange_four_points_star.png"),
    orange_eight_points_star("file:images/orange_eight_points_star.png"),
    orange_diamond("file:images/orange_diamond.png"),
    blue_clover("file:images/blue_clover.png"),
    blue_circle("file:images/blue_circle.png"),
    blue_square("file:images/blue_square.png"),
    blue_four_points_star("file:images/blue_four_points_star.png"),
    blue_eight_points_star("file:images/blue_eight_points_star.png"),
    blue_diamond("file:images/blue_diamond.png"),
    purple_clover("file:images/purple_clover.png"),
    purple_circle("file:images/purple_circle.png"),
    purple_square("file:images/purple_square.png"),
    purple_four_points_star("file:images/purple_four_points_star.png"),
    purple_eight_points_star("file:images/purple_eight_points_star.png"),
    purple_diamond("file:images/purple_diamond.png");

    String tileLocation;

    TileIcon(String tileLocation) {
        this.tileLocation = tileLocation;
    }

    public ImagePattern getImagePattern() {
        Image img = new Image(tileLocation,50,50,true,false);
        return new ImagePattern(img);
    }

    public String getColor(){
        return String.format("%s",tileLocation.charAt(7));
    }

    public String getShape(){
        String[] filename = tileLocation.split("_");
        return String.format("%s",filename[1].toLowerCase());
    }
}
