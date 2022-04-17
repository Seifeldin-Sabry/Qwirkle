package qwirkle.view.gamePlayFrame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
    private LinkedList<TileNode> deckTiles = new LinkedList<>();
    private LinkedList<TileNode> exchangedTiles = new LinkedList<>();
    private LinkedList<TileNode> validPositionList = new LinkedList<>();
    private LinkedList<TileNode> playedTiles = new LinkedList<>();
    private TileNode draggableTile;
    private DataFormat tileFormat;
    private double tileSize;

    public GamePlayPresenter(Stage stage, GamePlayView view, GameSession model) {
        currentModel = model;
        currentView = view;
        tileFormat = new DataFormat("MyTile");

        try {
            updateView(stage);
            welcomeMessage(stage);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        addEventHandler(stage);
        timerSet();
        welcome = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            if (currentModel.getActivePlayerSession().equals(currentModel.getComputerSession())) {
                playComputerMove(stage);
            }
            welcome.stop();
        }));
        welcome.play();
    }

    private void addEventHandler(Stage stage) {
        currentView.getQuit().setOnAction(event -> setAlert(event, stage));
        currentView.getRules().setOnAction(this::setRulesView);
        currentView.getSubmit().setOnAction(event -> submit(stage));
        currentView.getUndo().setOnAction(event -> undo(stage));
        swapTilesHandler();
    }

    private void submitExchange(Stage stage) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (TileNode tileNode : exchangedTiles) {
            tiles.add(tileNode.getTile());
        }
        if (currentModel.getPlayerSession().getPlayer().getDeck().trade(currentModel.getBag(), tiles)) {
            exchangedTiles.clear();

            ArrayList<Node> nodes = new ArrayList<>(currentView.getGrid().getChildren());
            for (Node node : nodes) {
                for (TileNode tileNode : playedTiles) {
                    if (((TileNode) node).equals(tileNode)) {
                        currentView.getGrid().getChildren().remove(node);
                        currentModel.getGrid().setTile(tileNode.getRow(), tileNode.getCol(), null);
                        cleanUpGrid();
                    }
                }
            }
            currentModel.getPlayerSession().getLastTurn().getMoves().clear();
            playedTiles.clear();
            deckTiles.clear();
            draggableTile = null;
            validateTiles();
            updateView(stage);
        }
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
            if (computerMove != null) {
                computerMove.stop();
            }
            tileFormat = null;
            setWelcomeFrame(stage);
        }
    }

    private void setWelcomeFrame(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.currentView.getScene().setRoot(welcomeView);
        this.currentModel = null;
    }

    private void setRulesView(ActionEvent event) {
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
        if (exchangedTiles.size() > 0) {
            undo(stage);
            submitExchange(stage);
        }
        playedTiles.clear();
        exchangedTiles.clear();
        currentModel.setNextPlayerSession();
        updateView(stage);
        if (!currentModel.isGameOver()) {
            playComputerMove(stage);
        }
    }

    private void playComputerMove(Stage stage) {

        Computer computer = (Computer) currentModel.getComputerSession().getPlayer();


        Move move = computer.makeMove();
        currentModel.getComputerSession().getPlayer().makeMove(move);
        TileNode tileNode = new TileNode(move.getTile(), gridTileSize());
        tileNode.savePosition(move.getCoordinate().getColumn(), move.getCoordinate().getRow());
        currentModel.getComputerSession().getLastTurn().add(move);
        playedTiles.add(tileNode);



        placeTiles(playedTiles);
        currentModel.setNextPlayerSession();
        positioningHandler(validPositionList);
        updateView(stage);

        int points = currentModel.getComputerSession().getLastTurn().getPoints();
        String pointsLabel;
        if (currentModel.getComputerSession().getLastTurn().getPoints() == 1) {
            pointsLabel = " point";
        } else {
            pointsLabel = " points";
        }
        popupComputerPlayed(stage, "Computer Played: ", String.valueOf(points + pointsLabel), 2.2, true);

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
            tileNode.setStyle("-fx-effect: dropshadow( gaussian , rgb(190,0,0) , 2,1,0,0 );");
            computerMove.play();
        }

        playedTiles.clear();
    }

    private void updateView(Stage stage) {
        if (currentModel.isGameOver()) {
            currentModel.setEndTime();
            setGameOver(stage);
            return;
        }

        paintGrid();
        fillEmptySpots();
        setDeckTiles();

        currentView.getTilesLeft().setText("Tiles left: " + currentModel.getBag().getAmountOfTilesLeft());
        if (currentModel.getPlayerSession().size() > 0) {
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
        if (currentModel.getComputerSession().size() > 0) {
            currentView.getComputerScore().setText(String.format("Computer score: %s (+%s)", currentModel.getComputerSession()
                    .getTotalScore(), currentModel.getComputerSession().getLastTurn().getPoints()));
        } else {
            currentView.getComputerScore().setText("Computer Score:    " + currentModel.getComputerSession().getTotalScore());
        }


        positioningHandler(validPositionList);
        resizeGridContent(gridTileSize());

    }



    private void setGameOver(Stage stage) {
        Database.getInstance().save(currentModel);
        currentModel.getActivePlayerSession().getLastTurn().setPoints(currentModel.getActivePlayerSession().getLastTurn().getPoints() + 6);
        currentView.getVb2().getChildren().remove(currentView.getGrid());
        try {
            currentView.getVb2().getChildren().add(currentView.getVBox());
        } catch (IllegalArgumentException ignored) {
            //this works but it prints a lot of errors
            //this is to set the gameover screen instead of the grid
        }
        if (currentModel.getComputerSession().getTotalScore() > currentModel.getPlayerSession().getTotalScore()) {
            currentView.getLabel().setText("Computer won!");

        } else {
            currentView.getLabel().setText("You won!");
        }
        cancelButtons();
        currentView.makeTransparent();
        currentView.getStatistics().setOnAction(e -> {
            StatisticsView view = new StatisticsView();
            new StatisticsPresenterGO(view, currentView);
            currentView.getScene().setRoot(view);
        });
        currentView.getNewGame().setOnAction(e -> {
            timer.stop();
            if (computerMove != null) {
                computerMove.stop();
            }
            tileFormat = null;
            NewGameView view = new NewGameView();
            new NewGamePresenter(stage, view);
            currentView.getScene().setRoot(view);
        });
        currentView.getQuit().setOnAction(e -> {
            timer.stop();
            if (computerMove != null) {
                computerMove.stop();
            }
            tileFormat = null;
            setWelcomeFrame(stage);
        });
        timer.stop();
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
                deckTiles.add(new TileNode(currentModel.getPlayerSession().getPlayer().getDeck().getTilesInDeck().get(i), 50, 50));
                currentView.getActiveDeck().getChildren().addAll(deckTiles.get(i));
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

        currentView.getExchangeTiles().setOnMouseEntered(e -> {
            currentView.getExchangeTiles().setGraphic(currentView.getFullBagPopup());
        });
        currentView.getExchangeTiles().setOnMouseExited(e -> {
            currentView.getExchangeTiles().setGraphic(currentView.getFirstFullBag());
        });
    }

    private void makeDraggable(TileNode tileNode) {
        if (exchangedTiles.size() > 0) return;
        if (!(tileNode.getParent() instanceof GridPane)) {
            tileNode.setOnDragDetected(e -> {
                Dragboard db = tileNode.startDragAndDrop(TransferMode.ANY);
                Image img = new Image(tileNode.getTile().getIconImage().getImage().getUrl(), gridTileSize(), gridTileSize(), true, true);
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
        new PopupPresenter(stage, view, text, 660, 220, duration);
    }

    void popupComputerPlayed(Stage stage, String text, String score, double duration, boolean computerPlayed) {
        StringBuilder newText = new StringBuilder(text + score);
        PopupView view = new PopupView();
        new PopupPresenter(stage, view, newText.toString(), 660, 300, duration, computerPlayed);
    }

    private String whoPlaysFirst() {
        return String.format("%s %s", currentModel.getActivePlayerSession().getPlayer().getName(), "plays first!");
    }

    private void setExchangedTiles(TileNode tileNode) {
        exchangedTiles.add(tileNode);
    }

    private void welcomeMessage(Stage stage) {
        popupWhoPlaysFirst(stage, whoPlaysFirst(), 2);
    }

    private void fillEmptySpots() {
        ArrayList<Node> nodes = new ArrayList<>(currentView.getGrid().getChildren());
        for (Node node : nodes) {
            if (((TileNode) node).hasTile()) {
                int[] coordinates = getCoordinates(node);
                assert coordinates != null;
                int col = coordinates[0];
                int row = coordinates[1];
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
                        if (currentModel.getGrid().isValidMove(currentModel.getPlayerSession().getLastTurn())) {
//                            currentView.getGrid().getChildren().remove(draggableTile);
                            currentModel.getPlayerSession().getPlayer().makeMove(currentMove);
                            playedTiles.add(draggableTile);
                            draggableTile.savePosition(col, row);
                            currentView.getGrid().add(draggableTile, col, row);
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

                    resizeGridContent(gridTileSize());

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

    private int[] getCoordinates(Node node) {
        int[] i = new int[2];
        for (int column = 0; column < currentView.getGrid().getColumnCount(); column++) {
            for (int row = 0; row < currentView.getGrid().getRowCount(); row++) {
                if (currentView.getGrid().getChildren().contains(node)) {
                    int actualCol = GridPane.getColumnIndex(node);
                    int actualRow = GridPane.getRowIndex(node);
                    i[0] = actualCol;
                    i[1] = actualRow;
                    return i;
                }
            }
        }
        return null;
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

    private boolean containsNode(Integer col, Integer row) {
        for (Node node : currentView.getGrid().getChildren()) {
            if (((Objects.equals(GridPane.getColumnIndex(node), col)) && (Objects.equals(GridPane.getRowIndex(node), row))) && (node instanceof TileNode)) {
                return true;
            }
        }
        return false;
    }

    private TileNode getNodeByCoordinate(Integer column, Integer row) {
        TileNode tile = new TileNode();
        for (Node node : currentView.getGrid().getChildren()) {
            if ((((TileNode) node).getCol() == column) && (((TileNode) node).getRow() == row)) {
                tile = (TileNode) node;
            }
        }
        return tile;
    }

    private double gridTileSize() {
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
        System.out.println("rows: " + rowsCounter);
        if (columnsCounter < defaultColumns && rowsCounter < defaultRows) {
            tileSize = defaultTileSize;
            return tileSize;
        }
        if ((rowsCounter * ((TileNode) tileNode).getHeight() > 650) && !(columnsCounter * ((TileNode) tileNode).getWidth() > 950)) {
            double numberOfCellsVertically = 650 / rowsCounter;
            String cellsVertically = "" + numberOfCellsVertically;
            String truncated = cellsVertically.substring(0, 2);
            System.out.println(Integer.parseInt(truncated));
            tileSize = Integer.parseInt(truncated);
            return tileSize;
        }
        System.out.println("columns: " + columnsCounter);
        if ((columnsCounter * ((TileNode) tileNode).getWidth() > 950) && !(rowsCounter * ((TileNode) tileNode).getHeight() > 650)) {
            double numberOfCellsHorizontally = 950 / columnsCounter;
            String cellsHorizontally = "" + numberOfCellsHorizontally;
            String truncated = cellsHorizontally.substring(0, 2);
            System.out.println(Integer.parseInt(truncated));
            tileSize = Integer.parseInt(truncated);
            return tileSize;
        }
        if ((columnsCounter * ((TileNode) tileNode).getWidth() > 950) && (rowsCounter * ((TileNode) tileNode).getHeight() > 650)) {
            double numberOfCellsHorizontally = 950 / columnsCounter;
            String cellsHorizontally = "" + numberOfCellsHorizontally;
            String truncated = cellsHorizontally.substring(0, 2);
            System.out.println(Integer.parseInt(truncated));
            int columnsSize = Integer.parseInt(truncated);
            double numberOfCellsVertically = 650 / rowsCounter;
            String cellsVertically = "" + numberOfCellsVertically;
            String truncated1 = cellsVertically.substring(0, 2);
            System.out.println(Integer.parseInt(truncated1));
            int rowsSize = Integer.parseInt(truncated1);
            tileSize = Math.min(columnsSize, rowsSize);
        }
        return tileSize;
    }

    private void resizeGridContent(double size) {
        for (Node node : currentView.getGrid().getChildren()) {
            ((TileNode) node).setWidth(size);
            ((TileNode) node).setHeight(size);

        }
    }

    private void removeTileEffect() {
        for (Node node : currentView.getGrid().getChildren()) {
            node.setStyle(null);
        }
    }
}
