package qwirkle.view.rulesFrame;

import javafx.stage.Stage;
import qwirkle.view.newGameFrame.NewGamePresenter;
import qwirkle.view.newGameFrame.NewGameView;

import java.io.FileNotFoundException;


public class RulesPresenterNG {

    private final RulesView view;
    private boolean isPlayerStarting;
    private String name;

    public RulesPresenterNG(Stage stage, RulesView view, NewGameView newGameView, boolean isPlayerStarting, String name) {
        this.view = view;
        this.isPlayerStarting = isPlayerStarting;
        this.name = name;
        addEventHandler(stage, newGameView);
    }

    private void addEventHandler(Stage stage, NewGameView newGameView) {
        view.getBack().setOnAction(event -> setBack(stage, newGameView));
    }

    private void setBack(Stage stage, NewGameView newGameView) {
        NewGamePresenter newGamePresenter = new NewGamePresenter(stage, newGameView);
        newGamePresenter.setIsPlayerStarting(isPlayerStarting);
        newGamePresenter.setName(name);
        view.getScene().setRoot(newGameView);
    }
}
