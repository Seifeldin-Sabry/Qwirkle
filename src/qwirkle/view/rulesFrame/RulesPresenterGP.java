package qwirkle.view.rulesFrame;

import qwirkle.view.gamePlayFrame.GamePlayView;

public class RulesPresenterGP {
    private final GamePlayView gamePlayView;
    private final RulesView view;

    public RulesPresenterGP(RulesView view, GamePlayView gamePlayView){
        this.view = view;
        this.gamePlayView = gamePlayView;
        addEventHandler();
    }

    //Back to GamePlayView
    private void addEventHandler(){
        view.getBack().setOnAction(event -> this.view.getScene().setRoot(gamePlayView));
    }

}
