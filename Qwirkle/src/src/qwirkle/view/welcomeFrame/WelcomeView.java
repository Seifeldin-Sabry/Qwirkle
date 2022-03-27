package src.qwirkle.view.welcomeFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class WelcomeView extends BorderPane {

    private Button start;
    private Button quit;
    private Button statistics;
    private Button rules;
    private ImageView logo;
    private VBox vbox1;
    private VBox vbox2;
    private HBox hbox;
    private Image background;
    private Text credits;

    public WelcomeView() {

        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() {
        start = new Button("New game");
        quit = new Button("Quit");
        statistics = new Button("Statistics");
        rules = new Button("Rules");
        logo = new ImageView(new Image("/images/logoMAX.png"));
        vbox1 = new VBox(100);
        vbox2 = new VBox(100);
        hbox = new HBox(50);
        background = new Image("/images/tiles3D_min.png");
        credits = new Text(10, 10, "Developed by Seif Sabry, Sakis Stefanidis, Nathan Hagos - ACS101 - Group14 © 2022");
    }

    public void layoutNodes() {
        start.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        start.setPrefSize(220, 60);
        quit.setPrefSize(220, 60);
        quit.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        rules.setPrefSize(220, 60);
        rules.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        statistics.setPrefSize(220, 60);
        statistics.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        logo.setPreserveRatio(true);
        logo.setFitWidth(900);
        logo.setSmooth(true);
        logo.setCache(true);
        setAlignment(logo, Pos.TOP_CENTER);
        setMargin(logo, new Insets(80, 0, 0, 0));
        vbox1.getChildren().addAll(getStart(), getRules());
        vbox1.setFillWidth(true);
        vbox1.setPadding(new Insets(10, 80, 10, 10));
        vbox1.setAlignment(Pos.BASELINE_RIGHT);
        vbox2.getChildren().addAll(getStatistics(), getQuit());
        vbox2.setPadding(new Insets(10, 10, 10, 80));
        vbox2.setAlignment(Pos.BASELINE_LEFT);
        vbox2.setFillWidth(true);
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.setPadding(new Insets(200, 0, 0, 0));
        hbox.setFillHeight(true);
        setCenter(hbox);
        hbox.getChildren().addAll(vbox1, vbox2);
        setBottom(credits);
        credits.setStyle("-fx-font-size: 16;");
        setAlignment(credits, Pos.BASELINE_CENTER);
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setStyle("-fx-font-family: 'American Typewriter';");
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        setTop(logo);
        setCenter(hbox);
        setBottom(credits);
        BackgroundImage bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, false, Side.BOTTOM, 0,
                        false), new BackgroundSize(100, 100, true,
                true, false, true));
        setBackground(new Background(bgImage));

    }

    Button getStart() {
        return start;
    }

    Button getQuit() {
        return quit;
    }

    Button getStatistics() {
        return statistics;
    }

    Button getRules() {
        return rules;
    }
}
