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

//PGAdmin popUp login window created when running the application for the first time. It stores the credentials of PGAdmin
//to a local file. It can only be triggered again if the file is erased or moved
public class DBLLoginPresenter {

    private DBLoginView view;
    private final Database model;
    private String username;
    private String password;
    private File file;
    private Timeline timeline;
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
            //Pass userName/password to the database class and attempt to login
            if (loginAttempt()) {
                //Save credentials locally if login attempt successful
                saveCredentials(primaryStage, fileChooser);
            }
        });
    }
    //Attempt to login to the database. It returns a boolean
    private boolean loginAttempt() {
        model.setUsername(username);
        model.setPassword(password);
        boolean connected;
        if (model.createDatabase()) {
            model.logIn();
            connected = true;
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
            connected = false;
        }
        return connected;
    }
    //Save locally the credentials using FileChooser
    private void saveCredentials(Stage primaryStage, FileChooser fileChooser) {
        fileChooser.setTitle("Save your credentials.");
        fileChooser.setInitialFileName(".user_data.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            saveTextToFile("Username: " + username + "\nPassword: " + password + "\nPlayer names: ", file);
        } else {
            //It exits the application if the user does not choose a path to save the file
            Platform.exit();
            System.exit(0);
        }
    }
    //Save the user's selected path where he stores the .user_data.txt file locally
    private void saveTextToFile(String content, File file) {
        try {
            //Save first the credentials to the chosen path by the user
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
            //Save the path of the .user_data.txt file to retrieve its content when needed at Database class and NewGamePresenter
            PrintWriter writer1;
            writer1 = new PrintWriter("resources/user-data/local_file_path.txt");
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
    //If it returns false the stage gets closed and the program gets terminated (used in an "if" statement in the Main class)
    public boolean isSaved() {
        return saved;
    }
}
