package src.qwirkle.view.welcomeFrame;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import qwirkle.view.newGameFrame.NewGamePresenter;
import qwirkle.view.newGameFrame.NewGameView;
import qwirkle.view.rulesFrame.RulesPresenterW;
import qwirkle.view.rulesFrame.RulesView;
import qwirkle.view.statisticsFrame.StatisticsPresenterW;
import qwirkle.view.statisticsFrame.StatisticsView;

public class WelcomePresenter {

    private final WelcomeView view;

    public WelcomePresenter(Stage stage, WelcomeView view) {
        this.view = view;
        addEventHandler(stage);
    }

    private void addEventHandler(Stage stage) {
        this.view.getStart().setOnAction(event -> setNewGameView(stage));
        this.view.getStatistics().setOnAction(event -> setStatisticsView(stage));
        this.view.getQuit().setOnAction(event -> quitApplication(event, stage));
        this.view.getRules().setOnAction(event -> setRulesView(stage));
    }

    private void setNewGameView(Stage stage) {
        NewGameView newGameView = new NewGameView();
        new NewGamePresenter(stage, newGameView);
        view.getScene().setRoot(newGameView);
    }

    private void setStatisticsView(Stage stage) {
        StatisticsView statisticsView = new StatisticsView();
        new StatisticsPresenterW(statisticsView, stage);
        view.getScene().setRoot(statisticsView);
//        Nathan statisticsView = new Nathan();
//        new NathanPresenter(statisticsView);
//        view.getScene().setRoot(statisticsView);
    }

    private void setRulesView(Stage stage) {
        RulesView rulesView = new RulesView();
        new RulesPresenterW(stage, rulesView);
        view.getScene().setRoot(rulesView);
    }

    public void addWindowEventHandlers(Stage stage) {
        view.getScene().getWindow().setOnCloseRequest(event -> closeApplication(event, stage));
        view.getQuit().setOnAction(e -> quitApplication(e, stage));
    }

    private void closeApplication(WindowEvent event, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("You are about to quit the game!");
        alert.setContentText("Do you really want to leave?");
        alert.getButtonTypes().clear();
        ButtonType no = new ButtonType("NO");
        ButtonType yes = new ButtonType("YES");
        alert.getButtonTypes().addAll(no, yes);
        alert.initOwner(stage);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
        if (alert.getResult() == null || alert.getResult().equals(no)) {
            event.consume();
        } else {
            Platform.exit();
            System.exit(0);
        }
    }

    private void quitApplication(ActionEvent event, Stage stage) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("You are about to quit the game!");
        alert.setContentText("Do you really want to leave?");
        alert.getButtonTypes().clear();
        ButtonType no = new ButtonType("NO");
        ButtonType yes = new ButtonType("YES");
        alert.getButtonTypes().addAll(no, yes);
        alert.initOwner(stage);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.showAndWait();
        if (alert.getResult().equals(yes)) {
            event.consume();
            Platform.exit();
            System.exit(0);
        }
    }
}
