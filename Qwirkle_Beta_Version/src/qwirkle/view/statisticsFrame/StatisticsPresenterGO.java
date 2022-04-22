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
        this.view.getTabPane2().setPrefHeight(0);
        this.view.getTabPane1().setPrefHeight(700);
        this.view.getLastGameButton().setStyle("-fx-background-color: #FF5733; -fx-text-fill: #fff;");
        loadStatistics();
        addEventHandler();
    }

    private void addEventHandler() {
        view.getBack().setOnAction(event -> setBack());
        tabButtons();
    }

    private void tabButtons() {
        view.getLastGameButton().setOnAction(event -> {
            view.getTabPane1().setPrefHeight(700);
            view.getTabPane2().setPrefHeight(0);
            view.getLastGameButton().setStyle("-fx-background-color: #FF5733; -fx-text-fill: #fff;");
            view.getAllGamesButton().setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-text-base-color;");
            view.getTabPane1().requestFocus();
        });
        view.getAllGamesButton().setOnAction(event -> {
            view.getTabPane2().setPrefHeight(700);
            view.getTabPane1().setPrefHeight(0);
            view.getLastGameButton().setStyle("-fx-background-color: transparent; -fx-text-fill: -fx-text-base-color;");
            view.getAllGamesButton().setStyle("-fx-background-color: #FF5733; -fx-text-fill: #fff;");
            view.getTabPane2().requestFocus();
        });
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

//    private void loadTilesByShape() {
//        view.getMostPlayedTilesByShape().getData().addAll(database.getTileByShapeChart());
//    }
//
//    private void loadTilesByColor() {
//        view.getMostPlayedTilesByColor().getData().addAll(database.getTileByColorChart());
//    }

    private void loadDurationPerTurnLastGame() {
//        XYChart.Series seriesDurationLastGameSessionComputer = view.getSeriesDurationLastGameSessionComputer();
//        seriesDurationLastGameSessionComputer.getData().addAll(database.getDurationPerTurnLastGameSessionComputer());

        XYChart.Series seriesDurationLastGameSessionPlayer = view.getSeriesDurationLastGameSessionPlayer();
        seriesDurationLastGameSessionPlayer.getData().addAll(database.getDurationPerTurnLastGameSessionPlayer());
//        view.getDurationLastGameSession().getData().addAll(List.of(seriesDurationLastGameSessionPlayer,seriesDurationLastGameSessionComputer));
        view.getDurationLastGameSession().getData().addAll(List.of(seriesDurationLastGameSessionPlayer));
    }

    private void loadPointsPerTurnLastGame() {
        XYChart.Series seriesPointsLastGameSessionComputer = view.getSeriesPointsPerTurnLastGameSessionComputer();
        seriesPointsLastGameSessionComputer.getData().addAll(database.getPointsPerTurnLastGameSessionComputer());

        XYChart.Series seriesPointsLastGameSessionPlayer = view.getSeriesPointsPerTurnLastGameSessionPlayer();
        seriesPointsLastGameSessionPlayer.getData().addAll(database.getPointsPerTurnLastGameSessionPlayer());
        view.getPointsPerTurnLastGameSession().getData().addAll(List.of(seriesPointsLastGameSessionPlayer,seriesPointsLastGameSessionComputer));
    }
}
