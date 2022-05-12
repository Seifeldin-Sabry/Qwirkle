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

public class GamePlayPresenter {

    private final GamePlayView view;
    private GameSession model;
    private Timeline timer;
    private Timeline iterateTurnsTM;
    private Timeline welcome;
    private Timeline tileExchange;
    private Timeline submit;
    private Timeline gameOver;
    private Timeline computerTurn;
    private final LinkedList<TileNode> deckTiles = new LinkedList<>();
    private final LinkedList<TileNode> exchangedTiles = new LinkedList<>();
    private final LinkedList<TileNode> validPositionList = new LinkedList<>();
    private final LinkedList<TileNode> playedTiles = new LinkedList<>();
    private TileNode draggableTile;
    private static DataFormat tileFormat;
    private double tileSize;

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

    private void setWelcomeFrame(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.view.getScene().setRoot(welcomeView);
        this.model = null;
    }

    private void setRulesView() {
        RulesView rulesView = new RulesView();
        new RulesPresenterGP(rulesView, this.view);
        view.getScene().setRoot(rulesView);
    }

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


    private void validateTiles() {
        for (Node node : view.getGrid().getChildren()) {
            if (((TileNode) node).isEmpty()) {
                validPositionList.add((TileNode) node);
            }
        }
    }

    private void submit(Stage stage) {
        if (computerTurn != null) {
            computerTurn.stop();
        }
        if (iterateTurnsTM != null) {
            iterateTurnsTM.stop();
        }
        if (playedTiles.size() > 0 && exchangedTiles.size() == 0) {
            KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0.05), e -> {
                iterateTurns(stage);
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
        if (playedTiles.size() == 0 && model.getBag().getAmountOfTilesLeft() > 0 && exchangedTiles.size() == 0) {
            String text = """
                    Place a tile on the board or
                     exchange tiles in the bag.""";
            popupMessage(stage, text, 2.5);
            return;
        }
        if (!model.hasNoMoreMoves(model.getPlayerSession()) && playedTiles.size() == 0) {
            String text = """
                    There is at least 1 more
                           available move.""";
            popupMessage(stage, text, 2.5);
            return;
        }
        if (exchangedTiles.size() > 0 && playedTiles.size() > 0) {
            String text = """
                    You can't trade tiles and place
                     them on the board at the same
                                        time!""";
            popupMessage(stage, text, 3);
            undo();
        }
    }

    private void popupMessage(Stage stage, String text, double startTime) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, startTime, false);
    }

    private void playComputerMove(Stage stage) {
        if (submit != null) {
            submit.stop();
        }
        if (iterateTurnsTM != null) {
            iterateTurnsTM.stop();
        }
        List<Move> moves = model.getComputerSession().getPlayer() instanceof ComputerAI ?
                ((ComputerAI) model.getComputerSession().getPlayer()).makeTurn(model.getComputerSession().indexOf(model.getComputerSession().getLastTurn()) + 1)
                : ((Computer) model.getComputerSession().getPlayer()).makeTurn();

        KeyFrame kf1 = new KeyFrame(Duration.seconds(0.1), e -> {
            if (moves == null && model.getBag().getTiles().size() > 0) {
                popupComputerPlayed(stage, "Computer traded tiles", "", 1.8);
                iterateTurns(stage);
                computerTurn.stop();
            }
            if (moves == null && model.getBag().getTiles().size() == 0) {
                popupComputerPlayed(stage, "Computer has no moves to play", "", 1.8);
                iterateTurns(stage);
                computerTurn.stop();
            }
            if (moves != null && moves.size() > 0) {
                calculatingMove(stage, "Calculating move");
                for (Move move : moves) {
                    model.getComputerSession().getPlayer().makeMove(move);
                    model.getComputerSession().getLastTurn().add(move);
                    TileNode tileNode = new TileNode(move.getTile(), gridZoomOut());
                    tileNode.savePosition(move.getCoordinate().getColumn(), move.getCoordinate().getRow());
                    playedTiles.add(tileNode);
                }
            }
        });
        KeyFrame kf2 = new KeyFrame(Duration.seconds(2.35), e -> {
            placeTiles(playedTiles);
            iterateTurns(stage);
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


    private void placeTiles(LinkedList<TileNode> playedTiles) {
        for (TileNode tileNode : playedTiles) {
            fillEmptySpots();
            view.getGrid().add(tileNode, tileNode.getCol(), tileNode.getRow());
            tileNode.setStyle("-fx-effect: dropshadow( gaussian , rgb(255,0,0) , 4,1,0,0 );");
        }
        playedTiles.clear();
    }

    private void updateView() {
        paintGrid();
        fillEmptySpots();
        setDeckTiles();
        updateScore();
        updateTilesLeftLabel();
        resizeGridContent(gridZoomOut());
    }

    private void updateTilesLeftLabel() {
        view.getTilesLeft().setText("Tiles left: " + model.getBag().getAmountOfTilesLeft());
    }

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

    private void setGameOver(Stage stage) {
        timer.stop();
        iterateTurnsTM.stop();
        model.setEndTime();
        model.getActivePlayerSession().getLastTurn().endTurn(model.getGrid());
        if (model.isGameOver()) {
            model.addExtraPoints();
            updateScore();
        }
        Database.getInstance().save(model);
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0));
        double duration = 0;
        if (model.isGameOver()) {
            if (model.getPlayerSession().isActive()) {
                keyFrame1 = new KeyFrame(Duration.seconds(1.2), e -> popupMessage(stage, "You got 6 extra points\n   for finishing first!", 1.8));
            } else if (model.getComputerSession().isActive()) {
                keyFrame1 = new KeyFrame(Duration.seconds(1.2), e -> popupMessage(stage, "Computer got 6 bonus points\n      for finishing first!", 1.8));
            }
            duration = 3.2;
        }
        if (!model.isGameOver()) {
            duration = 2;
        }
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

    private void cancelButtons() {
        view.getSubmit().setOnAction(null);
        view.getSubmit().setStyle("-fx-cursor: pointer;");
        view.getUndo().setOnAction(null);
        view.getUndo().setStyle("-fx-cursor: pointer;");
        view.getExchangeTiles().setOnAction(null);
        view.getExchangeTiles().setStyle("-fx-cursor: pointer;");
    }

    private void setDeckTiles() {
        int tilesDistributed = model.getPlayerSession().getPlayer().getDeck().getTilesInDeck().size();
        view.getActiveDeck().getChildren().clear();
        deckTiles.clear();
        draggableTile = new TileNode();
        if (tilesDistributed != 0) {
            for (int i = 0; i < tilesDistributed; i++) {
                deckTiles.add(new TileNode(model.getPlayerSession().getPlayer().getDeck().getTilesInDeck().get(i), 50));
                view.getActiveDeck().getChildren().addAll(deckTiles.get(i));
                deckTilesAnimation(deckTiles.get(i));
                makeDraggable((TileNode) view.getActiveDeck().getChildren().get(i));
            }
        }
    }

    private void paintGrid() {
        final int numCols = Grid.BOARD_SIZE;
        final int numRows = Grid.BOARD_SIZE;
        if ((model.getPlayerSession().getTurnsPlayed().size() == 1 && model.getComputerSession().getTurnsPlayed().size() == 0)
                || (model.getPlayerSession().getTurnsPlayed().size() == 0 && model.getComputerSession().getTurnsPlayed().size() == 1)) {
            view.getGrid().getChildren().clear();
            TileNode emptyTile = view.getEmptyTile();
            emptyTile.savePosition((numCols) / 2, (numRows) / 2);
            model.getGrid().setTile(emptyTile.getRow(), emptyTile.getCol(), emptyTile.getTile());
            validPositionList.add(emptyTile);
            view.getGrid().add(emptyTile, (numCols) / 2, (numRows) / 2);
        }
    }

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
                setExchangedTiles(draggableTile);
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


    private void popupWhoPlaysFirst(Stage stage, String text) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, 2);
    }

    private void popupComputerPlayed(Stage stage, String text, String score, double duration) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text + score, 660, 300, duration, false);
    }

    private void calculatingMove(Stage stage, String text) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text + "", 660, 300, 2, true);
    }

    private void popupPlayerPlayed(Stage stage) {
        String score = String.valueOf(model.getPlayerSession().getLastTurn().getPoints());
        PopupView view = new PopupView();
        String pointsText;
        if (model.getPlayerSession().getLastTurn().getPoints() == 1) {
            pointsText = " point";
        } else {
            pointsText = " points";
        }
        new PopupPresenter(stage, view, "You got " + score + pointsText, 660, 300, 1.4, false);
    }

    private void setExchangedTiles(TileNode tileNode) {
        exchangedTiles.add(tileNode);
    }

    private void welcomeMessage(Stage stage) {
        String whoPlaysFirst = String.format("%s %s", model.getActivePlayerSession().getPlayer().getName(), "plays first!");
        popupWhoPlaysFirst(stage, whoPlaysFirst);
    }

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
                //Place Down
                if (!containsTile(col, row + 1)) {
                    TileNode tileNode4 = getEmptyTile(col, row + 1);
                    view.getGrid().add(tileNode4, col, row + 1);
                    tileNode4.toBack();
                }
            }
        }
        positioningHandler(validPositionList);
    }

    private TileNode getEmptyTile(int col, int row) {
        TileNode tileNode = new TileNode();
        tileNode.savePosition(col, row);
        validPositionList.add(tileNode);
        return validPositionList.getLast();
    }

    //Set target for dragged tiles
    private void positioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane && target.isEmpty()) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    Move currentMove = new Move(draggableTile.getTile()
                            , new Move.Coordinate(GridPane.getRowIndex(target), GridPane.getColumnIndex(target)));
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
                        model.getPlayerSession().getLastTurn().add(currentMove);
                        if (model.getGrid().isValidMoves(model.getPlayerSession().getLastTurn())) {
                            model.getPlayerSession().getPlayer().makeMove(currentMove);
                            playedTiles.add(draggableTile);
                            draggableTile.savePosition(col, row);
                            view.getGrid().add(draggableTile, col, row);
                            resetGridTileAnimation(draggableTile);
                            deckTiles.remove(draggableTile);
                            draggableTile = null;
                            success = true;
                        }
                        if (!success) {
                            model.getPlayerSession().getLastTurn().removeLast();

                        }
                    }
                    e.setDropCompleted(success);
                    removeTileEffect();
                    fillEmptySpots();
                    resizeGridContent(gridZoomOut());

                    e.consume();
                });
            }
        }
    }

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

    private void cleanUpGrid() {
        ArrayList<Node> gridTiles = new ArrayList<>(view.getGrid().getChildren());
        for (Node gridTile : gridTiles) {
            int col = GridPane.getColumnIndex(gridTile);
            int row = GridPane.getRowIndex(gridTile);
            boolean hasTile = false;
            //Place left
            if ((containsPlayedTile(col - 1, row)) || (containsPlayedTile(col + 1, row))
                    || (containsPlayedTile(col, row - 1)) || (containsPlayedTile(col, row + 1))) {
                hasTile = true;
            }
            if (!hasTile && ((TileNode) gridTile).isEmpty()) {
                validPositionList.remove((TileNode) gridTile);
                view.getGrid().getChildren().remove(gridTile);
            }
        }
    }

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

    private void resizeGridContent(double newSize) {
        for (Node node : view.getGrid().getChildren()) {
            ((TileNode) node).setWidth(newSize);
            ((TileNode) node).setHeight(newSize);

        }
    }

    private void removeTileEffect() {
        for (Node node : view.getGrid().getChildren()) {
            node.setStyle(null);
        }
    }

    private void zoomIn(Node object, double start, double startTime) {
        KeyValue kv1 = new KeyValue(object.scaleXProperty(), (double) 1);
        KeyValue kv2 = new KeyValue(object.scaleYProperty(), (double) 1);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(startTime), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        object.setScaleX(start);
        object.setScaleY(start);
        seq.play();
    }

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

    private void resetGridTileAnimation(TileNode tile) {
        tile.setOnMouseExited(null);
        tile.setOnMouseEntered(null);
        tile.setOnDragDetected(null);
    }

    private void bounceNode(Node node, double startTime) {
        KeyValue kv1 = new KeyValue(node.scaleXProperty(), 1.1);
        KeyValue kv2 = new KeyValue(node.scaleYProperty(), 1.1);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(startTime), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        node.setScaleX(1);
        node.setScaleY(1);
        seq.play();
    }

    private void resetBounce(Node node) {
        node.setScaleX(1);
        node.setScaleY(1);
    }

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

    private void iterateTurns(Stage stage) {
        playedTiles.clear();
        exchangedTiles.clear();
        model.getActivePlayerSession().getLastTurn().endTurn(model.getGrid());
        KeyFrame kf1;
        KeyFrame kf2;
        if (!model.isGameOver()) {
            if (model.hasNoMoreMoves(model.getPlayerSession()) && model.hasNoMoreMoves(model.getComputerSession())) {
                kf1 = new KeyFrame(Duration.seconds(2), e -> popupMessage(stage, "No more valid moves\nfor any of the players", 2));
                kf2 = new KeyFrame(Duration.seconds(2.2), e -> {
                    setGameOver(stage);
                });
                iterateTurnsTM = new Timeline(kf1, kf2);
                iterateTurnsTM.play();
                return;
            }
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
