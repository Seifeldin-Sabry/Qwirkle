package qwirkle.view.gamePlayFrame;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import qwirkle.data.Database;
import qwirkle.model.*;
import qwirkle.model.computer.Computer;
import qwirkle.model.computer.ComputerAI;
import qwirkle.view.newGameFrame.NewGamePresenter;
import qwirkle.view.newGameFrame.NewGameView;
import qwirkle.view.rulesFrame.RulesPresenterGP;
import qwirkle.view.rulesFrame.RulesView;
import qwirkle.view.popupFrame.PopupPresenter;
import qwirkle.view.popupFrame.PopupView;
import qwirkle.view.statisticsFrame.StatisticsPresenterGO;
import qwirkle.view.statisticsFrame.StatisticsView;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//All game-play scene iterations and methods are in this class. It uses the PopupPresenter for messages to the user
//The gameOver view is trigger and "painted" here too by replacing the content of specific nodes and deactivating/removing
// certain buttons and fields
//All timelines are class attributes so that "stop" can be used at the end of the last Keyframe or from within the next
//called method. All timelines are used for timed popups or delayed sequence of actions
public class GamePlayPresenter {

    private final GamePlayView view;
    private GameSession model;
    private Timeline timer; //Used for the timer set on the top right of the screen
    private Timeline iterateTurnsTM; //All animations inside the iterateTurns method use this timeLine
    private Timeline welcome; //Used in the welcome  animation
    private Timeline tileExchange; //Animation of messages during the tiles exchange process
    private Timeline submit; //Sets delayed popup messages after the player presses submit
    private Timeline gameOver; //Active during the different GameOver conditions with different keyframes
    private Timeline computerTurn; //Used in the playComputer method to set the animation of computer turn
    private final LinkedList<TileNode> deckTiles = new LinkedList<>(); //Contains a list player's deck tiles
    private final LinkedList<TileNode> exchangedTiles = new LinkedList<>(); //Contains the list of exchanged tiles
    private final LinkedList<TileNode> validPositionList = new LinkedList<>(); //All empty (grey) tiles ("edges" in the model) where tiles can be placed
    private final LinkedList<TileNode> playedTiles = new LinkedList<>(); //Contains a list of the played tiles per turn for the grid
    private TileNode draggableTile; //Used for the DragBoard class to identify the draggable Nodes and separate them from the played tiles of the grid (DragEvent)
    private static DataFormat tileFormat; //Static attribute used by the DragBoard class. Can be only instantiated once per application.
    private double tileSize = 50; //The default TileSize when game starts

    public GamePlayPresenter(Stage stage, GamePlayView view, GameSession model) {
        this.model = model;
        this.view = view;
        if (tileFormat == null) {
            tileFormat = new DataFormat("MyTile");
        }
        updateView();
        addEventHandler(stage);
        timerSet();
        welcomeAnimation(stage);
    }

    private void addEventHandler(Stage stage) {
        view.getQuit().setOnAction(event -> setAlert(event, stage));
        view.getRules().setOnAction(event -> setRulesView());
        view.getSubmit().setOnAction(event -> {
            submit(stage);
            zoomIn(view.getSubmit(), 0.8, 100);
        });
        view.getSubmit().setOnMouseEntered(e -> bounceNode(view.getSubmit(), 100));
        view.getSubmit().setOnMouseExited(e -> resetBounce(view.getSubmit()));
        view.getUndo().setOnAction(event -> {
            undo();
            zoomIn(view.getUndo(), 0.8, 100);
        });
        view.getUndo().setOnMouseEntered(e -> bounceNode(view.getUndo(), 100));
        view.getUndo().setOnMouseExited(e -> resetBounce(view.getUndo()));
        view.getExchangeTiles().setOnMouseEntered(e -> view.getExchangeTiles().setGraphic(view.getFullBagPopup()));
        view.getExchangeTiles().setOnMouseExited(e -> view.getExchangeTiles().setGraphic(view.getFirstFullBag()));
        swapTilesHandler();
    }

    //Communicates with the model for tiles trade
    private void submitExchange() {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (TileNode tileNode : exchangedTiles) {
            tiles.add(tileNode.getTile());
        }
        model.getPlayerSession().getPlayer().getDeck().trade(model.getBag(), tiles);
        model.getPlayerSession().getLastTurn().getMoves().clear();
        playedTiles.clear();
        exchangedTiles.clear();
        deckTiles.clear();
        draggableTile = null;
        validateTiles();
        updateView();
    }

    //Quit button warning alert
    private void setAlert(ActionEvent event, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("All current progress will be lost!");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().clear();
        alert.initOwner(stage);
        ButtonType no = new ButtonType("NO");
        ButtonType yes = new ButtonType("YES");
        alert.getButtonTypes().addAll(no, yes);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
        if (alert.getResult() == null || alert.getResult().equals(no)) {
            event.consume();
        } else {
            timer.stop();
            setWelcomeFrame(stage);
        }
    }

