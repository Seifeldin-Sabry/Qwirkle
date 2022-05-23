package qwirkle.view.loginFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.util.Duration;
import qwirkle.data.Database;
import java.io.File;

//PGAdmin popUp login window created when running the application for the first time. It stores the credentials of PGAdmin
//to a local file. It can only be triggered again if the file is erased or moved
public class DBLLoginPresenter {

    private DBLoginView view;
    private final Database model;
    private String username;
    private String password;
    private Timeline timeline;

    public DBLLoginPresenter(DBLoginView view, Stage primaryStage) {
        this.view = view;
        model = Database.getInstance();
        addEventHandler(primaryStage);
    }

    private void addEventHandler(Stage primaryStage) {
        view.getCancel().setOnAction(event -> closeWindow());
        view.getLogin().setOnAction((ActionEvent event) -> {
            username = view.getUsernameField().getText();
            password = view.getPasswordField().getText();
            loginAttempt();
        });
    }
    //Attempt to login to the database. It returns a boolean
    private void loginAttempt() {
        model.setUsername(username);
        model.setPassword(password);
        if (model.createDatabase()) {
            model.logIn();
            view.getErrorMessage().setStyle("-fx-text-fill: #30323a; -fx-font-size: 18;");
            view.getErrorMessage().setPadding(new Insets(4, 10, 10, 10));
            view.getErrorMessage().setText("Please wait...");
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.5),
                    event -> {
                        view.getErrorMessage().setStyle("-fx-text-fill: #48db1c; -fx-font-size: 18;");
                        view.getErrorMessage().setText("Connected!");
                        timeline.stop();
                    }));
            timeline.play();
        } else {
            view.getErrorMessage().setText("Your username or password is not correct!");
        }
    }

    private void closeWindow() {
        Platform.exit();
    }
}
