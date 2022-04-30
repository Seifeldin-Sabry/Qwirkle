package qwirkle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import qwirkle.data.Database;
import qwirkle.view.introFrame.IntroView;
import qwirkle.view.loginFrame.DBLLoginPresenter;
import qwirkle.view.loginFrame.DBLoginView;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;


public class Main extends Application {
    //    --module-path "/Users/sakis/Documents/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.media

    //--module-path "/home/seif/Documents/lib/javafx-sdk-17.0.2/lib" --add-modules javafx.controls,javafx.media
    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        IntroView firstView = new IntroView();
        Stage intro = new Stage();
        intro.initStyle(StageStyle.TRANSPARENT);
        Scene introScene = new Scene(firstView);
        introScene.setFill(Color.TRANSPARENT);
        intro.setScene(introScene);
        intro.show();
        DBLoginView dbLoginView = new DBLoginView();
        new DBLLoginPresenter(dbLoginView);
        Stage loginStage = new Stage();
        Scene loginScene = new Scene(dbLoginView);
        loginScene.getStylesheets().add("style/style.css");
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
        main.getStylesheets().add("style/style.css");
        primaryStage.setScene(main);
        timeline = new Timeline(new KeyFrame(Duration.seconds(3),
                                             event -> {
                                                 try {
                                                     if (!Database.getInstance().createDatabase()) {
                                                         loginStage.show();
                                                         intro.close();
                                                     } else {
                                                         Database.getInstance().logIn();
                                                         primaryStage.show();
                                                         welcomePresenter.addWindowEventHandlers(primaryStage);
                                                         loginStage.close();
                                                         intro.close();
                                                         timeline.stop();
                                                     }
                                                 } catch (NullPointerException ignored) {
                                                 }
                                             }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
