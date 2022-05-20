package qwirkle.view.statisticsFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
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


    //Last-Session Statistics
    private LineChart durationLastGameSession;
    private XYChart.Series seriesDurationLastGameSessionPlayer;
    private NumberAxis yAxisTime;
    private NumberAxis xAxisTurnNo;

    private LineChart pointsPerTurnLastGameSession;
    private XYChart.Series seriesPointsPerTurnLastGameSessionPlayer;
    private XYChart.Series seriesPointsPerTurnLastGameSessionComputer;
    private NumberAxis yAxisPoints;
    private NumberAxis xAxisTurnNo2;

    //All Sessions Statistics

    private TabPane tabPane1;
    private TabPane tabPane2;
    private Tab durationPerTurnLastGameSessionTab;
    private Tab pointPerTurnLastGameSessionTab;

    private Tab durationPerSessionTab;

    private Tab averageScorePerSessionTab;

    private Tab bestScorePerSessionTab;

    //duration perSession
    private LineChart durationPerSession;
    private NumberAxis yAxisDuration;
    private NumberAxis xAxisGameNo1;
    private XYChart.Series seriesDurationPerGameSession;

    //player and computer score persession
    private LineChart bestScorePerSession;
    private NumberAxis yAxisScore;
    private NumberAxis xAxisGameNo3;
    private XYChart.Series seriesBestScorePlayer;
    private XYChart.Series seriesBestScoreComputerAI;
    private XYChart.Series seriesBestScoreComputerEASY;


    //average score per turn per session
    private LineChart averageScorePerSession;
    private NumberAxis yAxisAverageScore;
    private NumberAxis xAxisGameNo2;
    private XYChart.Series seriesAverageScorePlayer;
    private XYChart.Series seriesAverageScoreComputerAI;
    private XYChart.Series seriesAverageScoreComputerEASY;

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

        //LineChartS GAMESESSION
        seriesDurationLastGameSessionPlayer = new XYChart.Series<>();
        seriesDurationLastGameSessionPlayer.setName(playerName);
        yAxisTime = new NumberAxis();
        yAxisTime.setLabel("Time (seconds)");
        xAxisTurnNo = new NumberAxis();
        xAxisTurnNo.setLabel("Turn No.");
        xAxisTurnNo.setTickUnit(1);
        durationLastGameSession = new LineChart(xAxisTurnNo, yAxisTime);
        durationLastGameSession.setTitle("Duration per Turn last game session");
        durationLastGameSession.setLegendVisible(true);
        durationLastGameSession.setCreateSymbols(true);
        durationLastGameSession.setVerticalGridLinesVisible(false);
        durationLastGameSession.setHorizontalGridLinesVisible(false);
        durationLastGameSession.setPadding(new Insets(20, 20, 0, 0));

        seriesPointsPerTurnLastGameSessionPlayer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionPlayer.setName(playerName);
        seriesPointsPerTurnLastGameSessionComputer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionComputer.setName("Computer");
        yAxisPoints = new NumberAxis();
        yAxisPoints.setLabel("Points");
        xAxisTurnNo2 = new NumberAxis();
        xAxisTurnNo2.setLabel("Turn No.");
        xAxisTurnNo2.setTickUnit(1);
        pointsPerTurnLastGameSession = new LineChart(xAxisTurnNo2, yAxisPoints);
        pointsPerTurnLastGameSession.setTitle("Points Per Turn of last game session");
        pointsPerTurnLastGameSession.setVerticalGridLinesVisible(false);
        pointsPerTurnLastGameSession.setHorizontalGridLinesVisible(false);
        pointsPerTurnLastGameSession.setPadding(new Insets(20, 20, 0, 0));


        //LineChartS OVERALL Duration
        seriesDurationPerGameSession = new XYChart.Series<>();
        seriesDurationPerGameSession.setName("Duration");

        yAxisDuration = new NumberAxis();
        yAxisDuration.setLabel("Time (seconds)");
        xAxisGameNo1 = new NumberAxis();
        xAxisGameNo1.setLabel("Last 30 games");
        xAxisGameNo1.setTickUnit(1);
        durationPerSession = new LineChart(xAxisGameNo1, yAxisDuration);
        durationPerSession.setTitle("Duration per session");
        durationPerSession.setLegendVisible(true);
        durationPerSession.setCreateSymbols(true);
        durationPerSession.setVerticalGridLinesVisible(false);
        durationPerSession.setHorizontalGridLinesVisible(false);
        durationPerSession.setPadding(new Insets(20, 20, 0, 0));


        //LineChartS OVERALL Average Score
        seriesAverageScorePlayer = new XYChart.Series<>();
        seriesAverageScorePlayer.setName("Player");
        seriesAverageScoreComputerAI = new XYChart.Series<>();
        seriesAverageScoreComputerAI.setName("Computer AI");
        seriesAverageScoreComputerEASY = new XYChart.Series<>();
        seriesAverageScoreComputerEASY.setName("Computer EASY");

        yAxisAverageScore = new NumberAxis();
        yAxisAverageScore.setLabel("Score");
        xAxisGameNo2 = new NumberAxis();
        xAxisGameNo2.setLabel("Last 30 games");
        xAxisGameNo2.setTickUnit(1);
        averageScorePerSession = new LineChart(xAxisGameNo2, yAxisAverageScore);
        averageScorePerSession.setTitle("Average score per session");
        averageScorePerSession.setLegendVisible(true);
        averageScorePerSession.setCreateSymbols(true);
        averageScorePerSession.setVerticalGridLinesVisible(false);
        averageScorePerSession.setHorizontalGridLinesVisible(false);
        averageScorePerSession.setPadding(new Insets(20, 20, 0, 0));

        //LineChartS OVERALL Best Score Per Session
        seriesBestScorePlayer = new XYChart.Series<>();
        seriesBestScorePlayer.setName("Player");
        seriesBestScoreComputerAI = new XYChart.Series<>();
        seriesBestScoreComputerAI.setName("Computer AI");
        seriesBestScoreComputerEASY = new XYChart.Series<>();
        seriesBestScoreComputerEASY.setName("Computer EASY");

        yAxisScore = new NumberAxis();
        yAxisScore.setLabel("Score");
        xAxisGameNo3 = new NumberAxis();
        xAxisGameNo3.setLabel("Last 30 games");
        xAxisGameNo3.setTickUnit(1);
        bestScorePerSession = new LineChart(xAxisGameNo3, yAxisScore);
        bestScorePerSession.setTitle("Highest score in a turn per session");
        bestScorePerSession.setVerticalGridLinesVisible(false);
        bestScorePerSession.setHorizontalGridLinesVisible(false);
        bestScorePerSession.setPadding(new Insets(20, 20, 0, 0));


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

        tabPane1.getTabs().addAll(pointPerTurnLastGameSessionTab, durationPerTurnLastGameSessionTab);
        tabPane1.getSelectionModel().select(pointPerTurnLastGameSessionTab);
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

    //Resize the tab length with delay to let auto-alignment do its magic when tab-labels are set "aligned-center" with css
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

    LineChart getDurationPerSession() {

        return durationPerSession;
    }


    XYChart.Series getSeriesDurationPerGameSession() {
        return seriesDurationPerGameSession;
    }


    LineChart getBestScorePerSession() {
        return bestScorePerSession;
    }


    XYChart.Series getSeriesBestScorePlayer() {
        return seriesBestScorePlayer;
    }

    XYChart.Series getSeriesBestScoreComputerAI() {
        return seriesBestScoreComputerAI;
    }


    LineChart getAverageScorePerSession() {
        return averageScorePerSession;
    }


    XYChart.Series getSeriesAverageScorePlayer() {
        return seriesAverageScorePlayer;
    }


    XYChart.Series getSeriesAverageScoreComputerAI() {
        return seriesAverageScoreComputerAI;
    }

    XYChart.Series getSeriesAverageScoreComputerEASY() {
        return seriesAverageScoreComputerEASY;
    }

    XYChart.Series getSeriesBestScoreComputerEASY() {
        return seriesBestScoreComputerEASY;
    }

    Button getBack() {
        return back;
    }


    LineChart getDurationLastGameSession() {

        return durationLastGameSession;
    }

    XYChart.Series getSeriesDurationLastGameSessionPlayer() {
        return seriesDurationLastGameSessionPlayer;
    }

    LineChart getPointsPerTurnLastGameSession() {
        return pointsPerTurnLastGameSession;
    }

    XYChart.Series getSeriesPointsPerTurnLastGameSessionPlayer() {
        return seriesPointsPerTurnLastGameSessionPlayer;
    }

    XYChart.Series getSeriesPointsPerTurnLastGameSessionComputer() {
        return seriesPointsPerTurnLastGameSessionComputer;
    }

    //Axis getters

    NumberAxis getxAxisTurnNo() {
        return xAxisTurnNo;
    }

    NumberAxis getxAxisTurnNo2() {
        return xAxisTurnNo2;
    }

    NumberAxis getxAxisGameNo1() {
        return xAxisGameNo1;
    }

    NumberAxis getxAxisGameNo2() {
        return xAxisGameNo2;
    }

    NumberAxis getxAxisGameNo3() {
        return xAxisGameNo3;
    }
}