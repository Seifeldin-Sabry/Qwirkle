package qwirkle.view.statisticsFrame;

import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import qwirkle.data.Database;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

import java.util.List;

public class StatisticsPresenterW {
    private StatisticsView view;
    private Database database;


    public StatisticsPresenterW(StatisticsView view, Stage stage) {
        this.view = view;
        database = Database.getInstance();
        addEventHandler(stage);
        loadStatistics();
    }

    private void addEventHandler(Stage stage) {
        view.getBack().setOnAction(event -> setBack(stage));
    }

    private void setBack(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.view.getScene().setRoot(welcomeView);
    }

    private void loadStatistics() {
        loadDurationPerTurnLastGame();
        loadPointsPerTurnLastGame();
        loadTilesByShape();
        loadTilesByColor();
        loadDurationPerSession();
        loadAvgPointsPerSession();
        loadBestPointsPerSession();
    }

    private void loadBestPointsPerSession() {
        XYChart.Series seriesBestPointsPerSessionComputer = view.getSeriesBestScoreComputer();
        seriesBestPointsPerSessionComputer.getData().addAll(database.getBestPointsPerSessionComputer());

        XYChart.Series seriesBestPointsPerSessionPlayer = view.getSeriesBestScorePlayer();
        seriesBestPointsPerSessionPlayer.getData().addAll(database.getBestPointsPerSessionPlayer());
        view.getBestScorePerSession().getData().addAll(List.of(seriesBestPointsPerSessionPlayer,seriesBestPointsPerSessionComputer));
    }

    private void loadAvgPointsPerSession() {
        XYChart.Series seriesAvgPointsPerSessionComputer = view.getSeriesAverageScoreComputer();
        seriesAvgPointsPerSessionComputer.getData().addAll(database.getAvgPointsPerSessionComputer());

        XYChart.Series seriesAvgPointsPerSessionPlayer = view.getSeriesAverageScorePlayer();
        seriesAvgPointsPerSessionPlayer.getData().addAll(database.getAvgPointsPerSessionPlayer());
        view.getAverageScorePerSession().getData().addAll(List.of(seriesAvgPointsPerSessionPlayer,seriesAvgPointsPerSessionComputer));
    }

    private void loadDurationPerSession() {
        XYChart.Series seriesDurationOverall = view.getSeriesDurationPerGameSession();
        seriesDurationOverall.getData().addAll(database.getDurationPerSession());
        view.getDurationPerSession().getData().add(seriesDurationOverall);
    }

    private void loadTilesByShape() {
        view.getMostPlayedTilesByShape().getData().addAll(database.getTileByShapeChart());
    }

    private void loadTilesByColor() {
        view.getMostPlayedTilesByColor().getData().addAll(database.getTileByColorChart());
    }

    private void loadDurationPerTurnLastGame() {
        XYChart.Series seriesDurationLastGameSessionComputer = view.getSeriesDurationLastGameSessionComputer();
        seriesDurationLastGameSessionComputer.getData().addAll(database.getDurationPerTurnLastGameSessionComputer());

        XYChart.Series seriesDurationLastGameSessionPlayer = view.getSeriesDurationLastGameSessionPlayer();
        seriesDurationLastGameSessionPlayer.getData().addAll(database.getDurationPerTurnLastGameSessionPlayer());
        view.getDurationLastGameSession().getData().addAll(List.of(seriesDurationLastGameSessionPlayer,seriesDurationLastGameSessionComputer));
    }

    private void loadPointsPerTurnLastGame() {
        XYChart.Series seriesPointsLastGameSessionComputer = view.getSeriesPointsPerTurnLastGameSessionComputer();
        seriesPointsLastGameSessionComputer.getData().addAll(database.getPointsPerTurnLastGameSessionComputer());

        XYChart.Series seriesPointsLastGameSessionPlayer = view.getSeriesPointsPerTurnLastGameSessionPlayer();
        seriesPointsLastGameSessionPlayer.getData().addAll(database.getPointsPerTurnLastGameSessionPlayer());
        view.getPointsPerTurnLastGameSession().getData().addAll(List.of(seriesPointsLastGameSessionPlayer,seriesPointsLastGameSessionComputer));
    }


    }

