package src.qwirkle.view.rulesFrame;

import javafx.stage.Stage;
import qwirkle.view.newGameFrame.NewGamePresenter;
import qwirkle.view.newGameFrame.NewGameView;

public class RulesPresenterNG {

    private final RulesView view;
    private boolean isPlayerStarting;
    private String name;

    public RulesPresenterNG(Stage stage, RulesView view, boolean isPlayerStarting, String name){
        this.view = view;
        this.isPlayerStarting = isPlayerStarting;
        this.name = name;
        addEventHandler(stage);
    }
    private void addEventHandler(Stage stage){
        view.getBack().setOnAction(event -> setBack(stage));
    }

    private void setBack(Stage stage){
        NewGameView newGameView = new NewGameView();
        NewGamePresenter newGamePresenter = new NewGamePresenter(stage, newGameView);
        newGamePresenter.setIsPlayerStarting(isPlayerStarting);
        newGamePresenter.setName(name);
        view.getScene().setRoot(newGameView);
    }
}
