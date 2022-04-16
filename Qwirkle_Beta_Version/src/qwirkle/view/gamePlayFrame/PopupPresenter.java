package qwirkle.view.gamePlayFrame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class PopupPresenter {

    private final PopupView view;
    private Timeline timeline;


    PopupPresenter(Stage stage, PopupView view, String text, double width, double height, double duration) {
        this.view = view;
        updateView(text);
        createWhoPlaysFirst(stage, width, height, duration);
    }


    private void createWhoPlaysFirst(Stage primaryStage, double width, double height, double duration) {
        Scene scene = new Scene(view);
        Stage stage = new Stage();
        stage.setFullScreen(false);
        scene.setFill(Color.TRANSPARENT);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        stage.show();
        timeline = new Timeline(new KeyFrame(Duration.seconds(duration),
                event -> {
                    try {
                        stage.close();
                        timeline.stop();
                    } catch (NullPointerException ignored) {
                    }
                }));
        timeline.play();
    }


    private void updateView(String text) {
        view.getLabel().setText(text);
    }
}
