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
    private void addEventHandler(Stage stage){
        view.getBack().setOnAction(event -> setBack(stage));
    }

    private void setBack(Stage stage){
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
//        view.getScene().getStylesheets().add("/style/style.css");
        view.getScene().setRoot(welcomeView);
    }
}