    //Go to WelcomeView
    private void setWelcomeFrame(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.view.getScene().setRoot(welcomeView);
        this.model = null;
    }

    //Go to RulesView
    private void setRulesView() {
        RulesView rulesView = new RulesView();
        new RulesPresenterGP(rulesView, this.view);
        view.getScene().setRoot(rulesView);
    }

    //Undo method used by player for played tiles "undo all moves" or tiles exchange "undo". After "submit it is no longer active
    //It requires at least playTiles list size 1 or exchangeTiles list size 1
    private void undo() {
        ArrayList<Node> nodes = new ArrayList<>(view.getGrid().getChildren());
        for (Node node : nodes) {
            for (TileNode tileNode : playedTiles) {
                if (node.equals(tileNode)) {
                    view.getGrid().getChildren().remove(node);
                    model.getGrid().setTile(tileNode.getRow(), tileNode.getCol(), null);
                    tileNode.savePosition(0, 0);
                    view.getActiveDeck().getChildren().add(tileNode);
                    model.getPlayerSession().getPlayer().getDeck().getTilesInDeck().add(tileNode.getTile());
                    cleanUpGrid();
                }
            }
        }
        model.getPlayerSession().getLastTurn().getMoves().clear();
        exchangedTiles.clear();
        playedTiles.clear();
        deckTiles.clear();
        draggableTile = null;
        validateTiles();
        updateView();
    }

    //Grey tiles are added to a class variable list to be used later on for the dragOver event
    private void validateTiles() {
        for (Node node : view.getGrid().getChildren()) {
            if (((TileNode) node).isEmpty()) {
                validPositionList.add((TileNode) node);
            }
        }
    }

