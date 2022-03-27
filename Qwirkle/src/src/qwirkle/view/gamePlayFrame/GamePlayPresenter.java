package src.qwirkle.view.gamePlayFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import qwirkle.model.GameSession;
import qwirkle.model.Grid;
import qwirkle.model.Tile;
import qwirkle.view.rulesFrame.RulesPresenterGP;
import qwirkle.view.rulesFrame.RulesView;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GamePlayPresenter {

    private GamePlayView currentView;
    private GamePlayView previousView;
    private List<GamePlayView> views = new ArrayList<>();
    private GameSession currentModel;
    private GameSession rollBack;
    private Thread thread;
    private LinkedList<TileNode> deckTiles = new LinkedList<>();
    private LinkedList<TileNode> exchangedTiles = new LinkedList<>();
    private LinkedList<TileNode> validPositionList = new LinkedList<>();
    private TileNode draggableTile;
    private DataFormat tileFormat = new DataFormat("MyTile");

    public GamePlayPresenter(Stage stage, GamePlayView view, GameSession model) {
        currentModel = model;
        currentView = view;
        previousView = view;
        rollBack = model;
        timerSet();
        try {
            updateView();
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        addEventHandler(stage);
        welcomeMessage(stage);
    }

    private void addEventHandler(Stage stage) {
        currentView.getQuit().setOnAction(event -> setAlert(event, stage));
        currentView.getRules().setOnAction(this::setRulesView);
        currentView.getSubmit().setOnAction(event -> submit());
        currentView.getUndo().setOnAction(event -> undo());
        swapTilesHandler();
        currentView.getExchangeTiles().setOnAction(event -> {
            ArrayList<Tile> tiles = new ArrayList<>();
            for (TileNode tileNode : exchangedTiles){
                tiles.add(tileNode.getTile());
            }
            if (currentModel.getPlayerHuman().getDeck().trade(currentModel.getBag(), tiles)) {
                exchangedTiles.clear();
                ////////////////////////// for testing
                System.out.println("new tiles:" + deckTiles);
                System.out.println("Tiles left: " + currentModel.getBag().getAmountOfTilesLeft());
                //////////////////////////
                updateView();
            } else System.out.println("Tiles left not enough");
        });
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
            this.thread.stop();
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

    private void undo() {
        currentView = previousView;
        currentModel = rollBack;
        validPositionList.clear();
        exchangedTiles.clear();
        draggableTile = null;
        /////////////////////for testing
        System.out.println("Undo triggered");
        /////////////////////
        updateView();
    }
    private void submit(){
        previousView = currentView;
        //To do... implement Move and Turn of the model for player and initiate Computer moves
        currentModel.getComputer().makeMove();
        rollBack = currentModel;
        updateView();
    }

    private void updateView() {
        paintGrid();
        setDeckTiles();
        currentView.getTilesLeft().setText("Tiles left: " + currentModel.getBag().getAmountOfTilesLeft());
        currentView.getPlayerScore().setText("Your Score:    " + currentModel.getPlayerHuman().getPoints());
        currentView.getComputerScore().setText("Computer Score:    " + currentModel.getComputer().getPoints());
        firstPositioningHandler(validPositionList);
    }

    //Retrieve the distributed tiles and create a list
    private void setDeckTiles() {
        int tilesDistributed = currentModel.getPlayerHuman().getDeck().getTilesInDeck().size();
        currentView.getActiveDeck().getChildren().clear();
        deckTiles.clear();
        if (tilesDistributed != 0) {
            for (int i = 0; i < tilesDistributed; i++) {
                //Fill the pane with the tileNodes
                deckTiles.add(new TileNode(currentModel.getPlayerHuman().getDeck().getTilesInDeck().get(i), 50, 50));
                currentView.getActiveDeck().getChildren().addAll(deckTiles.get(i));
                draggableTile((TileNode) currentView.getActiveDeck().getChildren().get(i));
            }
        }
    }

    private void paintGrid() {
        final int numCols = Grid.BOARD_SIZE;
        final int numRows = Grid.BOARD_SIZE;
        if (currentModel.getTurnNo() == 0) {
            currentView.getGrid().getChildren().clear();
            validPositionList.add(currentView.getEmptyTile());
            currentView.getGrid().add(validPositionList.get(0), (numCols) / 2, (numRows) / 2);
            validSpots(validPositionList.get(0), deckTiles);
        }
    }

    //Mouse events
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
                for (TileNode tile : deckTiles){
                    if (tile.equals(draggableTile)) {
                        views.add(currentView);
                        currentView.getActiveDeck().getChildren().remove(tile);
                        draggableTile.setStyle("");
                        e.setDropCompleted(true);
                        System.out.println(exchangedTiles);
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
//

    //////////////////////////////
    //Set source drag tiles
    private void draggableTile(TileNode tileNode) {
//        double tileStartX = tileNode.getTranslateX();
//        double tileStartY = tileNode.getTranslateY();
//        final double[] startPointX = new double[1];
//        final double[] startPointY = new double[1];
//        tileNode.setOnMousePressed(n -> {
//            startPointX[0] = n.getSceneX() - tileNode.getTranslateX();
//            startPointY[0] = n.getSceneY() - tileNode.getTranslateY();
//            n.consume();
//        });
//        tileNode.setOnMouseDragged(n -> {
//            tileNode.setVisible(false);
//            n.consume();
//        });

        tileNode.setOnDragDetected(e -> {
            Dragboard db = tileNode.startDragAndDrop(TransferMode.ANY);
            Image img = tileNode.getTile().getIconImage().getImage();
            db.setDragView(img);
            ClipboardContent cc = new ClipboardContent();
            cc.put(tileFormat, " ");
            db.setContent(cc);
            draggableTile = tileNode;
            tileNode.setVisible(true);
            e.consume();
        });
    }

    private void bagPopup() {

    }

    private void validSpots(TileNode validSpot, List<TileNode> decktiles) {
//        if (?) {
//        }

    }

    void popupWindow(Stage stage, String text, double width, double height) {
        PopupView view = new PopupView();
        PopupPresenter presenter = new PopupPresenter(stage, view, text, width, height);
    }

    private String whoPlaysFirst() {
        if (currentModel.getPlayerIndex() == 2) {
            return String.format("%s %s", this.currentModel.getPlayerHuman().getName(), "plays first");
        } else {
            return String.format("%s", "Computer plays first!");
        }
    }

    private void setExchangedTiles(TileNode tileNode) {
        exchangedTiles.add(tileNode);
    }

    private void welcomeMessage(Stage stage) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            popupWindow(stage, whoPlaysFirst(), 440, 220);
        }));
        timeline.play();
    }

    private void fillEmptySpots(TileNode placedTile) {
        int[] coordinates = getCoordinates(placedTile);
        assert coordinates != null;
        int column = coordinates[0];
        int row = coordinates[1];
        //Place left
        if (!containsTile(column - 1, row)) {
            currentView.getGrid().add(addToGrid(validPositionList), column - 1, row);
        }
        //Place right
        if (!containsTile(column + 1, row)) {
            currentView.getGrid().add(addToGrid(validPositionList), column + 1, row);
        }
        //Place up
        if (!containsTile(column, row - 1)) {
            currentView.getGrid().add(addToGrid(validPositionList), column, row - 1);
        }
        //Place Down
        if (!containsTile(column, row + 1)) {
            currentView.getGrid().add(addToGrid(validPositionList), column, row + 1);
        }
    }

    private TileNode addToGrid(LinkedList<TileNode> tileNodeLinkedList) {
        validPositionList = tileNodeLinkedList;
        validPositionList.add(new TileNode());
        return validPositionList.getLast();
    }

    //Set target for dragged tiles
    private void firstPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        currentView.getGrid().add(draggableTile, column, row);
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                        secondPositioningHandler(validPosition);
                    }
                    e.consume();
                });
            }
        }
    }

    private void secondPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        currentView.getGrid().add(draggableTile, column, row);
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                        thirdPositioningHandler(validPosition);
                    }
                    e.consume();
                });
            }
        }
    }

    private void thirdPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        currentView.getGrid().add(draggableTile, column, row);
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                        forthPositioningHandler(validPosition);
                    }
                    e.consume();
                });
            }
        }
    }

    private void forthPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        if (containsEmptyTile(column, row)) {
                            currentView.getGrid().add(draggableTile, column, row);
                        }
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                        draggableTile = null;
                        fifthPositioningHandler(validPosition);
                    }
                    e.consume();
                });
            }
        }
    }

    private void stopDragging(TileNode tileNode) {

    }

    private void fifthPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        currentView.getGrid().add(draggableTile, column, row);
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                        sixthPositioningHandler(validPosition);
                    }
                    e.consume();
                });
            }
        }
    }

    private void sixthPositioningHandler(LinkedList<TileNode> validPosition) {
        for (TileNode target : validPosition) {
            if (target.getParent() instanceof GridPane) {
                target.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat) && draggableTile != null) {
                        e.acceptTransferModes(TransferMode.ANY);
                    }
                    e.consume();
                });
                target.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasContent(tileFormat)) {
                        int j[] = getCoordinates(target);
                        int column = j[0];
                        int row = j[1];
                        draggableTile.setStyle("");
                        currentView.getGrid().add(draggableTile, column, row);
                        e.setDropCompleted(true);
                        fillEmptySpots(target);
                    }
                    e.consume();
                });
            }
        }
    }

    private Node getNodeByCoordinate(Integer column, Integer row) {
        for (Node node : currentView.getGrid().getChildren()) {
            if ((GridPane.getColumnIndex(node) == column) && (GridPane.getColumnIndex(node) == row)) {
                return node;
            }
        }
        return null;
    }

    private boolean containsTile(Integer col, Integer row) {
        for (Node node : currentView.getGrid().getChildren()) {
            if ((GridPane.getColumnIndex(node) == col) && (GridPane.getRowIndex(node) == row)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsEmptyTile(Integer col, Integer row) {
        for (Node node : currentView.getGrid().getChildren()) {
            if (((GridPane.getColumnIndex(node) == col) && (GridPane.getRowIndex(node) == row)) && (((TileNode) node).isEmpty())) {
                return true;
            }
        }
        return false;
    }

    private int[] getCoordinates(Node node) {
        int i[] = new int[2];
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
        //Show current time
//        this.thread = new Thread(() -> {
//            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception ex) {
//                    System.out.println(ex);
//                }
//                final String timeNow = time.format(new Date());
//                Platform.runLater(() -> {
//                    view.getTime().setText(timeNow);
//                });
//            }
//        });
//        thread.start();
//        }

        //Show timer
        this.thread = new Thread(() -> {
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
            int duration = 0;
            int seconds = 0;
            int minutes = 0;
            int hours = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    seconds = ++duration % 60 == 0 ? 0 : ++seconds;
                    if (seconds == 0) {
                        ++minutes;
                    }
                    if (minutes == 60) {
                        minutes = 0;
                        hours += 1;
                    }
                    String date = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Platform.runLater(() -> {
                        currentView.getTime().setText(date);
                    });
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });
        thread.start();
    }

}
