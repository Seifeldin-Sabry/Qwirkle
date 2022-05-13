package qwirkle.view.rulesFrame;

import javafx.stage.Stage;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

public class RulesPresenterW {
    private final RulesView view;

    public RulesPresenterW(Stage stage, RulesView view){
        this.view = view;
        addEventHandler(stage);
    }
    //Back to WelcomeView
    private void addEventHandler(Stage stage){
        view.getBack().setOnAction(event -> {
            WelcomeView welcomeView = new WelcomeView();
            new WelcomePresenter(stage, welcomeView);
            view.getScene().setRoot(welcomeView);
        });
    }
}
