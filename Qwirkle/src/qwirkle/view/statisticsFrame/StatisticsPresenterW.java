package src.qwirkle.view.statisticsFrame;

import javafx.stage.Stage;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

public class StatisticsPresenterW {
    private StatisticsView view;

    public StatisticsPresenterW(StatisticsView view, Stage stage) {
        this.view = view;
        addEventHandler(stage);
    }

    private void addEventHandler(Stage stage) {
        view.getBack().setOnAction(event -> setBack(stage));
    }

    private void setBack(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.view.getScene().setRoot(welcomeView);
    }
}
