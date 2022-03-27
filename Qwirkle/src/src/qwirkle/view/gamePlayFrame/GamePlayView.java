package src.qwirkle.view.gamePlayFrame;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GamePlayView extends BorderPane {

    private Label tilesLeft;
    private Integer counter;
    private ImageView logo;
    private Label time;
    private BorderPane hbTOP;
    private Label computerScore;
    private HBox leftBox;
    private Label playerScore;
    private HBox rightBox;
    private HBox hbScore;
    private ListView<ListView<Rectangle>> playedTiles;
    private TileNode emptyTile;
    private GridPane grid;
    private HBox hbMAIN;
    private Button undo;
    private VBox vb1;
    private VBox vb2;
    private Button exchangeTiles;
    ImageView firstFullBag;
    ImageView fullBagPopup;
    private Button submit;
    private VBox vb3;
    private Rectangle deckSpot1;
    private Rectangle deckSpot2;
    private Rectangle deckSpot3;
    private Rectangle deckSpot4;
    private Rectangle deckSpot5;
    private Rectangle deckSpot6;
    private TilePane deck;
    private TilePane activeDeck;
    private StackPane stackDeck;
    private VBox vbBottom;
    private Button rules;
    private Button quit;
    private HBox hbmenu;
    private Image background;
    private ImageView newGame;
    private Label message;
    private VBox container;

    public GamePlayView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        //Top
        counter = 118;
        tilesLeft = new Label("Tiles Left: " + counter);
        logo = new ImageView(new Image("/images/logoMAX.png"));
        time = new Label("Time");
        hbTOP = new BorderPane();
        //Left
        undo = new Button("Undo");
        vb1 = new VBox();
        //Middle
        computerScore = new Label("Computer Score: 24");
        playerScore = new Label("Player Score: 42");
        leftBox = new HBox();
        rightBox = new HBox();
        hbScore = new HBox();

//        playedTiles = new LinkedList<>();
        emptyTile = new TileNode();
        grid = new GridPane();
        vb2 = new VBox();
        //Right
        exchangeTiles = new Button();
        submit = new Button("Submit");
        fullBagPopup = new ImageView();
        firstFullBag = new ImageView();
        vb3 = new VBox(140);
        //Center
        hbMAIN = new HBox();
        //Bottom
        rules = new Button("Rules");
        quit = new Button("Quit");
        deckSpot1 = new Rectangle(50, 50);
        deckSpot2 = new Rectangle(50, 50);
        deckSpot3 = new Rectangle(50, 50);
        deckSpot4 = new Rectangle(50, 50);
        deckSpot5 = new Rectangle(50, 50);
        deckSpot6 = new Rectangle(50, 50);
        deck = new TilePane();
        activeDeck = new TilePane();
        stackDeck = new StackPane();
        vbBottom = new VBox(20);
        hbmenu = new HBox(0.5);
        background = new Image("/images/tiles3D_min.png");
    }

    private void layoutNodes() {
        //Top
        tilesLeft.setTextFill(Color.rgb(255, 255, 255, 1.0));
        tilesLeft.setStyle("-fx-font-weight: bold; -fx-font-size: 22; -fx-alignment: center;");
        tilesLeft.setBackground(new Background(new BackgroundFill(Color.rgb(48, 50, 58, 1),
                new CornerRadii(5.0), new Insets(-15, -30, -15, -30))));
        tilesLeft.setPrefWidth(190);
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        logo.setCache(true);
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        time.setTextFill(Color.rgb(255, 255, 255, 1.0));
        time.setStyle("-fx-font-weight: bold; -fx-font-size: 22; -fx-alignment: center;");
        time.setBackground(new Background(new BackgroundFill(Color.rgb(48, 50, 58, 1),
                new CornerRadii(5.0), new Insets(-15, -30, -15, -30))));
        time.setPrefWidth(190);
        hbTOP.setLeft(tilesLeft);
        hbTOP.setCenter(logo);
        hbTOP.setRight(time);
        hbTOP.setPadding(new Insets(30, 70, 0, 70));
        setTop(hbTOP);
        //Left
        undo.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        undo.setPrefSize(220, 90);
        ;
        vb1.getChildren().addAll(undo);
        vb1.setAlignment(Pos.CENTER);
        vb1.setFillWidth(true);
        vb1.setPadding(new Insets(0, 10, 0, 50));
        //Middle
        computerScore.setTextFill(Color.rgb(0, 0, 0, 1));
        computerScore.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-alignment: center; " +
                "-fx-padding: 15, 30, 15, 30; -fx-border-radius: 5; -fx-border-color: rgba(0,0,0,0.54)");
        computerScore.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.1),
                new CornerRadii(5.0), new Insets(0, 0, 0, 0))));
        computerScore.setMinWidth(250);
        playerScore.setTextFill(Color.rgb(0, 0, 0, 1));
        playerScore.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-alignment: center; " +
                "-fx-padding: 15, 30, 15, 30; -fx-border-radius: 5; -fx-border-color: rgba(0,0,0,0.54);");
        playerScore.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.1),
                new CornerRadii(5.0), new Insets(0, 0, 0, 0))));
        playerScore.setMinWidth(250);
        leftBox.getChildren().add(computerScore);
        computerScore.setAlignment(Pos.BASELINE_LEFT);
        rightBox.getChildren().add(playerScore);
        playerScore.setAlignment(Pos.BASELINE_RIGHT);
        leftBox.setAlignment(Pos.BASELINE_LEFT);
        rightBox.setAlignment(Pos.BASELINE_RIGHT);
        hbScore.setSpacing(350);
        hbScore.getChildren().addAll(leftBox, rightBox);
        grid.setPrefSize(850, 650);
        grid.setMaxWidth(850);
        grid.setGridLinesVisible(false);
