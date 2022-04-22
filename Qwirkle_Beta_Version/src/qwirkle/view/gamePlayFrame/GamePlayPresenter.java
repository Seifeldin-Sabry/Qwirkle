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
import qwirkle.view.newGameFrame.NewGamePresenter;
import qwirkle.view.newGameFrame.NewGameView;
import qwirkle.view.rulesFrame.RulesPresenterGP;
import qwirkle.view.rulesFrame.RulesView;
import qwirkle.view.statisticsFrame.StatisticsPresenterGO;
import qwirkle.view.statisticsFrame.StatisticsView;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GamePlayPresenter {

    private GamePlayView currentView;
    private List<GamePlayView> views = new ArrayList<>();
    private GameSession currentModel;
    private Timeline timer;
    private Timeline computerMove;
    private Timeline welcome;
    private Timeline tileExchange;
    private LinkedList<TileNode> deckTiles = new LinkedList<>();
    private LinkedList<TileNode> exchangedTiles = new LinkedList<>();
    private LinkedList<TileNode> validPositionList = new LinkedList<>();
    private LinkedList<TileNode> playedTiles = new LinkedList<>();
    private TileNode draggableTile;
    private static DataFormat tileFormat;
    private double tileSize;

    public GamePlayPresenter(Stage stage, GamePlayView view, GameSession model) {
        currentModel = model;
        currentView = view;
        if (tileFormat == null) {
            tileFormat = new DataFormat("MyTile");
        }
        updateView(stage);
        addEventHandler(stage);
        timerSet();
        welcomeAnimation(stage);
    }

    private void addEventHandler(Stage stage) {
        currentView.getQuit().setOnAction(event -> setAlert(event, stage));
        currentView.getRules().setOnAction(event -> setRulesView());
        currentView.getSubmit().setOnAction(event -> {
            submit(stage);
            zoomIn(currentView.getSubmit(), 0.8, 1, 100);
        });
        currentView.getSubmit().setOnMouseEntered(e -> bounchNode(currentView.getSubmit(), 1, 1.1, 100));
        currentView.getSubmit().setOnMouseExited(e -> resetBounch(currentView.getSubmit(), 1));
        currentView.getUndo().setOnAction(event -> {
            undo(stage);
            zoomIn(currentView.getUndo(), 0.8, 1, 100);
        });
        currentView.getUndo().setOnMouseEntered(e -> bounchNode(currentView.getUndo(), 1, 1.1, 100));
        currentView.getUndo().setOnMouseExited(e -> resetBounch(currentView.getUndo(), 1));
        currentView.getExchangeTiles().setOnMouseEntered(e -> currentView.getExchangeTiles().setGraphic(currentView.getFullBagPopup()));
        currentView.getExchangeTiles().setOnMouseExited(e -> currentView.getExchangeTiles().setGraphic(currentView.getFirstFullBag()));
        swapTilesHandler();
    }

    private void submitExchange(Stage stage) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (TileNode tileNode : exchangedTiles) {
            tiles.add(tileNode.getTile());
        }
        currentModel.getPlayerSession().getPlayer().getDeck().trade(currentModel.getBag(), tiles);
        currentModel.getPlayerSession().getLastTurn().getMoves().clear();
        playedTiles.clear();
        deckTiles.clear();
        draggableTile = null;
        validateTiles();
        updateView(stage);
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
        this.currentView.getScene().setRoot(welcomeView);
        this.currentModel = null;
    }

    private void setRulesView() {
        RulesView rulesView = new RulesView();
        new RulesPresenterGP(rulesView, this.currentView);
        currentView.getScene().setRoot(rulesView);
    }

    private void undo(Stage stage) {
        ArrayList<Node> nodes = new ArrayList<>(currentView.getGrid().getChildren());
        for (Node node : nodes) {
            for (TileNode tileNode : playedTiles) {
                if (((TileNode) node).equals(tileNode)) {
                    currentView.getGrid().getChildren().remove(node);
                    currentModel.getGrid().setTile(tileNode.getRow(), tileNode.getCol(), null);
                    tileNode.savePosition(0, 0);
                    currentView.getActiveDeck().getChildren().add(tileNode);
                    currentModel.getPlayerSession().getPlayer().getDeck().getTilesInDeck().add(tileNode.getTile());
                    cleanUpGrid();
                }
            }
        }
        currentModel.getPlayerSession().getLastTurn().getMoves().clear();
        exchangedTiles.clear();
        playedTiles.clear();
        deckTiles.clear();
        draggableTile = null;
        validateTiles();
        updateView(stage);
    }

    private void validateTiles() {
        for (Node node : currentView.getGrid().getChildren()) {
            if (((TileNode) node).isEmpty()) {
                validPositionList.add((TileNode) node);
            }
        }
    }

    private void submit(Stage stage) {
        if (exchangedTiles.size() > 0 && playedTiles.size() > 0) {
            String text = """
                    You can't trade tiles and place
                     them on the board at the same
                                        time!""";
            popupMessage(stage, text, 3, false);
            undo(stage);
            return;
        }
        if (exchangedTiles.size() > 0) {
            submitExchange(stage);
            tileExchangeAnimation(stage); //Contains playComputer method in a keyframe
            return;
        }
        if (playedTiles.size() == 0 && !(noMoreMovesLeft()) && currentModel.getBag().getTiles().size() > 0) {
            String text = """
                    Place a tile on the board or
                     exchange tiles in the bag.""";
            popupMessage(stage, text, 2.5, false);
            return;
        }
        if (playedTiles.size() > 0) {
            playedTiles.clear();
            exchangedTiles.clear();
            if (!currentModel.isGameOver()) {
                currentModel.setNextPlayerSession();
                updateView(stage);
                playComputerMove(stage);
            }
            return;
        }
        if (currentModel.isGameOver() || (currentModel.getComputerSession().getLastTurn().getMoves().size() == 0 && noMoreMovesLeft())) {
            setGameOver(stage);
            updateScore();
            Database.getInstance().save(currentModel);
        }
    }


    private void popupMessage(Stage stage, String text, double duration, boolean computerPlayed) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, duration, computerPlayed);
    }

    private boolean noMoreMovesLeft() {
        if (currentModel.getActivePlayerSession().equals(currentModel.getComputerSession())) {
            Computer computer = (Computer) currentModel.getComputerSession().getPlayer();
            Move move = computer.makeMove();
            if (move == null) {
                return true;
            }
        } else {
            for (Tile deckTile : currentModel.getActivePlayerSession().getPlayer().getDeck().getTilesInDeck()) {
                for (TileNode tile : validPositionList) {
                    int row = tile.getRow();
                    int col = tile.getCol();
                    Move move = new Move(deckTile, new Move.Coordinate(row, col));
                    if (currentModel.getGrid().isValidMove(move)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void playComputerMove(Stage stage) {
        Computer computer = (Computer) currentModel.getComputerSession().getPlayer();
        Move move = computer.makeMove();
        if (move == null && currentModel.getBag().getTiles().size() > 0) {
            ((Computer) currentModel.getComputerSession().getPlayer()).trade();
            exchangedTiles.clear();
            popupComputerPlayed(stage, "Computer traded tiles", "", 2.2, true);
            currentModel.setNextPlayerSession();
            return;
        }
        if (move != null) {
            currentModel.getComputerSession().getPlayer().makeMove(move);
            TileNode tileNode = new TileNode(move.getTile(), gridZoomOut());
            tileNode.savePosition(move.getCoordinate().getColumn(), move.getCoordinate().getRow());
            currentModel.getComputerSession().getLastTurn().add(move);
            playedTiles.add(tileNode);
            placeTiles(playedTiles);
            currentModel.setNextPlayerSession();
            int points = currentModel.getComputerSession().getLastTurn().getPoints();
            String pointsLabel;
            if (currentModel.getComputerSession().getLastTurn().getPoints() == 1) {
                pointsLabel = " point";
            } else {
                pointsLabel = " points";
            }
            popupComputerPlayed(stage, "Computer Played: ", String.valueOf(points + pointsLabel), 2.2, true);
            positioningHandler(validPositionList);
            playedTiles.clear();
            updateView(stage);
            return;
        }
        if ((move == null && currentModel.getPlayerSession().getLastTurn().getMoves().size() == 0
                && currentModel.getBag().getTiles().size() == 0) || currentModel.isGameOver()) {
            setGameOver(stage);
            updateScore();
            Database.getInstance().save(currentModel);
        }
    }


    private void placeTiles(LinkedList<TileNode> playedTiles) {
        for (TileNode tileNode : playedTiles) {
            computerMove = new Timeline(new KeyFrame(Duration.seconds(6), e -> {
                fillEmptySpots();
                removeTileEffect();
                computerMove.stop();
            }));
            computerMove.setDelay(Duration.seconds(1));
            currentView.getGrid().add(tileNode, tileNode.getCol(), tileNode.getRow());
            tileNode.setStyle("-fx-effect: dropshadow( gaussian , rgb(255,0,0) , 4,1,0,0 );");
            computerMove.play();
        }

        playedTiles.clear();
    }

    private void updateView(Stage stage) {
        paintGrid();
        fillEmptySpots();
        setDeckTiles();
        updateTilesLeftLabel();
        updateScore();
        positioningHandler(validPositionList);
        resizeGridContent(gridZoomOut());
    }

    private void updateTilesLeftLabel() {
        currentView.getTilesLeft().setText("Tiles left: " + currentModel.getBag().getAmountOfTilesLeft());
    }

    private void updateScore() {
        if (currentModel.getPlayerSession().size() > 0 && currentModel.getPlayerSession().getTotalScore() > 0) {
            int playerPoints;
            try {
                playerPoints = currentModel.getPlayerSession().get(currentModel.getPlayerSession().indexOf(currentModel.getPlayerSession().getLastTurn()) - 1).getPoints();
            } catch (IndexOutOfBoundsException e) {
                playerPoints = 0;
            }
            currentView.getPlayerScore().setText(String.format("Your score: %s (+%s)", currentModel.getPlayerSession()
                    .getTotalScore(), playerPoints));
        } else {
            currentView.getPlayerScore().setText("Your Score:    " + currentModel.getPlayerSession().getTotalScore());
        }
        if (currentModel.getComputerSession().size() > 0 && currentModel.getComputerSession().getTotalScore() > 0) {
            currentView.getComputerScore().setText(String.format("Computer score: %s (+%s)", currentModel.getComputerSession()
                    .getTotalScore(), currentModel.getComputerSession().getLastTurn().getPoints()));
        } else {
            currentView.getComputerScore().setText("Computer Score:    " + currentModel.getComputerSession().getTotalScore());
        }
    }


    private void setGameOver(Stage stage) {
        currentModel.getActivePlayerSession().getLastTurn().endTurn(currentModel.getGrid());
        currentModel.setEndTime();
        timer.stop();
        currentView.getVb2().getChildren().clear();
        currentModel.addExtraPoints();
        currentView.getVb2().getChildren().addAll(currentView.getHbScore(), currentView.getVBox());
        if (currentModel.getComputerSession().getTotalScore() > currentModel.getPlayerSession().getTotalScore()) {
            currentView.getLabel().setText("Computer won!");
        } else {
            currentView.getLabel().setText("You won!");
        }
        zoomIn(currentView.getGameOverImage(), 0, 1, 500);
        cancelButtons();
        currentView.makeTransparent();
        currentView.getStatistics().setOnAction(e -> {
            StatisticsView view = new StatisticsView();
            new StatisticsPresenterGO(view, currentView);
            currentView.getScene().setRoot(view);
        });
        currentView.getNewGame().setOnAction(e -> {
            NewGameView view = new NewGameView();
            new NewGamePresenter(stage, view);
            currentView.getScene().setRoot(view);
        });
        currentView.getQuit().setOnAction(e -> {
            setWelcomeFrame(stage);
        });
    }

    private void cancelButtons() {
        currentView.getSubmit().setOnAction(null);
        currentView.getSubmit().setStyle("-fx-cursor: pointer;");
        currentView.getUndo().setOnAction(null);
        currentView.getUndo().setStyle("-fx-cursor: pointer;");
        currentView.getExchangeTiles().setOnAction(null);
        currentView.getExchangeTiles().setStyle("-fx-cursor: pointer;");
    }


    //Retrieve the distributed tiles and create a list
    private void setDeckTiles() {
        int tilesDistributed = currentModel.getPlayerSession().getPlayer().getDeck().getTilesInDeck().size();
        currentView.getActiveDeck().getChildren().clear();
        deckTiles.clear();
        draggableTile = new TileNode();
        if (tilesDistributed != 0) {
            for (int i = 0; i < tilesDistributed; i++) {
                deckTiles.add(new TileNode(currentModel.getPlayerSession().getPlayer().getDeck().getTilesInDeck().get(i), 50));
                currentView.getActiveDeck().getChildren().addAll(deckTiles.get(i));
                deckTilesAnimation(deckTiles.get(i));
                makeDraggable((TileNode) currentView.getActiveDeck().getChildren().get(i));
            }
        }
    }

    private void paintGrid() {
        final int numCols = Grid.BOARD_SIZE;
        final int numRows = Grid.BOARD_SIZE;
        if ((currentModel.getPlayerSession().getTurnsPlayed().size() == 1 && currentModel.getComputerSession().getTurnsPlayed().size() == 0)
                || (currentModel.getPlayerSession().getTurnsPlayed().size() == 0 && currentModel.getComputerSession().getTurnsPlayed().size() == 1)) {
            currentView.getGrid().getChildren().clear();
            TileNode emptyTile = currentView.getEmptyTile();
            emptyTile.savePosition((numCols) / 2, (numRows) / 2);
            currentModel.getGrid().setTile(emptyTile.getRow(), emptyTile.getCol(), emptyTile.getTile());
            validPositionList.add(emptyTile);
            currentView.getGrid().add(emptyTile, (numCols) / 2, (numRows) / 2);
        }
    }

    private void swapTilesHandler() {
        currentView.getExchangeTiles().setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(tileFormat) && draggableTile != null) {
                e.acceptTransferModes(TransferMode.ANY);
            }
            e.consume();
        });
        currentView.getExchangeTiles().setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(tileFormat)) {
                setExchangedTiles(draggableTile);
                for (TileNode tile : deckTiles) {
                    if (tile.equals(draggableTile)) {
                        views.add(currentView);
                        currentView.getActiveDeck().getChildren().remove(tile);
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

    void popupWhoPlaysFirst(Stage stage, String text, int duration) {
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, text, 660, 300, duration);
    }

    void popupComputerPlayed(Stage stage, String text, String score, double duration, boolean computerPlayed) {
        StringBuilder newText = new StringBuilder(text + score);
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, newText.toString(), 660, 300, duration, computerPlayed);
    }

    private void setExchangedTiles(TileNode tileNode) {
        exchangedTiles.add(tileNode);
    }

    private void welcomeMessage(Stage stage) {
        String whoPlaysFirst = String.format("%s %s", currentModel.getActivePlayerSession().getPlayer().getName(), "plays first!");
        popupWhoPlaysFirst(stage, whoPlaysFirst, 2);
    }

    private void fillEmptySpots() {
        ArrayList<Node> nodes = new ArrayList<>(currentView.getGrid().getChildren());
        for (Node node : nodes) {
            if (((TileNode) node).hasTile()) {
                int col = ((TileNode) node).getCol();
                int row = ((TileNode) node).getRow();
                //Place left
                if (!containsTile(col - 1, row)) {
                    TileNode tileNode1 = getEmptyTile(col - 1, row);
                    currentView.getGrid().add(tileNode1, col - 1, row);
                    tileNode1.toBack();
                }
                //Place right
                if (!containsTile(col + 1, row)) {
                    TileNode tileNode2 = getEmptyTile(col + 1, row);
                    currentView.getGrid().add(tileNode2, col + 1, row);
                    tileNode2.toBack();
                }
                //Place up
                if (!containsTile(col, row - 1)) {
                    TileNode tileNode3 = getEmptyTile(col, row - 1);
                    currentView.getGrid().add(tileNode3, col, row - 1);
                    tileNode3.toBack();
                }
                //Place Down
                if (!containsTile(col, row + 1)) {
                    TileNode tileNode4 = getEmptyTile(col, row + 1);
                    currentView.getGrid().add(tileNode4, col, row + 1);
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
                    if (db.hasContent(tileFormat) && draggableTile != null && currentModel.getGrid().isValidMove(currentMove)) {
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
                        currentModel.getPlayerSession().getLastTurn().add(currentMove);
                        if (currentModel.getGrid().isValidMoves(currentModel.getPlayerSession().getLastTurn())) {
                            currentModel.getPlayerSession().getPlayer().makeMove(currentMove);
                            playedTiles.add(draggableTile);
                            draggableTile.savePosition(col, row);
                            currentView.getGrid().add(draggableTile, col, row);
                            resetGridTileAnimation(draggableTile);
                            deckTiles.remove(draggableTile);
                            draggableTile = null;
                            success = true;
                        }
                        if (!success) {
                            currentModel.getPlayerSession().getLastTurn().removeLast();

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
        for (Node node : currentView.getGrid().getChildren()) {
            if (((TileNode) node).getCol() == col && ((TileNode) node).getRow() == row) {
                hasTile = true;
            }
        }
        return hasTile;
    }

    private boolean containsPlayedTile(Integer col, Integer row) {
        boolean hasTile = false;
        for (Node node : currentView.getGrid().getChildren()) {
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
                            final long diff = System.currentTimeMillis() - currentModel.getStartTime().getTime();
                            currentView.getTime().setText(timeFormat.format(diff));
                        }
                )
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private void cleanUpGrid() {
        ArrayList<Node> gridTiles = new ArrayList<>(currentView.getGrid().getChildren());
        for (Node gridTile : gridTiles) {
            int col = GridPane.getColumnIndex(gridTile);
            int row = GridPane.getRowIndex(gridTile);
            boolean hasTile = false;
            //Place left
            if ((containsPlayedTile(col - 1, row)) || (containsPlayedTile(col + 1, row)) || (containsPlayedTile(col, row - 1)) || (containsPlayedTile(col, row + 1))) {
                hasTile = true;
            }
            if (!hasTile && ((TileNode) gridTile).isEmpty()) {
                validPositionList.remove((TileNode) gridTile);
                currentView.getGrid().getChildren().remove(gridTile);
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
        for (Node node : currentView.getGrid().getChildren()) {
            if (((TileNode) node).hasTile()) {
                columns.add(((TileNode) node).getCol());
                rows.add(((TileNode) node).getRow());
            }
        }
        Node tileNode = currentView.getGrid().getChildren().get(rows.size());
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
        for (Node node : currentView.getGrid().getChildren()) {
            ((TileNode) node).setWidth(newSize);
            ((TileNode) node).setHeight(newSize);

        }
    }

    private void removeTileEffect() {
        for (Node node : currentView.getGrid().getChildren()) {
            node.setStyle(null);
        }
    }

    private void zoomIn(Node object, double start, double end, double duration) {
        KeyValue kv1 = new KeyValue(object.scaleXProperty(), end);
        KeyValue kv2 = new KeyValue(object.scaleYProperty(), end);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(duration), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        object.setScaleX(start);
        object.setScaleY(start);
        seq.play();
    }

    private void deckTilesAnimation(Node node) {
        node.setOnMouseEntered(e -> {
            bounchNode(node, 1, 1.1, 1);
            e.consume();
        });
        node.setOnMouseExited(e -> {
            resetBounch(node, 1);
            e.consume();
        });
    }

    private void resetGridTileAnimation(TileNode tile) {
        tile.setOnMouseExited(null);
        tile.setOnMouseEntered(null);
    }

    private void bounchNode(Node node, double oldSize, double newSize, double duration) {
        KeyValue kv1 = new KeyValue(node.scaleXProperty(), newSize);
        KeyValue kv2 = new KeyValue(node.scaleYProperty(), newSize);
        Timeline scaling = new Timeline(new KeyFrame(Duration.millis(duration), kv1, kv2));
        SequentialTransition seq = new SequentialTransition(scaling);
        node.setScaleX(oldSize);
        node.setScaleY(oldSize);
        seq.play();
    }

    private void resetBounch(Node node, double newSize) {
        node.setScaleX(newSize);
        node.setScaleY(newSize);
    }

    private void welcomeAnimation(Stage stage) {
        zoomIn(currentView.getGrid(), 0, 1, 500);
        KeyFrame firstFrame = new KeyFrame(Duration.seconds(0.5), e -> {
            welcomeMessage(stage);
        });
        KeyFrame secondFrame = new KeyFrame(Duration.seconds(2.5), e -> {
            if (currentModel.getActivePlayerSession().equals(currentModel.getComputerSession())) {
                playComputerMove(stage);
            }
            welcome.stop();
        });
        welcome = new Timeline(firstFrame, secondFrame);
        welcome.play();
    }

    private void tileExchangeAnimation(Stage stage) {
        String text = """
                Tiles exchanged successfully!""";
        ;
        KeyFrame firstFrame = new KeyFrame(Duration.seconds(0), e -> {
            popupMessage(stage, text, 1, false);
            updateView(stage);
        });
        KeyFrame secondFrame = new KeyFrame(Duration.seconds(1.6), e -> {
            if (!currentModel.isGameOver()) {
                playedTiles.clear();
                exchangedTiles.clear();
                currentModel.setNextPlayerSession();
                playComputerMove(stage);
                tileExchange.stop();
            }
        });
        tileExchange = new Timeline(firstFrame, secondFrame);
        tileExchange.play();
    }
}
