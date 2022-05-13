package qwirkle.view.gamePlayFrame;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
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
    private TileNode emptyTile;
    private GridPane grid;
    private HBox hbMAIN;
    private Button undo;
    private VBox vb1;
    private VBox vb2;
    private Button exchangeTiles;
    private ImageView firstFullBag;
    private ImageView fullBagPopup;
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
    private Label gameStatus;

    //GameOver
    private Label label;
    private ImageView gameOver;
    private VBox vBox;
    private Button statistics;
    private Button newGame;

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
        gameStatus = new Label();
        gameStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 22; -fx-alignment: center; -fx-text-fill: white;");
        gameStatus.setBackground(new Background(new BackgroundFill(Color.rgb(48, 50, 58, 1),
                new CornerRadii(5.0), new Insets(-15, -30, -15, -30))));
        gameStatus.setAlignment(Pos.CENTER);
        gameStatus.setPadding(new Insets(20, 0, 0, 0));
        hbTOP = new BorderPane();
        //Left
        undo = new Button("Undo");
        vb1 = new VBox();
        //Middle
        computerScore = new Label("");
        playerScore = new Label("");
        leftBox = new HBox();
        rightBox = new HBox();
        hbScore = new HBox();

//        playedTiles = new LinkedList<>();
        emptyTile = new TileNode(50);
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

        //GameOver
        label = new Label();
        gameOver = new ImageView(new Image("/images/gameover_min.png", 850, 459, true, true));
        vBox = new VBox(20);
        statistics = new Button("Statistics");
        newGame = new Button("New Game");
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
//        hbTOP.setBottom(gameStatus);
        BorderPane.setAlignment(gameStatus, Pos.CENTER);
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
        computerScore.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-alignment: center; " +
                "-fx-padding: 15, 30, 15, 30; -fx-border-radius: 5; -fx-border-color: rgba(0,0,0,0.54)");
        computerScore.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(5.0), new Insets(0, 0, 0, 0))));
        computerScore.setMinWidth(250);
        playerScore.setTextFill(Color.rgb(0, 0, 0, 1));
        playerScore.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-alignment: center; " +
                "-fx-padding: 15, 30, 15, 30; -fx-border-radius: 5; -fx-border-color: rgba(0,0,0,0.54);");
        playerScore.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(5.0), new Insets(0, 0, 0, 0))));
        playerScore.setMinWidth(250);
        leftBox.getChildren().add(computerScore);
        computerScore.setAlignment(Pos.BASELINE_LEFT);
        rightBox.getChildren().add(playerScore);
        playerScore.setAlignment(Pos.BASELINE_RIGHT);
        leftBox.setAlignment(Pos.BASELINE_LEFT);
        rightBox.setAlignment(Pos.BASELINE_RIGHT);
        hbScore.setPrefWidth(950);
        hbScore.setSpacing(450);
        hbScore.getChildren().addAll(leftBox, rightBox);
        grid.setPrefSize(950, 650);
        grid.setMaxSize(950, 650);
        grid.setPadding(new Insets(0));
        grid.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.6),
                new CornerRadii(15.0), new Insets(0))));
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-border-radius: 5");
        vb2.getChildren().addAll(hbScore, grid);
        vb2.setAlignment(Pos.CENTER);
        vb2.setMinHeight(650);
        vb2.setMaxWidth(950);
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

        //GameOver
        label.setTranslateY(gameOver.getY() - 90);
        label.setStyle("-fx-font-family: 'American Typewriter'; -fx-font-size: 60; -fx-text-fill: #ffffff;" +
                "-fx-font-weight: bold; -fx-effect: dropshadow( gaussian , rgb(0,0,0) , 5,0,10,10 );");
        label.setPrefWidth(450);
        label.setPrefHeight(200);
        label.setAlignment(Pos.CENTER);
        gameOver.setStyle("-fx-effect: dropshadow( gaussian , rgb(0,0,0) , 5,0,10,10 );");
        statistics.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        statistics.setPrefSize(220, 50);
        newGame.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        newGame.setPrefSize(220, 50);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(gameOver, label, statistics, newGame);
        vBox.setPrefWidth(850);
        vBox.setMaxWidth(850);
        vBox.setPadding(new Insets(0));
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-border-radius: 5");
        vBox.setPadding(new Insets(50, 0, 0, 0));
    }

     void makeTransparent() {
        grid.setOpacity(0);
        submit.setOpacity(0);
        undo.setOpacity(0);
        exchangeTiles.setOpacity(0);
        for (Node node : deck.getChildren()) {
            node.setOpacity(0);
        }
        for (Node node : activeDeck.getChildren()) {
            node.setOpacity(0);
        }
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

    Label getTime() {
        return time;
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

    ImageView getFullBagPopup() {
        return fullBagPopup;
    }

    ImageView getFirstFullBag() {
        return firstFullBag;
    }

    VBox getVBox() {
        return vBox;
    }

    VBox getVb2() {
        return vb2;
    }

    HBox getHbScore() {
        return hbScore;
    }

    Button getStatistics() {
        return statistics;
    }

    Button getNewGame() {
        return newGame;
    }

    Label getLabel() {
        return label;
    }

    ImageView getGameOverImage() {
        return gameOver;
    }
}
