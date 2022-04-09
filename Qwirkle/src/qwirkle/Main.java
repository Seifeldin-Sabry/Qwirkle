package src.qwirkle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import qwirkle.view.introFrame.GameInitializationPresenter;
import qwirkle.view.introFrame.GameInitializationView;
import qwirkle.view.loginFrame.DBLLoginPresenter;
import qwirkle.view.loginFrame.DBLoginView;
import qwirkle.view.welcomeFrame.*;

public class Main extends Application {
    //    --module-path "/Users/sakis/Documents/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.media
    @Override
    public void start(Stage primaryStage) {
        GameInitializationView firstView = new GameInitializationView();
        GameInitializationPresenter gameInitializationPresenter = new GameInitializationPresenter(firstView);
        Stage intro = new Stage();
        intro.initStyle(StageStyle.TRANSPARENT);
        Scene introScene = new Scene(firstView);
        introScene.setFill(Color.TRANSPARENT);
        intro.setScene(introScene);
        intro.show();
        DBLoginView dbLoginView = new DBLoginView();
        DBLLoginPresenter dblLoginPresenter = new DBLLoginPresenter(dbLoginView);
        Stage loginStage = new Stage();
        Scene loginScene = new Scene(dbLoginView);
        loginStage.setScene(loginScene);
        WelcomeView welcomeView = new WelcomeView();
        primaryStage.setMaximized(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.setResizable(true);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        WelcomePresenter welcomePresenter = new WelcomePresenter(primaryStage, welcomeView);
        Scene main = new Scene(welcomeView);
        primaryStage.setScene(main);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),
                event -> {
                    try {
                        if ((gameInitializationPresenter.getError()) && (!dblLoginPresenter.getLoggedInSuccessful())) {
                            loginStage.show();
                            intro.close();
                        } else {
                            primaryStage.show();
                            welcomePresenter.addWindowEventHandlers(primaryStage);
                            loginStage.close();
                            intro.close();
                        }
                    } catch (NullPointerException ignored){
                    }
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
