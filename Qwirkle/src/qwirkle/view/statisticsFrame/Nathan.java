package src.qwirkle.view.statisticsFrame;

import javafx.geometry.Insets;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Nathan extends BorderPane {
    private ImageView logo;
    private Button back;
    private Button rules;
    private Button quit;
    private Button tab1;
    private Button tab2;
    private Button tab3;
    private HBox tabs;
    private Button tab4;
    private HBox hBox;
    private VBox bottomBox;
    private HBox PlayerGraphTitle;
    private HBox ComputerGraphTitle;
    private HBox GraphTitle;
    private Button Player;
    private Button Computer;
    private Button Settings;
    private VBox graphAndTabs;
    final NumberAxis PlayerLine = new NumberAxis();
    final NumberAxis ComputerLine = new NumberAxis();
    AreaChart<Number, Number> stats = new AreaChart<>(PlayerLine, ComputerLine);

    public Nathan() {
        initialiseNodes();
        layoutNodes();
    }


    private void initialiseNodes() {
        logo = new ImageView(new Image("images/logoMAX.png"));
        back = new Button("Back");
        stats = new AreaChart<>(PlayerLine, ComputerLine);
        rules = new Button("Rules");
        Settings = new Button("Settings");
        quit = new Button("Quit");
        hBox = new HBox(100);
        tab1 = new Button("Game");
        tab2 = new Button("Per turn");
        tab3 = new Button("Player");
        tab4 = new Button("Computer");
        tabs = new HBox(tab1, tab2, tab3, tab4);
        PlayerGraphTitle = new HBox(50);
        ComputerGraphTitle = new HBox(50);
        Player = new Button("Player");
        Computer = new Button("Computer");
        bottomBox = new VBox(200);
        graphAndTabs = new VBox(900);
        GraphTitle = new HBox(200);
    }

    private void layoutNodes() {
        logo.setPreserveRatio(true);
        logo.setFitWidth(275);
        logo.setSmooth(true);
        logo.setCache(true);
        setMargin(logo, new Insets(20, 0, 0, 0));
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        setTop(logo);
        PlayerGraphTitle.setStyle("-fx-background-color: blue");
        ComputerGraphTitle.setStyle("-fx-background-color: Red");
        PlayerGraphTitle.getChildren().addAll(Player);
        ComputerGraphTitle.getChildren().addAll(Computer);
        stats.setStyle("-fx-font-size: 50; -fx-font-weight: bold;");
        stats.setPrefSize(5, 5);
        stats.contains(50.0, 50.0);
        graphAndTabs.getChildren().addAll(tabs, GraphTitle, stats);
        setCenter(graphAndTabs);
        GraphTitle.getChildren().addAll(PlayerGraphTitle, ComputerGraphTitle);
        tab1.setStyle("-fx-background-color: #FF5733; -fx-font-size: 22; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        tab2.setStyle("-fx-background-color: #FF5733; -fx-font-size: 22; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        tab3.setStyle("-fx-background-color: #FF5733; -fx-font-size: 22; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        tab4.setStyle("-fx-background-color: #FF5733; -fx-font-size: 22; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        back.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        rules.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        Settings.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        quit.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        back.setPrefSize(125, 60);
        hBox.getChildren().addAll(getRules(), getSettings(), getQuit());
        hBox.setSpacing(0);
        tabs.setSpacing(0);
        GraphTitle.setSpacing(2);
        bottomBox.getChildren().addAll(back, hBox);
        setBottom(bottomBox);
        bottomBox.setSpacing(5.0);
        stats.setPrefSize(5, 5);
    }


    Button getBack() {
        return back;
    }


    Button getRules() {
        return rules;
    }


    Button getQuit() {
        return quit;
    }


    Button getSettings() {
        return Settings;
    }


    Button getTab1() {
        return tab1;
    }


    Button getTab2() {
        return tab2;
    }


    Button getTab3() {
        return tab3;
    }


    Button getTab4() {
        return tab4;
    }


    HBox getTabs() {
        return tabs;
    }
}
