package src.qwirkle.view.rulesFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class RulesView extends BorderPane {
    //"Header"
    private ImageView logo;
    private Label vbTOP_title;
    private VBox vbTOP;
    //Container in the center;
    private HBox hbCenter;
    //First Column
    private VBox vbox1;
    private HBox hbvb1_1;
    private VBox vb1_1;
    private Label vb1_1_title;
    private Text vb1_1_paragraph;
    private Label vb1_2_title;
    private Text vb1_2_paragraph;
    private ImageView hbvb1_2_image;
    private Label vb1_3_title;
    private Text vb1_3_paragraph;
    private Label getVb1_4_title;
    private Text vb1_4_paragraph;
    private Label getVb1_5_title;
    private Text vb1_5_paragraph;
    private ImageView vb1_2_image;
    private Label vb1_2_IMGCaption;
    private HBox hbvb1_2;
    //Second Column
    private VBox vbox2;
    private Label vbox2_1_title;
    private Text vbox2_1_BulParagraph;
    private Text vbox2_1_paragraph;
    private Label vbox2_2_title;
    private Text vbox2_2_paragraph;
    private Text vbox2_3_paragraph;
    private HBox hbvb2;
    private ImageView hbvb2_image;
    private Label hbvb2_IMGCaption;
    //Third Column
    private VBox vbox3;
    private Text vbox3_1_paragraph;
    private HBox hbvb3_1;
    private ImageView hbvb3_1_image;
    private Label hbvb3_1_IMGCaption;
    private Text vbox3_2_3_paragraph;
    private Label vbox3_1_title;
    private Text vbox3_4_paragraph;
    private HBox hbvb3_2;
    private ImageView hbvb3_2_image;
    private Label hbvb3_2_IMGCaption;
    //Forth Column
    private VBox vbox4;
    private Text vbox4_1_paragraph;
    private HBox hbvb4;
    private ImageView hbvb4_image;
    private Label hbvb4_1_IMGCaption;
    private Text vbox4_2_paragraph;
    private ImageView vbox4_1_2_image;
    private Label vbox4_1_2_IMGCaption;
    private ImageView vbox4_2_1_image;
    private Label vbox4_2_1_IMGCaption;
    private Text vbox4_4_paragraph;
    private Label vbox4_1_title;
    private Text vbox4_5_paragraph;
    private Label vbox4_2_title;
    private Text vbox4_6_paragraph;
    //"Footer"
    private Button back;

    public RulesView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        //"Header"
        logo = new ImageView(new Image("/images/logoMAX.png"));
        vbTOP_title = new Label("Score and Win!");
        vbTOP = new VBox(10);
        //Container in the center;
        hbCenter = new HBox(20);
        //First Column
        vbox1 = new VBox(0);
        hbvb1_1 = new HBox(10);
        vb1_1 = new VBox(0);
        vb1_1_title = new Label("Components");
        vb1_1_paragraph = new Text("""
                108 tiles, 36 are unique
                tiles shown at right.
                1 bag
                2 players
                infinite combinations...""");
        vb1_2_title = new Label("Playing Time");
        vb1_2_paragraph = new Text("30 - 60 minutes(est.)");
        hbvb1_2_image = new ImageView(new Image("/images/vb1_1.png"));
        vb1_3_title = new Label("Object");
        vb1_3_paragraph = new Text("""
                Make lines of tiles that are either all one color or all
                one shape. Points are scored for every tile played.
                The player with the most points wins the game.""");
        getVb1_4_title = new Label("Setup");
        vb1_4_paragraph = new Text("Each game generates a random sequence of tiles.\n" +
                "Qwirkle does not use any boards.");
        getVb1_5_title = new Label("Starting the Game");
        vb1_5_paragraph = new Text("""
                Each player gets six tiles to create his or her hand.
                The player sees his tiles on the bottom of his screen.
                You may choose who starts first(You or the computer).
                Start playing by placing tiles that match per color or
                per shape. The tiles must be placed at one direction.""");
        vb1_2_image = new ImageView(new Image("/images/vb1_2.png"));
        vb1_2_IMGCaption = new Label("Example of opening move.");
        hbvb1_2 = new HBox(10);
        //Second Column
        vbox2 = new VBox(0);
        vbox2_1_title = new Label("Playing the Game");
        vbox2_1_BulParagraph = new Text("""
                On your turn:
                1. Place one or more tiles.
                2. Press submit when done.
                3. Random tiles will automatically fill
                   your bag to bring your hand back up to six.
                """);
        vbox2_1_paragraph = new Text("""
                If you can’t or don’t want to place tiles, you may discard
                may discard instead. This counts as your entire turn and
                you do not score any points. Set aside all the tiles you
                want to discard, then get the same number of random
                tiles as replacement tiles. You cannot discard more tiles
                than are in the bag.""");
        vbox2_2_title = new Label("Placing Tiles");
        vbox2_2_paragraph = new Text("""
                You may play multiple tiles on your turn as long as all
                tiles played are the same color or the same shape and are
                placed in the same line. You cannot play two tiles that
                are exactly the same.
                """);
        vbox2_3_paragraph = new Text("""
                At least one of the tiles you play must touch (side to side)
                a tile that has already been played and match the tile
                in color or shape. Any tiles that touch each other are
                part of a line. Lines are either all one all one color,
                 without any duplicates.""");
        hbvb2 = new HBox(10);
        hbvb2_image = new ImageView(new Image("/images/hbvb2.png"));
        hbvb2_IMGCaption = new Label("""
                A blue square and a
                yellow square can be
                added to the opening
                move to create a line of
                squares.""");
        //Third Column
        vbox3 = new VBox(0);
        vbox3_1_paragraph = new Text("The tiles you play must be added to the same line,\n" +
                "and they have to touch each other.");
        hbvb3_1 = new HBox(10);
        hbvb3_1_image = new ImageView(new Image("/images/hbvb3_1.png"));
        hbvb3_1_IMGCaption = new Label("""
                You can add a
                tile to both ends
                of a line in one
                turn.""");
        vbox3_2_3_paragraph = new Text("""
                There cannot be duplicate tiles in a line. For example,
                a line of squares can only have one blue square. A line
                can never be longer than six tiles.

                As the game progresses, spaces will be created where
                no tile can be played.""");
        vbox3_1_title = new Label("Scoring");
        vbox3_4_paragraph = new Text("One point is scored for each tile in a line that you\n" +
                "create or add to.");
        hbvb3_2 = new HBox(10);
        hbvb3_2_image = new ImageView(new Image("/images/hbvb3_2.png"));
        hbvb3_2_IMGCaption = new Label("""
                Playing the red
                starburst scores four
                points—one point for
                each tile in the red
                line.""");
        //Forth Column
        vbox4 = new VBox(0);
        vbox4_1_paragraph = new Text("A single tile can score twice if it is part of two lines.");
        hbvb4 = new HBox(10);
        hbvb4_image = new ImageView(new Image("/images/hbvb4_1.png"));
        hbvb4_1_IMGCaption = new Label("""
                Playing the blue
                circle scores four
                points; two for the
                blue line and two
                points for the
                circle line.""");
        vbox4_2_paragraph = new Text("""
                Whenever you complete a line of all six it is called
                a Qwirkle. A Qwirkle scores 12 points, six for the
                tiles in the line plus six bonus points.
                """);
        vbox4_1_2_image = new ImageView(new Image("/images/vbox4_1_2.png"));
        vbox4_1_2_IMGCaption = new Label("Qwirkle scored with shapes");
        vbox4_2_1_image = new ImageView(new Image("/images/vbox4_2_1.png"));
        vbox4_2_1_IMGCaption = new Label("Qwirkle scored with colors");
        vbox4_4_paragraph = new Text("A six-point bonus is scored by the first player\n" +
                "to run out of tiles.");
        vbox4_1_title = new Label("Ending the Game");
        vbox4_5_paragraph = new Text("""
                When there are no more tiles to draw, play
                continues as before, but players do not
                replenish their hands. The first player to
                run out of tiles scores six bonus points
                and ends the game. The player with the
                highest score wins.""");
        vbox4_2_title = new Label("Strategy Tips");
        vbox4_6_paragraph = new Text("""
                • Play your tiles so that they are part of more than
                  one line.
                • Save tiles that can help you make a Qwirkle.
                • Avoid creating places for other players to make
                  a Qwirkle.
                • Remember that there are three of each type of tile.""");
        //"Footer"
        back = new Button("Back");
    }

    private void layoutNodes() {
        //Header
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        logo.setCache(true);
        setAlignment(logo, Pos.CENTER);
        setMargin(logo, new Insets(30, 0, 0, 0));
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        //Center
        //First Column
        vbTOP_title.setStyle("-fx-font-size: 40; -fx-text-fill: #b80f34; -fx-font-weight: bold; -fx-text-alignment: center");
        vbTOP_title.setAlignment(Pos.TOP_CENTER);
        vbTOP_title.setPadding(new Insets(2, 0, 0, 0));
        vb1_1_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vb1_2_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vb1_1_title.setAlignment(Pos.TOP_LEFT);
        vb1_2_title.setAlignment(Pos.BOTTOM_LEFT);
        vb1_2_title.setPadding(new Insets(15, 0, 0, 0));
        vb1_1_paragraph.setStyle("-fx-font-size: 16;");
        vb1_2_paragraph.setStyle("-fx-font-size: 16;");
        hbvb1_2_image.setFitWidth(190);
        hbvb1_2_image.setPreserveRatio(true);
        vb1_1.getChildren().addAll(vb1_1_title, vb1_1_paragraph, vb1_2_title, vb1_2_paragraph);
        vb1_1.setAlignment(Pos.TOP_LEFT);
        hbvb1_1.getChildren().addAll(vb1_1, hbvb1_2_image);
        vb1_3_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vb1_3_title.setPadding(new Insets(15, 0, 0, 0));
        vb1_3_paragraph.setStyle("-fx-font-size: 16;");
        getVb1_4_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        getVb1_4_title.setPadding(new Insets(15, 0, 0, 0));
        vb1_4_paragraph.setStyle("-fx-font-size: 16;");
        getVb1_5_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        getVb1_5_title.setPadding(new Insets(15, 0, 0, 0));
        vb1_5_paragraph.setStyle("-fx-font-size: 16;");
        vb1_2_image.setFitWidth(111);
        vb1_2_image.setPreserveRatio(true);
        vb1_2_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        vb1_2_IMGCaption.setPadding(new Insets(13, 0, 0, 0));
        hbvb1_2.getChildren().addAll(vb1_2_image, vb1_2_IMGCaption);
        hbvb1_2.setPadding(new Insets(15, 0, 0, 0));
        vbox1.getChildren().addAll(hbvb1_1, vb1_3_title, vb1_3_paragraph, getVb1_4_title, vb1_4_paragraph, getVb1_5_title,
                vb1_5_paragraph, hbvb1_2);

        //Second Column
        vbox2_1_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox2_1_title.setAlignment(Pos.TOP_LEFT);
        vbox2_1_BulParagraph.setStyle("-fx-font-size: 16;");
        vbox2_1_paragraph.setStyle("-fx-font-size: 16;");
        vbox2_2_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox2_2_title.setAlignment(Pos.TOP_LEFT);
        vbox2_2_title.setPadding(new Insets(15, 0, 0, 0));
        vbox2_2_paragraph.setStyle("-fx-font-size: 16; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox2_3_paragraph.setStyle("-fx-font-size: 16; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        hbvb2_image.setPreserveRatio(true);
        hbvb2_image.setFitWidth(130);
        hbvb2_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        hbvb2_IMGCaption.setPadding(new Insets(13, 0, 0, 0));
        hbvb2.getChildren().addAll(hbvb2_image, hbvb2_IMGCaption);
        hbvb2.setPadding(new Insets(15, 0, 0, 0));
        vbox2.getChildren().addAll(vbox2_1_title, vbox2_1_BulParagraph, vbox2_1_paragraph, vbox2_2_title, vbox2_2_paragraph,
                vbox2_3_paragraph, hbvb2);

        //Third Column
        vbox3_1_paragraph.setStyle("-fx-font-size: 16;");
        hbvb3_1_image.setPreserveRatio(true);
        hbvb3_1_image.setFitWidth(200);
        hbvb3_1_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        hbvb3_1_IMGCaption.setPadding(new Insets(13, 0, 0, 0));
        hbvb3_1.getChildren().addAll(hbvb3_1_image, hbvb3_1_IMGCaption);
        hbvb3_1.setPadding(new Insets(15, 0, 0, 0));
        vbox3_2_3_paragraph.setStyle("-fx-font-size: 16;");
        vbox3_1_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox3_1_title.setAlignment(Pos.TOP_LEFT);
        vbox3_1_title.setPadding(new Insets(15, 0, 0, 0));
        vbox3_4_paragraph.setStyle("-fx-font-size: 16;");
        hbvb3_2_image.setPreserveRatio(true);
        hbvb3_2_image.setFitWidth(154);
        hbvb3_2_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        hbvb3_2_IMGCaption.setPadding(new Insets(13, 0, 0, 0));
        hbvb3_2.getChildren().addAll(hbvb3_2_image, hbvb3_2_IMGCaption);
        hbvb3_2.setPadding(new Insets(15, 0, 0, 0));
        vbox3.getChildren().addAll(vbox3_1_paragraph, hbvb3_1, vbox3_2_3_paragraph, vbox3_1_title, vbox3_4_paragraph,
                hbvb3_2);
        //Forth Column
        vbox4_1_paragraph.setStyle("-fx-font-size: 16;");
        hbvb4_image.setPreserveRatio(true);
        hbvb4_image.setFitWidth(151);
        hbvb4_1_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        hbvb4_1_IMGCaption.setPadding(new Insets(13, 0, 0, 0));
        hbvb4.getChildren().addAll(hbvb4_image, hbvb4_1_IMGCaption);
        hbvb4.setPadding(new Insets(15, 0, 0, 0));
        vbox4_2_paragraph.setStyle("-fx-font-size: 16;");
        vbox4_1_2_image.setPreserveRatio(true);
        vbox4_1_2_image.setFitWidth(230);
        vbox4_1_2_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        vbox4_1_2_IMGCaption.setPadding(new Insets(3, 0, 15, 0));
        vbox4_2_1_image.setPreserveRatio(true);
        vbox4_2_1_image.setFitWidth(230);
        vbox4_2_1_IMGCaption.setStyle("-fx-font-size: 15; -fx-text-fill: #ab3636; -fx-font-weight: bold;");
        vbox4_2_1_IMGCaption.setPadding(new Insets(3, 0, 15, 0));
        vbox4_4_paragraph.setStyle("-fx-font-size: 16;");
        vbox4_1_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox4_1_title.setAlignment(Pos.TOP_LEFT);
        vbox4_1_title.setPadding(new Insets(15, 0, 0, 0));
        vbox4_5_paragraph.setStyle("-fx-font-size: 16;");
        vbox4_2_title.setStyle("-fx-font-size: 20; -fx-text-fill: #0f81b8; -fx-font-weight: bold;");
        vbox4_2_title.setAlignment(Pos.TOP_LEFT);
        vbox4_2_title.setPadding(new Insets(15, 0, 0, 0));
        vbox4_6_paragraph.setStyle("-fx-font-size: 16;");
        vbox4.getChildren().addAll(vbox4_1_paragraph, hbvb4, vbox4_2_paragraph, vbox4_1_2_image, vbox4_1_2_IMGCaption,
                vbox4_2_1_image, vbox4_2_1_IMGCaption, vbox4_4_paragraph, vbox4_2_title, vbox4_6_paragraph);


        //Main container center
        hbCenter.setPadding(new Insets(10, 20, 10, 20));
        hbCenter.setAlignment(Pos.TOP_CENTER);
        hbCenter.getChildren().addAll(vbox1, vbox2, vbox3, vbox4);
        vbTOP.getChildren().addAll(vbTOP_title, hbCenter);
        vbTOP.setAlignment(Pos.TOP_CENTER);


        //Footer
        back.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        setMargin(back, new Insets(0, 0, 50, 0));
        back.setPrefSize(220, 60);
        setTop(logo);
        setBottom(back);
        setCenter(vbTOP);
        setAlignment(back, Pos.CENTER);
        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    //Getters
    Button getBack() {
        return back;
    }

    ImageView getLogo() {
        return logo;
    }

    VBox getVbTOP() {
        return vbTOP;
    }
}
