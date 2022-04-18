package qwirkle.view.statisticsFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;


public class StatisticsView extends BorderPane {
    private ImageView logo;
    private Button back;


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


    private TabPane tabPane;
    private Tab durationPerTurnTabLastGameSession;
    private Tab pointPerTurnTabLastGameSession;
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
        seriesDurationLastGameSessionPlayer.setName("Player");
        seriesDurationLastGameSessionComputer = new XYChart.Series<>();
        seriesDurationLastGameSessionComputer.setName("Computer");

        yAxisTime = new NumberAxis();
        yAxisTime.setLabel("Time (s)");
        xAxisTurnNo = new NumberAxis();
        xAxisTurnNo.setLabel("Turn No.");
        durationLastGameSession = new AreaChart(xAxisTurnNo,yAxisTime);
        durationLastGameSession.setTitle("Duration per Turn last game session");
        durationLastGameSession.setLegendVisible(true);
        durationLastGameSession.setCreateSymbols(true);

        seriesPointsPerTurnLastGameSessionPlayer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionPlayer.setName("Player");
        seriesPointsPerTurnLastGameSessionComputer = new XYChart.Series<>();
        seriesPointsPerTurnLastGameSessionComputer.setName("Computer");
        yAxisPoints = new NumberAxis();
        yAxisPoints.setLabel("Points");
        xAxisTurnNo2 = new NumberAxis();
        xAxisTurnNo2.setLabel("Turn No.");
        pointsPerTurnLastGameSession = new AreaChart(xAxisTurnNo2,yAxisPoints);
        pointsPerTurnLastGameSession.setTitle("Points Per Turn of last game session");


        //AREACHARTS OVERALL Duration
        seriesDurationPerGameSession = new XYChart.Series<>();
        seriesDurationPerGameSession.setName("Duration");

        yAxisDuration = new NumberAxis();
        yAxisDuration.setLabel("Time (s)");
        xAxisGameNo = new NumberAxis();
        xAxisGameNo.setLabel("Game No.");
        durationPerSession = new AreaChart(xAxisGameNo,yAxisDuration);
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
        averageScorePerSession = new AreaChart(xAxisGameNo3,yAxisAverageScore);
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
        bestScorePerSession = new AreaChart(xAxisGameNo3,yAxisScore);




        //Statistics
        tabPane = new TabPane();
        tabPane.setMaxWidth(250);
        tabPane.setMaxHeight(800);
        tabPane.setTabMinWidth(100);
        tabPane.setTabMinHeight(50);
        tabPane.setPadding(new Insets(0, 30, 30, 0));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);

        durationPerTurnTabLastGameSession = new Tab("Time Per Move");
        durationPerTurnTabLastGameSession.setContent(durationLastGameSession);

        pointPerTurnTabLastGameSession = new Tab("Points Per Turn");
        pointPerTurnTabLastGameSession.setContent(pointsPerTurnLastGameSession);

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
        tabPane.getTabs().addAll(durationPerTurnTabLastGameSession, pointPerTurnTabLastGameSession, bestScorePerSessionTab, durationPerSessionTab, averageScorePerSessionTab);

    }

    private void layoutNodes() {
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        logo.setCache(true);
        setAlignment(logo, Pos.TOP_CENTER);
        setMargin(logo, new Insets(40, 0, 0, 0));
        setTop(logo);
        setCenter(tabPane);
        setAlignment(tabPane, Pos.CENTER);
        setMargin(back, new Insets(0, 0, 50, 0));
        back.setPrefSize(220, 60);
        setBottom(back);
        setAlignment(back, Pos.CENTER);
        Image background = new Image("images/tiles3D_line_op1.png");
        BackgroundImage bgImage = new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                new BackgroundPosition(Side.LEFT, 0, false, Side.BOTTOM, 0,
                        false), new BackgroundSize(100, 100, true,
                true, false, true));
        setBackground(new Background(bgImage));
    }

    private void fixTabsAlignment() {
        Platform.runLater(() -> {
            try {
                final StackPane region = (StackPane) tabPane.lookup(".headers-region");
                final StackPane regionTop = (StackPane) tabPane.lookup(".tab-pane:top *.tab-header-area");
                regionTop.widthProperty().addListener((arg0, arg1, arg2) -> {
                    Insets in = regionTop.getPadding();
                    regionTop.setPadding(new Insets(
                            in.getTop(),
                            in.getRight(),
                            in.getBottom(),
                            arg2.doubleValue() / 2 - region.getWidth() / 2));
                });
            } catch (NullPointerException ignored) {

            }

        });
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(600), e -> {
            tabPane.setMaxWidth(1100);
            tabPane.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-opacity: 1;");
        }));
        timeline.play();
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
