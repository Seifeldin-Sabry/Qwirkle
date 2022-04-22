package qwirkle.view.statisticsFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import qwirkle.data.Database;


public class StatisticsView extends BorderPane {
    private ImageView logo;
    private Button back;
    private Button lastGameButton;
    private Button allGamesButton;
    private HBox upperButtons;
    private VBox vBox;
    private String playerName;


    //Game-Session Statistics
    private AreaChart durationLastGameSession;
    private XYChart.Series seriesDurationLastGameSessionPlayer;
    private XYChart.Series seriesDurationLastGameSessionComputer;
    private NumberAxis yAxisTime;
    private NumberAxis xAxisTurnNo;

    private AreaChart pointsPerTurnLastGameSession;
    private XYChart.Series seriesPointsPerTurnLastGameSessionPlayer;
    private XYChart.Series seriesPointsPerTurnLastGameSessionComputer;
    private NumberAxis yAxisPoints;
    private NumberAxis xAxisTurnNo2;

    //Game Statistics
//    private PieChart mostPlayedTilesByColor;
//    private PieChart mostPlayedTilesByShape;


    private TabPane tabPane1;
    private TabPane tabPane2;
    private Tab durationPerTurnLastGameSessionTab;
    private Tab pointPerTurnLastGameSessionTab;
    private Tab tileByColorTab;
    private Tab tileByShapeTab;

    private Tab durationPerSessionTab;

    private Tab averageScorePerSessionTab;

    private Tab bestScorePerSessionTab;

    //duration perSession
    private AreaChart durationPerSession;
    private NumberAxis yAxisDuration;
    private NumberAxis xAxisGameNo;
    private XYChart.Series seriesDurationPerGameSession;

    //player and computer score persession
    private AreaChart bestScorePerSession;
    private NumberAxis yAxisScore;
    private NumberAxis xAxisGameNo2;
    private XYChart.Series seriesBestScorePlayer;
    private XYChart.Series seriesBestScoreComputer;

    //average score perturn per session
    private AreaChart averageScorePerSession;
    private NumberAxis yAxisAverageScore;
    private NumberAxis xAxisGameNo3;
    private XYChart.Series seriesAverageScorePlayer;
    private XYChart.Series seriesAverageScoreComputer;


    public StatisticsView() {
        initialiseNodes();
        layoutNodes();
        fixTabsAlignment();
    }

