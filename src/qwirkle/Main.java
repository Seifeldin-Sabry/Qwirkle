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

    private Timeline timeline;

    @Override
    public void start(Stage primaryStage) {
        //Intro stage is starting
        IntroView firstView = new IntroView();
        Stage intro = new Stage();
        intro.initStyle(StageStyle.TRANSPARENT);
        Scene introScene = new Scene(firstView);
        introScene.setFill(Color.TRANSPARENT);
        intro.setScene(introScene);
        intro.show();
        //Login stage instantiated and called only once at the first run of the application within the timeline (3 secs delayed)
        Stage loginStage = new Stage();
        DBLoginView dbLoginView = new DBLoginView();
        DBLLoginPresenter loginPresenter = new DBLLoginPresenter(dbLoginView, loginStage);
        Scene loginScene = new Scene(dbLoginView);
        loginScene.getStylesheets().add("style/style.css");
        loginStage.setScene(loginScene);
        //Main stage instantiated. It starts within the timeLine when the "else if" statement is true
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
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(3),
                event -> {
                    //Returns false when there are no pgAdmin credentials saved. It returns false only the first time running the application
                    if (!Database.getInstance().createDatabase()) {
                        loginStage.show();
                        intro.close();
                        //Returns true after the pgAdmin credentials get stored locally
                    } else if (loginPresenter.isSaved()) {
                        timeline.setDelay(Duration.seconds(0.5));
                        Database.getInstance().logIn();
                        primaryStage.show();
                        welcomePresenter.addWindowEventHandlers(primaryStage); //It contains a WindowEvent that gets passed to all the rest of the scenes
                        loginStage.close();
                        intro.close();
                        timeline.stop();
                    }
                });
        timeline = new Timeline(keyFrame);
        //Works as a while loop that checks every 3 secs if the pgAdmin credentials are correct. If they are correct it stops from within the timeLine above.
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
