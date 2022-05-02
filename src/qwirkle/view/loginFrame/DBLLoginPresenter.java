package qwirkle.view.loginFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import qwirkle.Main;
import qwirkle.data.Database;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBLLoginPresenter {

    private DBLoginView view;
    private final Database model;
    private String username;
    private String password;
    private File file;
    private boolean saved;

    public DBLLoginPresenter(DBLoginView view, Stage primaryStage) {
        this.view = view;
        model = Database.getInstance();
        saved = Database.getInstance().logIn();
        addEventHandler(primaryStage);
    }

    private void addEventHandler(Stage primaryStage) {
        view.getCancel().setOnAction(event -> closeWindow());
        view.getLogin().setOnAction((ActionEvent event) -> {
            username = view.getUsernameField().getText();
            password = view.getPasswordField().getText();
            FileChooser fileChooser = new FileChooser();
            if (loginAttempt()) {
                saveCredentials(primaryStage, fileChooser);
            }
        });
    }

    private boolean loginAttempt() {
        model.setUsername(username);
        model.setPassword(password);
        boolean connected;
        if (model.createDatabase()) {
            model.logIn();
            connected = true;
            view.getErrorMessage().setStyle("-fx-text-fill: #30323a; -fx-font-size: 18;");
            view.getErrorMessage().setPadding(new Insets(4,10,10,10));
            view.getErrorMessage().setText("Please wait...");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5),
                    event -> {
                        try {
                            view.getErrorMessage().setStyle("-fx-text-fill: #48db1c; -fx-font-size: 18;");
                            view.getErrorMessage().setText("Connected!");
                        } catch (NullPointerException ignored){
                        }
                    }));
            timeline.play();
        } else {
            view.getErrorMessage().setText("Your username or password is not correct!");
            connected = false;
        }
        return connected;
    }

    private void saveCredentials(Stage primaryStage, FileChooser fileChooser){
        fileChooser.setTitle("Save your credentials");
        fileChooser.setInitialFileName(".my_login_credentials");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            saveTextToFile("Username: " + username + "\nPassword: " + password + "\nPlayer names: ", file);
        }
    }

    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
            PrintWriter writer1;
            writer1 = new PrintWriter("resources/user-data/info.txt");
            writer1.println(file.getPath());
            writer1.close();
            saved = true;
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            saved = false;
        }
    }

    private void closeWindow() {
        Platform.exit();
    }

    public boolean isSaved() {
        return saved;
    }
}
