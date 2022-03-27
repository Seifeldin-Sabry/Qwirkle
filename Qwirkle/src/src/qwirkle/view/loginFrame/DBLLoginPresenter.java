package src.qwirkle.view.loginFrame;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.util.Duration;
import qwirkle.data.Database;

public class DBLLoginPresenter {

    private DBLoginView view;
    private final Database model;
    private String username;
    private String password;
    private boolean loggedInSuccessful = false;

    public DBLLoginPresenter(DBLoginView view) {
        this.view = view;
        model = new Database();
        addEventHandler();
    }

    private void addEventHandler() {
        view.getCancel().setOnAction(event -> closeWindow());
        view.getLogin().setOnAction((ActionEvent event) -> {
            username = view.getUsernameField().getText();
            password = view.getPasswordField().getText();
            loginAttempt();
        });
    }

    private void loginAttempt() {
        model.setUsername(username);
        model.setPassword(password);
        model.createDatabase();
        if (!model.getError()) {
            view.getErrorMessage().setStyle("-fx-text-fill: #30323a; -fx-font-size: 18;");
            view.getErrorMessage().setPadding(new Insets(4,10,10,10));
            view.getErrorMessage().setText("Please wait...");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1),
                    event -> {
                        try {
                            view.getErrorMessage().setStyle("-fx-text-fill: #48db1c; -fx-font-size: 18;");
                            view.getErrorMessage().setText("Connected!");
                            loggedInSuccessful = true;
                        } catch (NullPointerException ignored){
                        }
                    }));
            timeline.play();
        } else {
            loggedInSuccessful = false;
            view.getErrorMessage().setText("Your username or password is not correct!");
        }
    }

    private void closeWindow() {
        Platform.exit();
    }
    public boolean getLoggedInSuccessful(){
        return loggedInSuccessful;
    }
}
