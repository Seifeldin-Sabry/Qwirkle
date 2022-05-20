package qwirkle.view.introFrame;


import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class IntroView extends BorderPane {

    private ImageView logo;
    private Label credits;
    private VBox vbox;

    public IntroView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        logo = new ImageView(new Image("/images/logoMAX.png"));
        credits = new Label("Developed by Seifeldin Sabry, Sakis Stefanidis, Nathan Hagos - ACS101 - Group14 © 2022");
        vbox = new VBox();
    }

    private void layoutNodes() {
        logo.setFitWidth(900);
        logo.setPreserveRatio(true);
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setTranslateY(logo.getY()-50);
        credits.setStyle("-fx-font-family: 'American Typewriter'; -fx-font-size: 22; -fx-text-fill: #ffffff;" +
                "-fx-font-weight: bold; -fx-effect: dropshadow( gaussian , rgb(0,0,0) , 5,0,10,10 );");
        vbox.getChildren().addAll(logo,credits);
        vbox.setAlignment(Pos.BASELINE_CENTER);
        setStyle("-fx-background-color: transparent; -fx-background-radius: 10;");
        setCenter(vbox);
        setAlignment(vbox, Pos.BOTTOM_CENTER);
    }
}