    private void initialiseNodes() {
        logo = new ImageView(new Image("images/logoMAX.png"));
        logo.getStyleClass().add("logo");
        back = new Button("Back");
        back.getStyleClass().add("stat-backBtn");
        playerName = Database.getInstance().getLastPlayerName();

//        //PIECHARTS
//        mostPlayedTilesByColor = new PieChart();
//        mostPlayedTilesByColor.setTitle("Your Most Played Tiles By Color");
//        mostPlayedTilesByColor.setLegendSide(Side.LEFT);
//
//        mostPlayedTilesByShape = new PieChart();
//        mostPlayedTilesByShape.setTitle("Your Most Played Tiles By Shape");
//        mostPlayedTilesByShape.setLegendSide(Side.LEFT);


        //AREACHARTS GAMESESSION
        seriesDurationLastGameSessionPlayer = new XYChart.Series<>();
        seriesDurationLastGameSessionPlayer.setName(playerName);

//        seriesDurationLastGameSessionComputer = new XYChart.Series<>();
//        seriesDurationLastGameSessionComputer.setName("Computer");

        yAxisTime = new NumberAxis();
        yAxisTime.setLabel("Time (seconds)");
        xAxisTurnNo = new NumberAxis();
        xAxisTurnNo.setLabel("Turn No.");
        durationLastGameSession = new AreaChart(xAxisTurnNo, yAxisTime);
        durationLastGameSession.setTitle("Duration per Turn last game session");
        durationLastGameSession.setLegendVisible(true);
        durationLastGameSession.setCreateSymbols(true);

        seriesPointsPerTurnLastGameSessionPlayer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionPlayer.setName(playerName);
        seriesPointsPerTurnLastGameSessionComputer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionComputer.setName("Computer");
        yAxisPoints = new NumberAxis();
        yAxisPoints.setLabel("Points");
        xAxisTurnNo2 = new NumberAxis();
        xAxisTurnNo2.setLabel("Turn No.");
        pointsPerTurnLastGameSession = new AreaChart(xAxisTurnNo2, yAxisPoints);
        pointsPerTurnLastGameSession.setTitle("Points Per Turn of last game session");


        //AREACHARTS OVERALL Duration
        seriesDurationPerGameSession = new XYChart.Series<>();
        seriesDurationPerGameSession.setName("Duration");

        yAxisDuration = new NumberAxis();
        yAxisDuration.setLabel("Time (seconds)");
        xAxisGameNo = new NumberAxis();
        xAxisGameNo.setLabel("Game No.");
        durationPerSession = new AreaChart(xAxisGameNo, yAxisDuration);
        durationPerSession.setTitle("Duration per session");
        durationPerSession.setLegendVisible(true);
        durationPerSession.setCreateSymbols(true);


        //AREACHARTS OVERALL Average Score
        seriesAverageScorePlayer = new XYChart.Series<>();
        seriesAverageScorePlayer.setName("Player");
        seriesAverageScoreComputer = new XYChart.Series<>();
        seriesAverageScoreComputer.setName("Computer");

        yAxisAverageScore = new NumberAxis();
        yAxisAverageScore.setLabel("Score");
        xAxisGameNo3 = new NumberAxis();
        xAxisGameNo3.setLabel("Game No.");
        averageScorePerSession = new AreaChart(xAxisGameNo3, yAxisAverageScore);
        averageScorePerSession.setTitle("Average score per session");
        averageScorePerSession.setLegendVisible(true);
        averageScorePerSession.setCreateSymbols(true);

        //AREACHARTS OVERALL Best Score Per Session
        seriesBestScorePlayer = new XYChart.Series<>();
        seriesBestScorePlayer.setName("Player");
        seriesBestScoreComputer = new XYChart.Series<>();
        seriesBestScoreComputer.setName("Computer");

        yAxisScore = new NumberAxis();
        yAxisScore.setLabel("Score");
        xAxisGameNo3 = new NumberAxis();
        xAxisGameNo3.setLabel("Game No.");
        bestScorePerSession = new AreaChart(xAxisGameNo3, yAxisScore);


        //Statistics
        tabPane1 = new TabPane();
        tabPane2 = new TabPane();

        durationPerTurnLastGameSessionTab = new Tab("Time Per Move");
        durationPerTurnLastGameSessionTab.setContent(durationLastGameSession);

        pointPerTurnLastGameSessionTab = new Tab("Points Per Turn");
        pointPerTurnLastGameSessionTab.setContent(pointsPerTurnLastGameSession);

        durationPerSessionTab = new Tab("Time Per Session");
        durationPerSessionTab.setContent(durationPerSession);

        averageScorePerSessionTab = new Tab("Average Score");
        averageScorePerSessionTab.setContent(averageScorePerSession);

        bestScorePerSessionTab = new Tab("Best Score");
        bestScorePerSessionTab.setContent(bestScorePerSession);


//        tileByColorTab = new Tab("TilesByColor");
//        tileByColorTab.setContent(mostPlayedTilesByColor);
//
//        tileByShapeTab = new Tab("TilesByShape");
//        tileByShapeTab.setContent(mostPlayedTilesByShape);

//        tabPane.getTabs().addAll(durationPerTurnTabLastGameSession, pointPerTurnTabLastGameSession, tileByColorTab, tileByShapeTab, durationPerSessionTab, averageScorePerSessionTab);
        tabPane1.getTabs().addAll(durationPerTurnLastGameSessionTab, pointPerTurnLastGameSessionTab);
        tabPane1.getSelectionModel().select(durationPerTurnLastGameSessionTab);
        tabPane2.getTabs().addAll(bestScorePerSessionTab, durationPerSessionTab, averageScorePerSessionTab);
        tabPane2.getSelectionModel().select(bestScorePerSessionTab);
        lastGameButton = new Button("Last Game");
        allGamesButton = new Button("All Games");
        upperButtons = new HBox(0.5, lastGameButton, allGamesButton);
        vBox = new VBox(0, tabPane1, tabPane2, upperButtons);
    }

