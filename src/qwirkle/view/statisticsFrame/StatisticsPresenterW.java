package qwirkle.view.statisticsFrame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import qwirkle.data.Database;
import qwirkle.model.computer.Computer;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

import java.util.List;
//Statistics view Presenter. It uses the HoveredNode class to present the y-axis values of each chart on node mouseOver
public class StatisticsPresenterW {
    private final StatisticsView view;
    private final Database database;


    public StatisticsPresenterW(StatisticsView view, Stage stage) {
        this.view = view;
        database = Database.getInstance();
        this.view.getTabPane2().setPrefHeight(0);
        this.view.getTabPane1().setPrefHeight(700);
        this.view.getLastGameButton().setStyle("-fx-background-color: #FF5733; -fx-text-fill: #fff;");
        addEventHandler(stage);
        loadStatistics();
        updateStyle();
    }

    private void updateStyle() {
        view.getxAxisGameNo1().setUpperBound(view.getSeriesDurationPerGameSession().getData().size());
        view.getxAxisGameNo3().setUpperBound(view.getSeriesBestScorePlayer().getData().size());
        view.getxAxisGameNo2().setUpperBound(view.getSeriesAverageScorePlayer().getData().size());
        view.getxAxisTurnNo().setUpperBound(view.getSeriesDurationLastGameSessionPlayer().getData().size());
        view.getxAxisTurnNo2().setUpperBound(view.getSeriesPointsPerTurnLastGameSessionPlayer().getData().size());
        view.getxAxisGameNo1().setAutoRanging(false);
        view.getxAxisGameNo2().setAutoRanging(false);
        view.getxAxisGameNo3().setAutoRanging(false);
        view.getxAxisTurnNo().setAutoRanging(false);
        view.getxAxisTurnNo2().setAutoRanging(false);
        view.getSeriesPointsPerTurnLastGameSessionComputer().setName("Computer " + Database.getInstance().getLastComputerMode());
        view.getSeriesPointsPerTurnLastGameSessionComputer().getNode().getStyleClass().addAll("computer-axis-line");
        view.getSeriesBestScoreComputerAI().getNode().getStyleClass().addAll("computerAI-axis-line");
        view.getSeriesBestScoreComputerEASY().getNode().getStyleClass().addAll("computerEASY-axis-line");
        view.getSeriesAverageScoreComputerAI().getNode().getStyleClass().addAll("computerAI-axis-line");
        view.getSeriesAverageScoreComputerEASY().getNode().getStyleClass().addAll("computerEASY-axis-line");
    }

    private void addEventHandler(Stage stage) {
        view.getBack().setOnAction(event -> setBack(stage));
        tabButtons();
    }
    //Buttons conditional styling imitating tab labels behaviour
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
    //Back to WelcomeView
    private void setBack(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        this.view.getScene().setRoot(welcomeView);
    }

    private void loadStatistics() {
        loadDurationPerTurnLastGame();
        loadPointsPerTurnLastGame();
        loadDurationPerSession();
        loadAvgPointsPerSession();
        loadBestPointsPerSession();
    }
    //Load data from the database to fill the Series for each tab separately
    private void loadBestPointsPerSession() {
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getBestPointsPerSessionComputer(Computer.LevelOfDifficulty.AI)));
        view.getSeriesBestScoreComputerAI().getData().addAll(data);
        data.clear();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getBestPointsPerSessionComputer(Computer.LevelOfDifficulty.EASY)));
        view.getSeriesBestScoreComputerEASY().getData().addAll(data);
        data.clear();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getBestPointsPerSessionPlayer()));
        view.getSeriesBestScorePlayer().getData().addAll(data);
        view.getBestScorePerSession().getData().addAll(List.of(view.getSeriesBestScorePlayer()
                , view.getSeriesBestScoreComputerAI()
                , view.getSeriesBestScoreComputerEASY()));
    }

    private void loadAvgPointsPerSession() {
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getAvgPointsPerSessionComputer(Computer.LevelOfDifficulty.AI)));
        view.getSeriesAverageScoreComputerAI().getData().addAll(data);
        data.clear();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getAvgPointsPerSessionComputer(Computer.LevelOfDifficulty.EASY)));
        view.getSeriesAverageScoreComputerEASY().getData().addAll(data);
        data.clear();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getAvgPointsPerSessionPlayer()));
        view.getSeriesAverageScorePlayer().getData().addAll(data);
        view.getAverageScorePerSession().getData().addAll(List.of(view.getSeriesAverageScorePlayer()
                , view.getSeriesAverageScoreComputerAI()
                , view.getSeriesAverageScoreComputerEASY()));
    }

    private void loadDurationPerSession() {
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getDurationPerSession()));
        view.getSeriesDurationPerGameSession().getData().addAll(data);
        view.getDurationPerSession().getData().add(view.getSeriesDurationPerGameSession());
    }


    private void loadDurationPerTurnLastGame() {
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getDurationPerTurnLastGameSessionPlayer()));
        view.getSeriesDurationLastGameSessionPlayer().getData().addAll(data);
        view.getDurationLastGameSession().getData().addAll(List.of(view.getSeriesDurationLastGameSessionPlayer()));
    }

    private void loadPointsPerTurnLastGame() {
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getPointsPerTurnLastGameSessionComputer()));
        view.getSeriesPointsPerTurnLastGameSessionComputer().getData().addAll(data);
        data.clear();
        data.add(new XYChart.Data<>(0, 0));
        data.addAll(mouseOverChart(database.getPointsPerTurnLastGameSessionPlayer()));
        view.getSeriesPointsPerTurnLastGameSessionPlayer().getData().addAll(data);
        view.getPointsPerTurnLastGameSession().getData().addAll(List.of(view.getSeriesPointsPerTurnLastGameSessionPlayer()
                , view.getSeriesPointsPerTurnLastGameSessionComputer()));
        view.getSeriesPointsPerTurnLastGameSessionComputer().getNode().getStyleClass().addAll(".computer-axis-line");
    }
    //Using the HoveredNode helper-class to present the y-axis values on each tab content.
    private ObservableList<XYChart.Data<Integer, Integer>> mouseOverChart(ObservableList<XYChart.Data> series) {
        final ObservableList<XYChart.Data<Integer, Integer>> dataset = FXCollections.observableArrayList();
        int i = 0;
        while (i < series.size()) {
            final XYChart.Data<Integer, Integer> data = new XYChart.Data<>(i + 1, Integer.parseInt(series.get(i).getYValue().toString()));
            //Filling y-axis data with 0. Used for all sessions data to assist in indexing of sessions of both AI and EASY mode compared to Player's data
            if (Integer.parseInt(series.get(i).getYValue().toString()) == 0) {
                Rectangle rectangle = new Rectangle(0, 0);
                //Assigning invisible node to a series data prevents it from showing in a chart (used on 0 y-axis values)
                rectangle.setVisible(false);
                data.setNode(rectangle);
            } else {
                HoveredNode node = new HoveredNode((int) series.get(i).getYValue());
                data.setNode(node);
                dataset.add(data);
            }
            i++;
        }
        return dataset;
    }
}


