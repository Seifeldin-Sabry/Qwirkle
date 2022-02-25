package qwirkle.view;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import qwirkle.model.*;

import java.util.List;


public class QwirkleGameScreenPresenter {
    private Grid grid;
    private Bag bag;
    private Player playerHuman;
    private Computer computer;
    private final QwirkleGameScreenView view;

    public QwirkleGameScreenPresenter(QwirkleGameScreenView view) {
        this.view = view;
        bag = new Bag();
        grid = new Grid();

        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        // get controls from view (by accessing the package-private getters)
        // and add event handlers, listeners, ...
        view.getMenuButtons().get("quit").setOnAction(event -> Platform.exit());

    }

    private void onDrag(MouseEvent event, Tile tile) {
     tile.setX(event.getX() + tile.getX());
     tile.setY(event.getY() + tile.getY());
     tile.draw();
    }

    private void onDragRelease(MouseEvent event, Tile tile) {
        if (grid.contains(tile.getX(),tile.getY())) System.out.println("Intersects");
        grid.updateGrid();
    }


    private void updateView() {
        /* fills view*/
        view.getCenterContainer().getChildren().add(0,grid);
        initialiseHandDisplay();
    }

    private void initialiseHandDisplay(){
        List<Tile> tiles = bag.initialHandSetup();
        GridPane hand = new GridPane();
        for (int row = 0; row < Bag.SIZE_HAND; row++) {
            Tile tile = tiles.get(row);
            Rectangle rectangle = tile.getRectangle();
//            ImageView rectangle = tile.getRectangle();
            hand.add(rectangle,row,0);
            rectangle.setOnMouseDragged(event -> onDrag(event,tile));
            rectangle.setOnMouseReleased(event -> onDragRelease(event,tile));
        }
        hand.setHgap(20);
        view.getBottomCenterContainer().getChildren().add(hand);
    }

    private void findNearestGridSquare(Tile tile){

    }

}




