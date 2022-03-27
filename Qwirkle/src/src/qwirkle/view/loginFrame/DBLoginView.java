package src.qwirkle.view.loginFrame;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DBLoginView extends BorderPane {

    private Label msg;
    private TextField usernameField;
    private TextField passwordField;
    private Label username;
    private Label password;
    private VBox vbox;
    private HBox hbox1;
    private HBox hBox2;
    private HBox hBox3;
    private Button login;
    private Button cancel;
    private ImageView pgAdminLogo;
    private HBox hBox4;
    private Label errorMessage;

    public DBLoginView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        msg = new Label("""
                The username and/or password of your postgres
                credentials do not match with the database
                of the current application.
                Please insert your postgres username and password.""");
        usernameField = new TextField();
        usernameField.setText("your username");
        usernameField.setStyle("-fx-font-style: italic;");
        passwordField = new TextField();
        passwordField.setText("your password");
        passwordField.setStyle("-fx-font-style: italic;");
        username = new Label("Username:");
        password = new Label("Password:");
        login = new Button("Login");
        cancel = new Button("Cancel");
        pgAdminLogo = new ImageView(new Image("/images/postgresql.png"));
        vbox = new VBox(10);
        hbox1 = new HBox(20);
        hBox2 = new HBox(20);
        hBox3 = new HBox(50);
        hBox4 = new HBox(10);
        errorMessage = new Label();
    }

    private void layoutNodes() {
        setTop(pgAdminLogo);
        msg.setPrefWidth(460);
        msg.setStyle("-fx-font-size: 18; -fx-text-alignment: center; -fx-alignment: center;");
        msg.setPadding(new Insets(20, 10, 30, 10));
        pgAdminLogo.setFitWidth(300);;
        pgAdminLogo.setPreserveRatio(true);
        usernameField.setStyle("-fx-text-fill: rgba(65,68,80,0.5); -fx-font-style: italic; -fx-font-size: 16;");
        username.setStyle("-fx-font-size: 16;");
        passwordField.setStyle("-fx-text-fill: rgba(65,68,80,0.5); -fx-font-style: italic; -fx-font-size: 16;");
        password.setStyle("-fx-font-size: 16;");
        errorMessage.setStyle("-fx-font-style: italic; -fx-text-fill: #ff0000; -fx-font-size: 16; -fx-alignment: center;");
        hbox1.getChildren().addAll(username, usernameField);
        hbox1.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(password, passwordField);
        hBox2.setAlignment(Pos.CENTER);
        hBox3.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(msg, hbox1, hBox2, hBox3, errorMessage);
        vbox.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(10, 15, 10, 15));
        login.setPrefSize(117,47);
        login.setStyle(" -fx-text-fill: #30323a; -fx-border-radius: 4; -fx-border-width: 1;" +
                "-fx-border-color: #30323a; -fx-font-size: 18;");
        cancel.setPadding(new Insets(10, 15, 10, 15));
        cancel.setPrefSize(117,47);
        cancel.setStyle(" -fx-text-fill: #30323a; -fx-border-radius: 4; -fx-border-width: 1;" +
                "-fx-border-color: #30323a; -fx-font-size: 18;");
        hBox4.getChildren().addAll(cancel, login);
        hBox4.setAlignment(Pos.BOTTOM_CENTER);
        hBox4.setPadding(new Insets(10, 10, 25, 10));
        errorMessage.setPadding(new Insets(10,10,10,10));
        setTop(pgAdminLogo);
        setPadding(new Insets(20,10,10,10));
        setAlignment(pgAdminLogo, Pos.CENTER);
        setCenter(vbox);
        setBottom(hBox4);
        setAlignment(hBox4, Pos.BOTTOM_CENTER);
        setPrefSize(520, 740);
        setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 1),
                new CornerRadii(15.0), new Insets(0))));
    }

    TextField getUsernameField() {
        return usernameField;
    }

    TextField getPasswordField() {
        return passwordField;
    }

    Button getLogin() {
        return login;
    }

    Button getCancel() {
        return cancel;
    }
    Label getErrorMessage() {
        return errorMessage;
    }
}