    //Contains all possible scenarios while Player gets his turn to play. Triggered by "submit" button
    private void submit(Stage stage) {
        //First stop the timelines of computer's turn and the turn iteration
        if (computerTurn != null) {
            computerTurn.stop();
        }
        if (iterateTurnsTM != null) {
            iterateTurnsTM.stop();
        }
        //If player puts valid tiles on the grid and no tiles in the bag to exchange
        if (playedTiles.size() > 0 && exchangedTiles.size() == 0) {
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.05), e -> {
                iterateTurns(stage);
                //popup message showing the player points for this turn (see the method)
                popupPlayerPlayed(stage);
            });
            KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1.8), e -> {
                if (!model.isGameOver()) {
                    playComputerMove(stage);
                }
                submit.stop();
            });
            submit = new Timeline(keyFrame1, keyFrame2);
            submit.play();
            return;
        }
        //When player exchange tiles another animation played
        if (exchangedTiles.size() > 0 && playedTiles.size() == 0) {
            submitExchange();
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.05), e -> {
                popupTilesExchange(stage);
                iterateTurns(stage);
            });
            KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1.4), e -> {
                playComputerMove(stage);
                submit.stop();
            });
            submit = new Timeline(keyFrame1, keyFrame2);
            submit.play();
            return;
        }
        //Notifies the player what to do when he makes no choices and he presses the submit button
        if (playedTiles.size() == 0 && model.getBag().getAmountOfTilesLeft() > 0 && exchangedTiles.size() == 0) {
            String text = """
                    Place a tile on the board or
                     exchange tiles in the bag.""";
            popupMessage(stage, text, 2.5);
            return;
        }
        //Triggered only when the bag has no more tiles to replenish the deck
        if (!model.hasNoMoreMoves(model.getPlayerSession()) && playedTiles.size() == 0) {
            String text = """
                    There is at least 1 more
                           available move.""";
            popupMessage(stage, text, 2.5);
            return;
        }
        //Warning message when player has played a tile and added tiles too in the bag for exchange in the same turn
        if (exchangedTiles.size() > 0 && playedTiles.size() > 0) {
            String text = """
                    You can't trade tiles and place
                     them on the board at the same
                                        time!""";
            popupMessage(stage, text, 3);
            undo();
        }
    }

    //General pop message with fixed size for messages/warnings. Open on a separate stage and deactivate interaction with the back main stage while active
    private void popupMessage(Stage stage, String text, double startTime) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, startTime);
    }

    //Computer's turn to play (similar to player's submit method but with different conditions)
    //computerTurn.stop TimeLine method gets called at the end of each "if" statement, and it works as a "return"
    private void playComputerMove(Stage stage) {
        //First stop the timelines of player's turn and the turn iteration
        if (submit != null) {
            submit.stop();
        }
        if (iterateTurnsTM != null) {
            iterateTurnsTM.stop();
        }
        //Getting a list of moves from the model conditionally and based on the chosen game mode (Easy/AI)
        List<Move> moves = model.getComputerSession().getPlayer() instanceof ComputerAI ?
                ((ComputerAI) model.getComputerSession().getPlayer()).makeTurn(model.getComputerSession().indexOf(model.getComputerSession().getLastTurn()) + 1)
                : ((Computer) model.getComputerSession().getPlayer()).makeTurn();
        //First keyframe with a timed popup message to notify the player what was computer's move
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.1), e -> {
            //When moves return null and the bag still has tiles it means the computer traded tiles within the model
            if (moves == null && model.getBag().getTiles().size() > 0) {
                //Notify the player that the computer traded tiles
                popupComputerPlayed(stage, "Computer traded tiles", "", 1.8);
                iterateTurns(stage);
                computerTurn.stop();
            }
            //Triggered when the bag has no more tiles to notify the user that the computer had no move to play
            if (moves == null && model.getBag().getTiles().size() == 0) {
                popupComputerPlayed(stage, "Computer has no moves to play", "", 1.8);
                iterateTurns(stage);
                computerTurn.stop();
            }
            //Animation with a fixed duration shown before each time the computer places tiles on the grid
            if (moves != null && moves.size() > 0) {
                calculatingMove(stage, "Calculating move");
                for (Move move : moves) {
                    //First place 1 tile at a tile in the mode grid
                    model.getComputerSession().getPlayer().makeMove(move);
                    model.getComputerSession().getLastTurn().add(move);
                    //Assign a TileNode to represent graphically the played tile
                    TileNode tileNode = new TileNode(move.getTile(), gridZoomOut());
                    //Change coordinates of the tileNode from 0,0 (default) to the from the model
                    tileNode.savePosition(move.getCoordinate().getColumn(), move.getCoordinate().getRow());
                    //Add the tile to the playedTiles list for further handling in the presenter later on
                    playedTiles.add(tileNode);
                }
            }
        });
        //Second keyframe after the model has been updated. Time to show the tiles on the grid graphically
        KeyFrame kf2 = new KeyFrame(Duration.seconds(2.35), e -> {
            //Tiles placed graphically (see method)
            placeTiles(playedTiles);
            iterateTurns(stage);
            //Play popup message showing the points for the turn
            int points = model.getComputerSession().getLastTurn().getPoints();
            if (model.getComputerSession().getLastTurn().getPoints() > 0) {
                String pointsLabel;
                if (model.getComputerSession().getLastTurn().getPoints() == 1) {
                    pointsLabel = " point";
                } else {
                    pointsLabel = " points";
                }
                if (moves != null) {
                    popupComputerPlayed(stage, "Computer Played: ", points + pointsLabel, 1.6);
                }
            }
            computerTurn.stop();
        });
        computerTurn = new Timeline(kf1, kf2);
        computerTurn.play();
    }

    //Place the computer played tiles graphically on the grid
    private void placeTiles(LinkedList<TileNode> playedTiles) {
        for (TileNode tileNode : playedTiles) {
            //First add empty tiles from all 4 sizes if needs (see method for more details)
            fillEmptySpots();
            view.getGrid().add(tileNode, tileNode.getCol(), tileNode.getRow());
            //Sets a red border our the computer played tiles of that turn.
            //It gets removed after the player plays his first tile on the grid on his turn
            tileNode.setStyle("-fx-effect: dropshadow( gaussian , rgb(255,0,0) , 4,1,0,0 );");
        }
        playedTiles.clear();
    }

    //Called after each turn
    private void updateView() {
        if ((model.getPlayerSession().getTurnsPlayed().size() == 1 && model.getComputerSession().getTurnsPlayed().size() == 0)
                || (model.getPlayerSession().getTurnsPlayed().size() == 0 && model.getComputerSession().getTurnsPlayed().size() == 1)) {
            paintGrid(); //Only if nobody has placed a tile on the grid yet
        }
        fillEmptySpots();
        setDeckTiles();
        updateScore();
        updateTilesLeftLabel();
        resizeGridContent(gridZoomOut());
    }

    //Top left label indicating the amount of left tiles in the bag. It starts with 96
    private void updateTilesLeftLabel() {
        view.getTilesLeft().setText("Tiles left: " + model.getBag().getAmountOfTilesLeft());
    }

    //Used by the 2 popup methods showing computer's and player's score after submitting their move
    private void updateScore() {
        int computerTurnPoints;
        int computerTotalScore;
        int playerTurnPoints;
        int playerTotalScore;
        //When PlayerSession is active
        if (model.getActivePlayerSession().equals(model.getPlayerSession())) {
            if (model.getPlayerSession().getTurnsPlayed().size() == 1 && model.getPlayerSession().getLastTurn().getPoints() == 0) {
                view.getPlayerScore().setText("Your Score:    " + 0);
            } else {
                playerTurnPoints = model.getPlayerSession().get(model.getPlayerSession().indexOf(model.getPlayerSession().getLastTurn()) - 1).getPoints();
                playerTotalScore = model.getPlayerSession().getTotalScore();
                if (model.isGameOver()) {
                    playerTurnPoints += 6;
                }
                view.getPlayerScore().setText(String.format("Your score: %s (+%s)", playerTotalScore, playerTurnPoints));
            }
            if (model.getComputerSession().getTurnsPlayed().size() == 0) {
                view.getComputerScore().setText("Computer Score:    " + 0);
            } else {
                computerTurnPoints = model.getComputerSession().getLastTurn().getPoints();
                computerTotalScore = model.getComputerSession().getTotalScore();
                view.getComputerScore().setText(String.format("Computer score: %s (+%s)", computerTotalScore, computerTurnPoints));
            }
            //When ComputerSession is active
        } else {
            if (model.getComputerSession().getTurnsPlayed().size() == 1 && model.getComputerSession().getLastTurn().getPoints() == 0) {
                view.getComputerScore().setText("Computer Score:    " + 0);
            } else {
                computerTurnPoints = model.getComputerSession().get(model.getComputerSession().indexOf(model.getComputerSession().getLastTurn()) - 1).getPoints();
                computerTotalScore = model.getComputerSession().getTotalScore();
                if (model.isGameOver()) {
                    computerTurnPoints += 6;
                }
                view.getComputerScore().setText(String.format("Computer score: %s (+%s)", computerTotalScore, computerTurnPoints));
            }
            if (model.getPlayerSession().getTurnsPlayed().size() == 0) {
                view.getPlayerScore().setText("Your Score:    " + 0);
            } else {
                playerTurnPoints = model.getPlayerSession().getLastTurn().getPoints();
                playerTotalScore = model.getPlayerSession().getTotalScore();
                view.getPlayerScore().setText(String.format("Your score: %s (+%s)", playerTotalScore, playerTurnPoints));
            }
        }
    }

    //Trigger either by the first player who played all his tiles or when none of the players has a valid move and the
    //bag has no tiles left to exchange
    private void setGameOver(Stage stage) {
        //First stop all possible running timelines
        timer.stop();
        iterateTurnsTM.stop();
        model.setEndTime();
        model.getActivePlayerSession().getLastTurn().endTurn(model.getGrid());
        //Condition met by the first player who has played all his tiles
        if (model.isGameOver()) {
            //Bonus 6 points occur only on this gameOver condition. Model gets updated but the animation is running in a keyFrame bellow
            model.addExtraPoints();
            updateScore();
        }
        //Database gets all data stored
        Database.getInstance().save(model);
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0));
        //Different animation duration depending on the gameOver condition
        double duration = 0;
        //A player played al of his tiles on deck and there are none in the bag left
        if (model.isGameOver()) {
            if (model.getPlayerSession().isActive()) {
                keyFrame1 = new KeyFrame(Duration.seconds(1.2), e -> popupMessage(stage, "You got 6 extra points\n   for finishing first!", 1.8));
            } else if (model.getComputerSession().isActive()) {
                keyFrame1 = new KeyFrame(Duration.seconds(1.2), e -> popupMessage(stage, "Computer got 6 bonus points\n      for finishing first!", 1.8));
            }
            duration = 3.2;
        }
        //No tiles left in the bag but no possible moves for any of the players left
        if (!model.isGameOver()) {
            duration = 2;
        }
        //The view changes. Game Over image appears indicated who won. Several buttons become inactive and some other nodes are removed
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(duration), e -> {
            view.getVb2().getChildren().clear();
            view.getActiveDeck().getChildren().clear();
            view.getVb2().getChildren().addAll(view.getHbScore(), view.getVBox());
            if (model.getComputerSession().getTotalScore() > model.getPlayerSession().getTotalScore()) {
                view.getLabel().setText("Computer won!");
            } else {
                view.getLabel().setText("You won!");
            }
            zoomIn(view.getLabel(), 0, 500);
            zoomIn(view.getGameOverImage(), 0, 500);
            cancelButtons();
            view.makeTransparent();
            view.getStatistics().setOnAction(event -> {
                StatisticsView view = new StatisticsView();
                new StatisticsPresenterGO(view, this.view);
                this.view.getScene().setRoot(view);
            });
            view.getNewGame().setOnAction(event -> {
                NewGameView view = new NewGameView();
                new NewGamePresenter(stage, view);
                this.view.getScene().setRoot(view);
            });
            view.getQuit().setOnAction(event -> setWelcomeFrame(stage));
            if (submit != null) {
                submit.stop();
            }
            if (computerTurn != null) {
                computerTurn.stop();
            }
            gameOver.stop();
        });
        gameOver = new Timeline(keyFrame1, keyFrame2);
        gameOver.play();
    }

    //Deactivating actionEvents of certain buttons
    private void cancelButtons() {
        view.getSubmit().setOnAction(null);
        view.getSubmit().setStyle("-fx-cursor: pointer;");
        view.getUndo().setOnAction(null);
        view.getUndo().setStyle("-fx-cursor: pointer;");
        view.getExchangeTiles().setOnAction(null);
        view.getExchangeTiles().setStyle("-fx-cursor: pointer;");
    }

    //Getting the tiles distributed from the model and present them graphically for the player's deck
    private void setDeckTiles() {
        int tilesDistributed = model.getPlayerSession().getPlayer().getDeck().getTilesInDeck().size();
        view.getActiveDeck().getChildren().clear(); //Always clear the deck and refresh it from the model after each turn
        deckTiles.clear();
        draggableTile = new TileNode(gridZoomOut());
        //Triggered only when there are tiles distributed to the player after each turn
        if (tilesDistributed != 0) {
            for (int i = 0; i < tilesDistributed; i++) {
                //Add them to the deckTiles list with a fixed-final size
                deckTiles.add(new TileNode(model.getPlayerSession().getPlayer().getDeck().getTilesInDeck().get(i), 50));
                //Present them graphically
                view.getActiveDeck().getChildren().addAll(deckTiles.get(i));
                //Assign mouseOver bounce event
                deckTilesAnimation(deckTiles.get(i));
                //Assigning draggable property (setOnDragDetected) - DragEvent
                makeDraggable((TileNode) view.getActiveDeck().getChildren().get(i));
            }
        }
    }

    //Used only at the first turn placing the initial grey square. Called from within the updateView
    private void paintGrid() {
        final int numCols = Grid.BOARD_SIZE;
        final int numRows = Grid.BOARD_SIZE;
        view.getGrid().getChildren().clear();
        TileNode emptyTile = view.getEmptyTile();
        emptyTile.savePosition((numCols) / 2, (numRows) / 2);
        model.getGrid().setTile(emptyTile.getRow(), emptyTile.getCol(), emptyTile.getTile());
        validPositionList.add(emptyTile);
        view.getGrid().add(emptyTile, (numCols) / 2, (numRows) / 2);

    }
    //Tile exchange DragEvents
    private void swapTilesHandler() {
        view.getExchangeTiles().setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(tileFormat) && draggableTile != null) {
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });
        view.getExchangeTiles().setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(tileFormat)) {
                exchangedTiles.add(draggableTile);
                for (TileNode tile : deckTiles) {
                    if (tile.equals(draggableTile)) {
                        view.getActiveDeck().getChildren().remove(tile);
                        draggableTile.setStyle("");
                        e.setDropCompleted(true);
                    }
                }
            }
            e.consume();
        });
    }
    //DragEvent activation of deck tiles only
    private void makeDraggable(TileNode tileNode) {
        if (exchangedTiles.size() > 0) return;
        if (!(tileNode.getParent() instanceof GridPane)) {
            tileNode.setOnDragDetected(e -> {
                Dragboard db = tileNode.startDragAndDrop(TransferMode.ANY);
                Image img = new Image(tileNode.getTile().getIconImage().getImage().getUrl(), gridZoomOut(), gridZoomOut(), true, true);
                db.setDragView(img);
                ClipboardContent cc = new ClipboardContent();
                cc.put(tileFormat, " ");
                db.setContent(cc);
                draggableTile = tileNode;
                e.consume();
            });
        }
    }
    //Called by the welcome popup
    private void popupWhoPlaysFirst(Stage stage, String text) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, 2);
    }
    //Shows computer turn-score on a popup
    private void popupComputerPlayed(Stage stage, String text, String score, double duration) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text + score, 660, 300, duration);
    }
    //Animation popup before computer places tiles on the grid
    private void calculatingMove(Stage stage, String text) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text + "", 660, 300, 2, true);
    }
    //Popup message showing player's points after a turn
    private void popupPlayerPlayed(Stage stage) {
        String score = String.valueOf(model.getPlayerSession().getLastTurn().getPoints());
        PopupView view = new PopupView();
        String pointsText;
        if (model.getPlayerSession().getLastTurn().getPoints() == 1) {
            pointsText = " point";
        } else {
            pointsText = " points";
        }
        new PopupPresenter(stage, view, "You got " + score + pointsText, 660, 300, 1.4);
    }
    //Welcome popup showing who plays first
    private void welcomeMessage(Stage stage) {
        String whoPlaysFirst = String.format("%s %s", model.getActivePlayerSession().getPlayer().getName(), "plays first!");
        popupWhoPlaysFirst(stage, whoPlaysFirst);
    }
    //Called every time after a tile is placed on the gird. It attempts placing empty-grey tiles on empty positions on all 4 sides
    private void fillEmptySpots() {
        ArrayList<Node> nodes = new ArrayList<>(view.getGrid().getChildren());
        for (Node node : nodes) {
            if (((TileNode) node).hasTile()) {
                int col = ((TileNode) node).getCol();
                int row = ((TileNode) node).getRow();
                //Place left
                if (!containsTile(col - 1, row)) {
                    TileNode tileNode1 = getEmptyTile(col - 1, row);
                    view.getGrid().add(tileNode1, col - 1, row);
                    tileNode1.toBack();
                }
                //Place right
                if (!containsTile(col + 1, row)) {
                    TileNode tileNode2 = getEmptyTile(col + 1, row);
                    view.getGrid().add(tileNode2, col + 1, row);
                    tileNode2.toBack();
                }
                //Place up
                if (!containsTile(col, row - 1)) {
                    TileNode tileNode3 = getEmptyTile(col, row - 1);
                    view.getGrid().add(tileNode3, col, row - 1);
                    tileNode3.toBack();
                }
                //Place down
                if (!containsTile(col, row + 1)) {
                    TileNode tileNode4 = getEmptyTile(col, row + 1);
                    view.getGrid().add(tileNode4, col, row + 1);
                    tileNode4.toBack();
                }
            }
        }
        //Make those nodes "targets" for draggable deck tiles
        positioningHandler(validPositionList);
    }
    //Add grey-empty tile to the validPosition list and return it
    private TileNode getEmptyTile(int col, int row) {
        TileNode tileNode = new TileNode(gridZoomOut());
        tileNode.savePosition(col, row);
        validPositionList.add(tileNode);
        return validPositionList.getLast();
    }

    //Set target for dragged tiles (grey-empty tiles DragOver/DragDropped event) only after model validates that move (for Player only)
    //The part where the "communication" between the model grid and the GridPane is the most evident.
    private void positioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane && target.isEmpty()) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    //Create a move and pass it to the model only for validation (move of the fly and not saved to the model)
                    //Get target coordinates from the grid (View-GridPane)
                    Move currentMove = new Move(draggableTile.getTile()
                            , new Move.Coordinate(GridPane.getRowIndex(target), GridPane.getColumnIndex(target)));
                    //Only valid moves may accept target nodes (tiles). Validates a possible move only
                    if (db.hasContent(tileFormat) && draggableTile != null && model.getGrid().isValidMove(currentMove)) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    boolean success = false;
                    Node node = e.getPickResult().getIntersectedNode();
                    if (target.isEmpty() && db.hasContent(tileFormat)) {
                        Integer cIndex = GridPane.getColumnIndex(node);
                        Integer rIndex = GridPane.getRowIndex(node);
                        int col = cIndex == null ? 0 : cIndex;
                        int row = rIndex == null ? 0 : rIndex;
                        Move currentMove = new Move(draggableTile.getTile(), new Move.Coordinate(row, col));
                        //Only now passing the move to the model and the move is added to the user's turn
                        model.getPlayerSession().getLastTurn().add(currentMove);
                        //Validation per turn and update the model if valid
                        if (model.getGrid().isValidMoves(model.getPlayerSession().getLastTurn())) {
                            model.getPlayerSession().getPlayer().makeMove(currentMove);
                            playedTiles.add(draggableTile);
                            draggableTile.savePosition(col, row);
                            view.getGrid().add(draggableTile, col, row);
                            resetGridTileEvents(draggableTile);
                            deckTiles.remove(draggableTile);
                            draggableTile = null;
                            success = true;
                        }
                        if (!success) {
                            model.getPlayerSession().getLastTurn().removeLast();
                        }
                    }
                    e.setDropCompleted(success);
                    //Remove bouncing effect
                    removeTileEffect();
                    //Add empty tiles where possible
                    fillEmptySpots();
                    //Resize tiles if grid not big enough
                    resizeGridContent(gridZoomOut());
                    e.consume();
                });
            }
        }
    }
    //Checks if there is a tile placed or a grey empty tile is there
    private boolean containsTile(Integer col, Integer row) {
        boolean hasTile = false;
        for (Node node : view.getGrid().getChildren()) {
            if (((TileNode) node).getCol() == col && ((TileNode) node).getRow() == row) {
                hasTile = true;
                break;
            }
        }
        return hasTile;
    }
    //Checks only for played tiles and ignores empty-grey ones
    private boolean containsPlayedTile(Integer col, Integer row) {
        boolean hasTile = false;
        for (Node node : view.getGrid().getChildren()) {
            if (((TileNode) node).getCol() == col && ((TileNode) node).getRow() == row) {
                if (((TileNode) node).hasTile()) {
                    hasTile = true;
                }
            }
        }
        return hasTile;
    }
    //Starts the timer on the top right of the screen. It stops at gameOver
    private void timerSet() {
        DateFormat timeFormat = new SimpleDateFormat("mm:ss");
        timer = new Timeline(
                new KeyFrame(
                        Duration.millis(1000),
                        event -> {
                            final long diff = System.currentTimeMillis() - model.getStartTime().getTime();
                            view.getTime().setText(timeFormat.format(diff));
                        }
                )
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }
    //Method called only from within the "undo" method. It removes from the grid view all grey tiles that are not attached to played tiles
    private void cleanUpGrid() {
        ArrayList<Node> gridTiles = new ArrayList<>(view.getGrid().getChildren());
        for (Node gridTile : gridTiles) {
            int col = GridPane.getColumnIndex(gridTile);
            int row = GridPane.getRowIndex(gridTile);
            boolean hasTile = (containsPlayedTile(col - 1, row)) || (containsPlayedTile(col + 1, row))
                    || (containsPlayedTile(col, row - 1)) || (containsPlayedTile(col, row + 1));
            if (!hasTile && ((TileNode) gridTile).isEmpty()) {
                validPositionList.remove((TileNode) gridTile);
                view.getGrid().getChildren().remove(gridTile);
            }
        }
    }
    //Effect = grid zooms out. Actually the tiles become smaller to fit the grid when their length or height exceed the grid view size
    //This method just calculates the ideal size for the amount of tiles on the grid and it returns that size. See the next method
    //for the actual resizing
    private double gridZoomOut() {
        Set<Integer> columns = new HashSet<>();
        Set<Integer> rows = new HashSet<>();
        double defaultTileSize = 50;
        double defaultWidth = 950;
        double defaultHeight = 650;
        double defaultRows = defaultHeight / defaultTileSize + 1;
        double defaultColumns = defaultWidth / defaultTileSize + 1;
        for (Node node : view.getGrid().getChildren()) {
            if (((TileNode) node).hasTile()) {
                columns.add(((TileNode) node).getCol());
                rows.add(((TileNode) node).getRow());
            }
        }
        Node tileNode = view.getGrid().getChildren().get(rows.size());
        double rowsCounter = rows.size() + 2;
        double columnsCounter = columns.size() + 2;
        if (columnsCounter < defaultColumns && rowsCounter < defaultRows) {
            tileSize = defaultTileSize;
            return tileSize;
        }
        if ((rowsCounter * ((TileNode) tileNode).getHeight() > 650) && !(columnsCounter * ((TileNode) tileNode).getWidth() > 950)) {
            double numberOfCellsVertically = 650 / rowsCounter;
            String cellsVertically = "" + numberOfCellsVertically;
            String truncated = cellsVertically.substring(0, 2);
            tileSize = Integer.parseInt(truncated);
            return tileSize;
        }
        if ((columnsCounter * ((TileNode) tileNode).getWidth() > 950) && !(rowsCounter * ((TileNode) tileNode).getHeight() > 650)) {
            double numberOfCellsHorizontally = 950 / columnsCounter;
            String cellsHorizontally = "" + numberOfCellsHorizontally;
            String truncated = cellsHorizontally.substring(0, 2);
            tileSize = Integer.parseInt(truncated);
            return tileSize;
        }
        if ((columnsCounter * ((TileNode) tileNode).getWidth() > 950) && (rowsCounter * ((TileNode) tileNode).getHeight() > 650)) {
            double numberOfCellsHorizontally = 950 / columnsCounter;
            String cellsHorizontally = "" + numberOfCellsHorizontally;
            String truncated = cellsHorizontally.substring(0, 2);
            int columnsSize = Integer.parseInt(truncated);
            double numberOfCellsVertically = 650 / rowsCounter;
            String cellsVertically = "" + numberOfCellsVertically;
            String truncated1 = cellsVertically.substring(0, 2);
            int rowsSize = Integer.parseInt(truncated1);
            tileSize = Math.min(columnsSize, rowsSize);
        }
        return tileSize;
    }
    //It resizes all tiles based on the value returned from the previous method. It gets called after a tiles is played
    //or event after "undo" from within updateView (to zoomIn if necessary)
    private void resizeGridContent(double newSize) {
        for (Node node : view.getGrid().getChildren()) {
            ((TileNode) node).setWidth(newSize);
            ((TileNode) node).setHeight(newSize);

        }
    }
    //Remove bouncing mouseOver effect after the tile has been placed on the grid
    private void removeTileEffect() {
        for (Node node : view.getGrid().getChildren()) {
            node.setStyle(null);
        }
    }
    //Resize nodes used mostly on MouseOverEvents
    private void zoomIn(Node object, double start, double startTime) {
        KeyValue kv1 = new KeyValue(object.scaleXProperty(), (double) 1);
        KeyValue kv2 = new KeyValue(object.scaleYProperty(), (double) 1);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(startTime), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        object.setScaleX(start);
        object.setScaleY(start);
        seq.play();
    }
    //Quick resize effect on mouseOver event for the tiles on deck only
    private void deckTilesAnimation(Node node) {
        node.setOnMouseEntered(e -> {
            bounceNode(node, 1);
            e.consume();
        });
        node.setOnMouseExited(e -> {
            resetBounce(node);
            e.consume();
        });
    }
    //Resets the draggable property of tiles that were placed from the deck to the grid
    private void resetGridTileEvents(TileNode tile) {
        tile.setOnMouseExited(null);
        tile.setOnMouseEntered(null);
        tile.setOnDragDetected(null);

    }
    //MouseOver scale effect with animation (used for buttons)
    private void bounceNode(Node node, double startTime) {
        KeyValue kv1 = new KeyValue(node.scaleXProperty(), 1.1);
        KeyValue kv2 = new KeyValue(node.scaleYProperty(), 1.1);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(startTime), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        node.setScaleX(1);
        node.setScaleY(1);
        seq.play();
    }
    //It resets  the bouncing effect of the buttons
    private void resetBounce(Node node) {
        node.setScaleX(1);
        node.setScaleY(1);
    }
    //Welcome animation timers/keyframes. Sets computer move if computer plays first
    private void welcomeAnimation(Stage stage) {
        zoomIn(view.getGrid(), 0, 500);
        KeyFrame firstFrame = new KeyFrame(Duration.seconds(0.5), e -> welcomeMessage(stage));
        KeyFrame secondFrame = new KeyFrame(Duration.seconds(2.9), e -> {
            if (model.getActivePlayerSession().equals(model.getComputerSession())) {
                playComputerMove(stage);
            }
            welcome.stop();
        });
        welcome = new Timeline(firstFrame, secondFrame);
        welcome.play();
    }
    //Popup animation when player exchanges tiles
    private void popupTilesExchange(Stage stage) {
        String text = """
                Tiles exchanged successfully!""";
        KeyFrame firstFrame = new KeyFrame(Duration.seconds(0), e -> {
            popupMessage(stage, text, 1);
            updateView();
        });
        KeyFrame secondFrame = new KeyFrame(Duration.seconds(1.1), e -> {
            tileExchange.stop();
        });
        tileExchange = new Timeline(firstFrame, secondFrame);
        tileExchange.play();
    }
    //Main iteration of turns method. It gets called by the player when pressing submit or by the computer when played or
    //When a player has no moves to play (at end-game phase)
    private void iterateTurns(Stage stage) {
        playedTiles.clear();
        exchangedTiles.clear();
        //End the turn of the active player in the model (his score gets updated in the model and at the same time the popup
        //indicating the current turn points of the player or the computer is presented in a different timeline but synced to
        //play after endTurn gets active in the model)
        model.getActivePlayerSession().getLastTurn().endTurn(model.getGrid());
        KeyFrame kf1;
        KeyFrame kf2;
        //Different conditions while both players have still tiles on their decks
        if (!model.isGameOver()) {
            //None of the players has valid moves
            if (model.hasNoMoreMoves(model.getPlayerSession()) && model.hasNoMoreMoves(model.getComputerSession())) {
                kf1 = new KeyFrame(Duration.seconds(2), e -> popupMessage(stage, "No more valid moves\nfor any of the players", 2));
                kf2 = new KeyFrame(Duration.seconds(2.2), e -> {
                    setGameOver(stage);
                });
                iterateTurnsTM = new Timeline(kf1, kf2);
                iterateTurnsTM.play();
                return;
            }
            //Set next turn only when at least a player has a move to play
            model.setNextPlayerSession();
            updateView();
            if (model.getPlayerSession().isActive() && !model.hasNoMoreMoves(model.getComputerSession())) {
                if (model.hasNoMoreMoves(model.getPlayerSession())) {
                    kf1 = new KeyFrame(Duration.seconds(2), e -> {
                        popupMessage(stage, "You have no possible\n     moves to play", 2);
                    });
                    kf2 = new KeyFrame(Duration.seconds(4.2), e -> {
                        model.getActivePlayerSession().getLastTurn().endTurn(model.getGrid());
                        model.setNextPlayerSession();
                        playComputerMove(stage);
                    });
                    iterateTurnsTM = new Timeline(kf1, kf2);
                    iterateTurnsTM.play();
                }
            }
            //When a player played all of his tiles first (sets different keyframe timers) - regular gameOver condition met
        } else {
            kf1 = new KeyFrame(Duration.seconds(0));
            kf2 = new KeyFrame(Duration.seconds(1), e -> {
                setGameOver(stage);
            });
            iterateTurnsTM = new Timeline(kf1, kf2);
            iterateTurnsTM.play();
        }
    }
}
