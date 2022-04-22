package qwirkle.view.loginFrame;


import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DBLoginView extends BorderPane {

    private Group inputGroup;

    private Label msg;
    private TextField usernameField;
    private PasswordField passwordField;
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
    private Group buttonGroup;

    public DBLoginView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        msg = new Label("""
                The username and/or password of your postgres
                credentials do not match with the database.
                Please insert your postgres username and password.""");
        msg.getStyleClass().add("message");
        usernameField = new TextField();
        usernameField.setPromptText("your username");
        usernameField.getStyleClass().add("input-field");
        passwordField = new PasswordField();
        passwordField.setPromptText("your password");

        username = new Label("Username:");
        username.getStyleClass().add("label");
        password = new Label("Password:");
        password.getStyleClass().add("label");
        login = new Button("Login");
        cancel = new Button("Cancel");
        pgAdminLogo = new ImageView(new Image("/images/postgresql.png"));
        vbox = new VBox(10);
        hbox1 = new HBox(20);
        hBox2 = new HBox(20);
        hBox3 = new HBox(50);
        hBox4 = new HBox(10);
        errorMessage = new Label();
        errorMessage.getStyleClass().add("error-message");

        inputGroup = new Group(usernameField,passwordField);
        inputGroup.getChildren().forEach(node -> node.getStyleClass().add("input-field"));

        buttonGroup = new Group(login,cancel);
        buttonGroup.getChildren().forEach(node -> node.getStyleClass().add("db-button"));
    }

    private void layoutNodes() {
        setTop(pgAdminLogo);
        msg.setPrefWidth(460);
        msg.setPadding(new Insets(20, 10, 30, 10));
        pgAdminLogo.setFitWidth(300);;
        pgAdminLogo.setPreserveRatio(true);
        hbox1.getChildren().addAll(username, usernameField);
        hbox1.setAlignment(Pos.CENTER);
        hBox2.getChildren().addAll(password, passwordField);
        hBox2.setAlignment(Pos.CENTER);
        hBox3.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(msg, hbox1, hBox2, hBox3, errorMessage);
        vbox.setAlignment(Pos.CENTER);
        login.setPadding(new Insets(10, 15, 10, 15));
        login.setPrefSize(117,47);

        cancel.setPadding(new Insets(10, 15, 10, 15));
        cancel.setPrefSize(117,47);

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
        login.setDefaultButton(true);
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
