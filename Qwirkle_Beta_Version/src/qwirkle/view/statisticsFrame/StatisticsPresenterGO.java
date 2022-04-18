package qwirkle.view.statisticsFrame;

import javafx.scene.chart.XYChart;
import qwirkle.data.Database;
import qwirkle.view.gamePlayFrame.GamePlayView;

import java.util.List;


public class StatisticsPresenterGO {
    private StatisticsView view;
    private GamePlayView gamePlayView;
    private Database database;

    public StatisticsPresenterGO(StatisticsView view, GamePlayView gamePlayView) {
        this.view = view;
        this.gamePlayView = gamePlayView;
        database = Database.getInstance();
        loadStatistics();
        addEventHandler();
    }

    private void addEventHandler() {
        view.getBack().setOnAction(event -> setBack());
    }

    private void setBack() {
        this.view.getScene().setRoot(gamePlayView);
    }

    private void loadStatistics() {
        loadDurationPerTurnLastGame();
        loadPointsPerTurnLastGame();
//        loadTilesByShape();
//        loadTilesByColor();
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
