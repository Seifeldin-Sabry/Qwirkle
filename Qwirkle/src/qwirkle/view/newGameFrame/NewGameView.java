package src.qwirkle.view.newGameFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class NewGameView extends BorderPane {

    //private Node attributes
    private ImageView logo;
    private VBox vbox1;
    private VBox vbox2;
    private VBox vbox3;
    private HBox hboxCenter;
    private HBox menuBox;
    private Button play;
    private Button rules;
    private Button quit;
    private Label whoPlaysFirst;
    private Label gameMode;
    private RadioButton radioHPF1;
    private RadioButton radioHPF2;
    private RadioButton radioMode1;
    private RadioButton radioMode2;
    private CheckBox changeName;
    private TextArea placeholder;
    private Button submit;
    private VBox vbox2_1;
    private TextArea textArea;
    private ToggleGroup group1;
    private ToggleGroup group2;
    private Image background;

    public NewGameView() {
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes() throws NullPointerException {
        logo = new ImageView(new Image("/images/logoMAX.png"));
        vbox1 = new VBox(15);
        vbox2 = new VBox(15);
        vbox2_1 = new VBox(10);
        vbox3 = new VBox(100);
        hboxCenter = new HBox(30);
        menuBox = new HBox(0.5);
        play = new Button("Play");
        rules = new Button("Game Rules");
        quit = new Button("Back");
        whoPlaysFirst = new Label("Who plays first?");
        gameMode = new Label("Game Mode");
        textArea = new TextArea("");
        radioHPF1 = new RadioButton("Player 1");
        radioHPF2 = new RadioButton("Computer");
        radioMode1 = new RadioButton("Easy");
        radioMode2 = new RadioButton("AI active");
        changeName = new CheckBox("Change your name?");
        placeholder = new TextArea();
        submit = new Button("Submit");
        group1 = new ToggleGroup();
        group2 = new ToggleGroup();
        background = new Image("/images/tiles3D_min.png");

    }

    public void layoutNodes() {
        logo.setPreserveRatio(true);
        logo.setFitWidth(300);
        logo.setSmooth(true);
        logo.setCache(true);
        logo.setPreserveRatio(true);
        logo.setFitWidth(900);
        logo.setSmooth(true);
        logo.setCache(true);
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        play.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-background-radius: 5;");
        play.setPrefSize(220, 50);
        rules.setStyle("-fx-background-color: #FF5733; -fx-font-size: 20; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-text-alignment: center;");
        rules.setPrefSize(220, 25);
        quit.setStyle("-fx-background-color: #FF5733; -fx-font-size: 20; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10); -fx-cursor: hand; -fx-text-alignment: center;");
        quit.setPrefSize(220, 25);
        radioHPF1.setToggleGroup(group1);
        radioHPF1.setPrefWidth(175);
        radioHPF1.setStyle("-fx-text-fill: #fff;");
        radioHPF2.setToggleGroup(group1);
        changeName.setStyle("-fx-text-fill: #fff; -fx-font-size: 16;");
        changeName.setPadding(new Insets(5, 0, 0, 0));
        group1.selectToggle(radioHPF1);
        radioHPF2.setPrefWidth(175);
        radioHPF2.setStyle("-fx-text-fill: #fff;");
        radioMode1.setToggleGroup(group2);
        radioMode1.setPrefWidth(160);
        radioMode1.setStyle("-fx-text-fill: #fff;");
        radioMode2.setToggleGroup(group2);
        group2.selectToggle(radioMode1);
        radioMode2.setPrefWidth(160);
        radioMode2.setStyle("-fx-text-fill: #fff;");
        whoPlaysFirst.setStyle("-fx-background-color: rgba(255,255,255,0); -fx-text-fill: #fff; -fx-padding: 20;" +
                " -fx-font-size: 25;");
        gameMode.setStyle("-fx-background-color: rgba(255,255,255,0); -fx-text-fill: #fff; -fx-padding: 20;" +
                " -fx-font-size: 25;");
        vbox1.getChildren().addAll(whoPlaysFirst, radioHPF1, radioHPF2, changeName);
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setStyle("-fx-background-color: rgb(24,24,24); -fx-text-fill: #fff; -fx-background-radius: 25; " +
                "-fx-font-family: 'Comic Sans MS';");
        vbox1.setMaxWidth(300);
        vbox1.setMinWidth(300);
        vbox1.setMaxHeight(250);
        vbox1.setMinHeight(250);
        vbox2.getChildren().addAll(gameMode, radioMode1, radioMode2);
        vbox2.setAlignment(Pos.TOP_CENTER);
        vbox2.setStyle("-fx-background-color: rgb(24,24,24); -fx-text-fill: #fff; -fx-background-radius: 25;" +
                " -fx-font-family: 'Comic Sans MS';");
        vbox2.setMaxWidth(300);
        vbox2.setMinWidth(300);
        vbox2.setMaxHeight(250);
        vbox2.setMinHeight(250);
        vbox2_1.setMaxWidth(300);
        vbox2_1.setMinWidth(300);
        vbox2_1.setMaxHeight(250);
        vbox2_1.setMinHeight(250);
        vbox2_1.setStyle("-fx-background-color: rgb(24,24,24); -fx-text-fill: #fff; -fx-background-radius: 25;" +
                " -fx-font-family: 'Comic Sans MS';");
        vbox2_1.setAlignment(Pos.TOP_CENTER);
        vbox2_1.getChildren().addAll(placeholder);
        placeholder.setStyle("-fx-background-color: rgb(24,24,24); -fx-text-fill: #000000;-fx-font-family: 'Comic Sans MS';" +
                "-fx-font-size: 18;");
        placeholder.setPadding(new Insets(10, 10,10,10 ));
        placeholder.setMaxWidth(195);
        submit.setStyle("-fx-cursor: hand; -fx-background-color: #FF5733; -fx-border-radius: 5; -fx-padding: 5;" +
                "-fx-text-fill: #fff;");
        hboxCenter.getChildren().addAll(vbox1, vbox2);
        hboxCenter.setStyle("-fx-font-size: 20;");
        hboxCenter.setAlignment(Pos.CENTER);
        play.setAlignment(Pos.CENTER);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getChildren().addAll(getRules(), getQuit());
        menuBox.setPadding(new Insets(0, 0, 20, 0));
        vbox3.getChildren().addAll(play, menuBox);
        vbox3.setAlignment(Pos.CENTER);
        setMargin(logo, new Insets(80, 0, 0, 0));
        setCenter(hboxCenter);
        setTop(logo);
        setBottom(vbox3);
        setAlignment(hboxCenter, Pos.CENTER);
        setAlignment(logo, Pos.TOP_CENTER);
        setAlignment(vbox3, Pos.BOTTOM_CENTER);
        BackgroundImage bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, false, Side.BOTTOM, 0,
                        false), new BackgroundSize(100, 100, true,
                true, false, true));
        setBackground(new Background(bgImage));
    }

    //Getters
    Button getPlay() {
        return play;
    }

    Button getRules() {
        return rules;
    }

    Button getQuit() {
        return quit;
    }

    TextArea getTextArea() {
        return textArea;
    }

    RadioButton getRadioHPF1() {
        return radioHPF1;
    }

    RadioButton getRadioHPF2() {
        return radioHPF2;
    }

    ToggleGroup getGroup1() {
        return group1;
    }

    ToggleGroup getGroup2() {
        return group2;
    }

    CheckBox getChangeName() {
        return changeName;
    }

    VBox getVbox1() {
        return vbox1;
    }

    Label getWhoPlaysFirst() {
        return whoPlaysFirst;
    }

    RadioButton getRadioMode1() {
        return radioMode1;
    }

    RadioButton getRadioMode2() {
        return radioMode2;
    }

    TextArea getPlaceholder() {
        return placeholder;
    }

    HBox getHboxCenter() {
        return hboxCenter;
    }

    VBox getVbox2() {
        return vbox2;
    }

    VBox getVbox2_1() {
        return vbox2_1;
    }

    VBox getVbox3() {
        return vbox3;
    }

    Button getSubmit() {
        return submit;
    }


}