    private void layoutNodes() {
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        logo.setCache(true);
        tabPane1.setMaxWidth(250);
        tabPane1.setPrefHeight(700);
        tabPane2.setMaxWidth(250);
        tabPane2.setPrefHeight(700);
        vBox.setPrefHeight(700);
        tabPane1.setPadding(new Insets(0, 30, 0, 0));
        tabPane1.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane1.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        tabPane1.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        tabPane2.setPadding(new Insets(0, 30, 0, 0));
        tabPane2.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane2.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        tabPane2.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
        lastGameButton.setPrefSize(160, 50);
        allGamesButton.setPrefSize(160, 50);
        lastGameButton.getStyleClass().add("stat-lastGameButton");
        allGamesButton.getStyleClass().add("stat-allGamesButton");
        setAlignment(logo, Pos.TOP_CENTER);
        setMargin(logo, new Insets(40, 0, 0, 0));
        setTop(logo);
        setCenter(vBox);
        setAlignment(tabPane2, Pos.TOP_CENTER);
        setAlignment(tabPane1, Pos.TOP_CENTER);
        back.setPrefSize(220, 60);
        setBottom(back);
        vBox.setAlignment(Pos.TOP_CENTER);
        upperButtons.setAlignment(Pos.BOTTOM_CENTER);
        setAlignment(back, Pos.BASELINE_CENTER);
        setAlignment(vBox, Pos.CENTER);
        upperButtons.setStyle("-fx-opacity: 0;");
        Image background = new Image("images/tiles3D_line_op1.png");
        BackgroundImage bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                                                      new BackgroundPosition(Side.LEFT, 0, false, Side.BOTTOM, 0,
                                                                             false), new BackgroundSize(100, 100, true,
                                                                                                        true, false, true));
        setBackground(new Background(bgImage));
        tabPane1.getTabs().get(0).getStyleClass().add("first-tab");
        tabPane2.getTabs().get(0).getStyleClass().add("first-tab");
        lastGameButton.getStyleClass().add("stat-button");
        allGamesButton.getStyleClass().add("stat-button");

    }

    private void fixTabsAlignment() {
        Platform.runLater(() -> {
            try {
                final StackPane region1 = (StackPane) tabPane1.lookup(".headers-region");
                final StackPane regionTop1 = (StackPane) tabPane1.lookup(".tab-pane:top *.tab-header-area");
                regionTop1.widthProperty().addListener((arg0, arg1, arg2) -> {
                    Insets in = regionTop1.getPadding();
                    regionTop1.setPadding(new Insets(
                            in.getTop(),
                            in.getRight(),
                            in.getBottom(),
                            arg2.doubleValue() / 2 - region1.getWidth() / 2));
                });
                final StackPane region2 = (StackPane) tabPane2.lookup(".headers-region");
                final StackPane regionTop2 = (StackPane) tabPane2.lookup(".tab-pane:top *.tab-header-area");
                regionTop2.widthProperty().addListener((arg0, arg1, arg2) -> {
                    Insets in = regionTop1.getPadding();
                    regionTop2.setPadding(new Insets(
                            in.getTop(),
                            in.getRight(),
                            in.getBottom(),
                            arg2.doubleValue() / 2 - region2.getWidth() / 2));
                });
            } catch (NullPointerException ignored) {

            }

        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), e -> {
            tabPane1.setMaxWidth(1100);
            tabPane1.setStyle("-fx-opacity: 1;");
            tabPane2.setMaxWidth(1100);
            tabPane2.setStyle("-fx-opacity: 1;");
            upperButtons.setStyle("-fx-opacity: 1;");

        }));
        timeline.play();
    }

    public Tab getBestScorePerSessionTab() {
        return bestScorePerSessionTab;
    }

    Tab getDurationPerTurnLastGameSessionTab() {
        return durationPerTurnLastGameSessionTab;
    }

    TabPane getTabPane2() {
        return tabPane2;
    }


    Button getLastGameButton() {
        return lastGameButton;
    }

    Button getAllGamesButton() {
        return allGamesButton;
    }

    TabPane getTabPane1() {
        return tabPane1;
    }

    AreaChart getDurationPerSession() {

        return durationPerSession;
    }


    XYChart.Series getSeriesDurationPerGameSession() {
        return seriesDurationPerGameSession;
    }


    AreaChart getBestScorePerSession() {
        return bestScorePerSession;
    }


    XYChart.Series getSeriesBestScorePlayer() {
        return seriesBestScorePlayer;
    }

    XYChart.Series getSeriesBestScoreComputer() {
        return seriesBestScoreComputer;
    }


    AreaChart getAverageScorePerSession() {
        return averageScorePerSession;
    }


    XYChart.Series getSeriesAverageScorePlayer() {
        return seriesAverageScorePlayer;
    }


    XYChart.Series getSeriesAverageScoreComputer() {
        return seriesAverageScoreComputer;
    }


    Button getBack() {
        return back;
    }


    AreaChart getDurationLastGameSession() {

        return durationLastGameSession;
    }
//
//    PieChart getMostPlayedTilesByColor() {
//        return mostPlayedTilesByColor;
//    }
//
//    PieChart getMostPlayedTilesByShape() {
//        return mostPlayedTilesByShape;
//    }

    XYChart.Series getSeriesDurationLastGameSessionPlayer() {
        return seriesDurationLastGameSessionPlayer;
    }

    XYChart.Series getSeriesDurationLastGameSessionComputer() {
        return seriesDurationLastGameSessionComputer;
    }

    AreaChart getPointsPerTurnLastGameSession() {
        return pointsPerTurnLastGameSession;
    }

    XYChart.Series getSeriesPointsPerTurnLastGameSessionPlayer() {
        return seriesPointsPerTurnLastGameSessionPlayer;
    }

    XYChart.Series getSeriesPointsPerTurnLastGameSessionComputer() {
        return seriesPointsPerTurnLastGameSessionComputer;
    }
}