//        final int numCols = 17 ;
//        final int numRows = 13 ;
//        for (int i = 0; i < numCols; i++) {
//            ColumnConstraints columnConstraints = new ColumnConstraints();
//            columnConstraints.setHgrow(Priority.SOMETIMES);
//            grid.getColumnConstraints().add(columnConstraints);
//        }
//        for (int i = 0; i < numRows; i++) {
//            RowConstraints rowConstraints = new RowConstraints();
//            rowConstraints.setVgrow(Priority.SOMETIMES);
//            grid.getRowConstraints().add(rowConstraints);
//        }
//        for (int row = 0; row < grid.getRowCount(); row++){
//            for (int column = 0; column < grid.getColumnCount(); column++){
//                grid.add(new TileNode(), column, row);
//            }
//        }
//        TileNode[][] tiles = new TileNode[17][13];
//        for (int i = 0; i < tiles.length; i++) {
//            for (int j = 0; j < tiles[i].length; j++) {
//                tiles[i][j] = new TileNode();
//                grid.add(tiles[i][j], i, j);
//            }
//        }
        grid.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(15.0), new Insets(0))));
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-border-radius: 5");
        vb2.getChildren().addAll(hbScore, grid);
        vb2.setAlignment(Pos.CENTER);
        vb2.setMinHeight(650);
        vb2.setMaxWidth(850);
        //Right
        Image img = new Image("/images/qwirkle_bag_no_label_min.png");
        firstFullBag.setImage(img);
        firstFullBag.setPreserveRatio(true);
        firstFullBag.setFitWidth(220);
        Image popup = new Image("images/full_bag_min.png");
        fullBagPopup.setImage(popup);
        fullBagPopup.setPreserveRatio(true);
        fullBagPopup.setFitWidth(220);
        exchangeTiles.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand;");
        exchangeTiles.setPrefWidth(220);
        exchangeTiles.setBackground(Background.EMPTY);
        exchangeTiles.setGraphic(firstFullBag);
        submit.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        submit.setPrefSize(220, 220);
        vb3.getChildren().addAll(exchangeTiles, submit);
        vb3.setAlignment(Pos.CENTER);
        vb3.setFillWidth(true);
        vb3.setPrefHeight(700);
        vb3.setPadding(new Insets(0, 50, 0, 10));
        //Center
        hbMAIN.getChildren().addAll(vb2);
        hbMAIN.setAlignment(Pos.CENTER);
        hbMAIN.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10);");
        setCenter(hbMAIN);
        setLeft(vb1);
        setRight(vb3);
        //Bottom
        rules.setStyle("-fx-background-color: #FF5733; -fx-font-size: 20; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-text-alignment: center;");
        rules.setPrefSize(220, 25);
        quit.setStyle("-fx-background-color: #FF5733; -fx-font-size: 20; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-text-alignment: center;");
        quit.setPrefSize(220, 25);
        hbmenu.getChildren().addAll(rules, quit);
        hbmenu.setPadding(new Insets(0, 0, 20, 0));
        hbmenu.setAlignment(Pos.CENTER);
        deckSpot1.setFill(Color.LIGHTGRAY);
        deckSpot1.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deckSpot2.setFill(Color.LIGHTGRAY);
        deckSpot2.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deckSpot3.setFill(Color.LIGHTGRAY);
        deckSpot3.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deckSpot4.setFill(Color.LIGHTGRAY);
        deckSpot4.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deckSpot5.setFill(Color.LIGHTGRAY);
        deckSpot5.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deckSpot6.setFill(Color.LIGHTGRAY);
        deckSpot6.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-border-radius: 2;");
        deck.getChildren().addAll(deckSpot1, deckSpot2, deckSpot3, deckSpot4, deckSpot5, deckSpot6);
        deck.setHgap(28);
        activeDeck.setHgap(28);
        deck.setAlignment(Pos.BASELINE_LEFT);
        deck.setPrefTileWidth(50);
        deck.setPrefColumns(6);
        activeDeck.setAlignment(Pos.BASELINE_LEFT);
        activeDeck.setPrefTileWidth(50);
        activeDeck.setPrefColumns(6);
        stackDeck.getChildren().addAll(deck, activeDeck);
        StackPane.setAlignment(deck, Pos.CENTER);
        StackPane.setAlignment(activeDeck, Pos.CENTER);
        vbBottom.getChildren().addAll(stackDeck, hbmenu);
        vbBottom.setAlignment(Pos.CENTER);
        vbBottom.setMaxWidth(440);
        setBottom(vbBottom);
        setAlignment(vbBottom, Pos.CENTER);
        BackgroundImage bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, false, Side.BOTTOM, 0,
                        false), new BackgroundSize(100, 100, true,
                true, false, true));
        setBackground(new Background(bgImage));
    }

    //Getters

    Button getSubmit() {
        return submit;
    }

    Button getRules() {
        return rules;
    }

    Button getQuit() {
        return quit;
    }

    Rectangle getDeckSpot1() {
        return deckSpot1;
    }

    Rectangle getDeckSpot2() {
        return deckSpot2;
    }

    Rectangle getDeckSpot3() {
        return deckSpot3;
    }

    Rectangle getDeckSpot4() {
        return deckSpot4;
    }

    Rectangle getDeckSpot5() {
        return deckSpot5;
    }

    Rectangle getDeckSpot6() {
        return deckSpot6;
    }

    Label getTime() {
        return time;
    }

    TilePane getDeck() {
        return deck;
    }

    Label getTilesLeft() {
        return tilesLeft;
    }

    Label getPlayerScore() {
        return playerScore;
    }

    TilePane getActiveDeck() {
        return activeDeck;
    }

    TileNode getEmptyTile() {
        return emptyTile;
    }

    Button getUndo() {
        return undo;
    }

    GridPane getGrid() {
        return grid;
    }

    Label getComputerScore() {
        return computerScore;
    }

    Button getExchangeTiles() {
        return exchangeTiles;
    }
    ImageView getFullBagPopup(){
        return fullBagPopup;
    }
    ImageView getFirstFullBag(){
        return firstFullBag;
    }


}
