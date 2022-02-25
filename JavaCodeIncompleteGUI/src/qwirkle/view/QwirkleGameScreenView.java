package qwirkle.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;


public class QwirkleGameScreenView extends BorderPane { //Could be another kind of pane


    //TOP
    private VBox topContainer;

    //TOP TOP
    private HBox topHBox;
    private Label tilesLeft;
    private ImageView logoImage;
    private Label timer;

    //TOP BOTTOM
    private HBox scoreContainer;
    private Label playerScoreLabel;
    private Label computerScoreLabel;

    //LEFT
    private VBox leftContainer;
    private Button undoButton;

    //RIGHT
    private VBox rightContainer;
    private Button swapTilesButton;
    private Button submitButton;

    //BOTTOM
    private HBox bottomContainer;
    private Button ruleButton;
    private Button settingButton;
    private Button quitButton;
    private HashMap<String,Button> menuButtons;


    //CENTRE
    private VBox centerContainer;
    private GridPane handDisplay;

    //BOTTOM CENTRE
    private HBox bottomCenterContainer;







    public QwirkleGameScreenView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        //TOP
        topContainer = new VBox();

        //TOP TOP
        topHBox = new HBox();
        tilesLeft = new Label("Tiles Left: 108");
        logoImage = new ImageView(new Image("images/qwirkleLOGO.png"));
        timer = new Label("Time: ");

        //TOP BOTTOM
        scoreContainer = new HBox();
        playerScoreLabel = new Label("Your Score: ");
        computerScoreLabel = new Label("Computer Score: ");

        //LEFT
        leftContainer = new VBox();
        undoButton = new Button("Undo");

        //RIGHT
        rightContainer = new VBox();
        swapTilesButton = new Button("Trade");
        submitButton = new Button("Submit");

        //BOTTOM
        bottomContainer = new HBox();
        ruleButton = new Button("Rules");
        settingButton = new Button("Settings");
        quitButton = new Button("Quit");


        menuButtons = new HashMap<>();
        menuButtons.put("rules",ruleButton);
        menuButtons.put("settings",settingButton);
        menuButtons.put("quit",quitButton);

        //CENTRE
        centerContainer = new VBox();
        handDisplay = new GridPane();

        //BOTTOM CENTRE
        bottomCenterContainer = new HBox();

    }

    private void layoutNodes() {
        //TOP
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setSpacing(50);
//        topContainer.setId("VBox topContainer");
        //TOP TOP---------------------------------------------------------------------------------------------------------
        topHBox.setAlignment(Pos.CENTER);
        topHBox.setSpacing(400);
        tilesLeft.setStyle("-fx-border-color: black; -fx-font-family: Consolas; -fx-font-size: 20");
        timer.setStyle("-fx-border-color: black; -fx-font-family: Consolas; -fx-font-size: 20");
        logoImage.setPreserveRatio(true);
        logoImage.setFitWidth(350);
        centerLogo();
        topHBox.getChildren().addAll(tilesLeft,logoImage,timer);

        //TOP BOTTOM---------------------------------------------------------------------------------------------------------
        scoreContainer.setAlignment(Pos.CENTER);
        scoreContainer.setSpacing(250);
        scoreContainer.getChildren().addAll(computerScoreLabel,playerScoreLabel);

        topContainer.getChildren().addAll(topHBox,scoreContainer);

        //LEFT---------------------------------------------------------------------------------------------------------
        leftContainer.setAlignment(Pos.CENTER);
        leftContainer.setPrefWidth(250);

        undoButton.setPrefWidth(100);
        undoButton.setPrefHeight(40);
        undoButton.setAlignment(Pos.CENTER);
        leftContainer.getChildren().add(undoButton);

        //RIGHT---------------------------------------------------------------------------------------------------------
        rightContainer.setAlignment(Pos.CENTER);
        rightContainer.setSpacing(150);
        rightContainer.setPrefWidth(250);

        submitButton.setPrefHeight(70);
        submitButton.setPrefWidth(100);
        submitButton.setAlignment(Pos.CENTER);
        swapTilesButton.setPrefWidth(100);
        swapTilesButton.setPrefHeight(40);
        swapTilesButton.setAlignment(Pos.CENTER);

        rightContainer.getChildren().addAll(swapTilesButton,submitButton);

        //BOTTOM---------------------------------------------------------------------------------------------------------
        bottomContainer.setAlignment(Pos.BOTTOM_CENTER);

        ruleButton.setPrefWidth(80);
        ruleButton.setPrefHeight(35);

        quitButton.setPrefWidth(80);
        quitButton.setPrefHeight(35);


        settingButton.setPrefWidth(80);
        settingButton.setPrefHeight(35);

        bottomContainer.getChildren().addAll(ruleButton,settingButton,quitButton);

        //CENTRE---------------------------------------------------------------------------------------------------------
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setSpacing(25);
//        initialiseGridBoard();

        bottomCenterContainer.setAlignment(Pos.CENTER);
        handDisplay.setAlignment(Pos.CENTER);
        bottomCenterContainer.getChildren().add(handDisplay);

        centerContainer.getChildren().add(bottomCenterContainer);






        this.setBottom(bottomContainer);
        this.setTop(topContainer);
        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
        this.setRight(rightContainer);

        BorderPane.setAlignment(topContainer,Pos.CENTER);

        BorderPane.setAlignment(centerContainer,Pos.CENTER);
        BorderPane.setMargin(centerContainer,new Insets(0,0,10,0));

        BorderPane.setAlignment(leftContainer,Pos.CENTER);
        BorderPane.setAlignment(rightContainer,Pos.CENTER);
        BorderPane.setAlignment(bottomContainer,Pos.CENTER);

    }




    /**
     * Calculation to centre an image
     */
    private void centerLogo() {
        Image img = logoImage.getImage();
        if (img != null) {
            double w;
            double h;

            double ratioX = logoImage.getFitWidth() / img.getWidth();
            double ratioY = logoImage.getFitHeight() / img.getHeight();

            double reducCoeff = Math.min(ratioX, ratioY);

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            logoImage.setX((logoImage.getFitWidth() - w) / 2);
            logoImage.setY((logoImage.getFitHeight() - h) / 2);

        }
    }


    // package-private Getters
    // for controls used by Presenter

    /**
     *
     * @return saved Menu Buttons for ease of use
     */
     HashMap<String, Button> getMenuButtons() {
        return menuButtons;
    }


     Button getUndoButton() {
        return undoButton;
    }

     Button getSwapTilesButton() {
        return swapTilesButton;
    }

     Button getSubmitButton() {
        return submitButton;
    }

     GridPane getHandDisplay() {
        return handDisplay;
    }

     void setHandDisplay(GridPane handDisplay) {
        this.handDisplay = handDisplay;
    }

    VBox getCenterContainer() {
        return centerContainer;
    }

     HBox getBottomCenterContainer() {
        return bottomCenterContainer;
    }


}
