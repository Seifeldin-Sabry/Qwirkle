package qwirkle.view.popupFrame;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class PopupPresenter {

    private final PopupView view;
    private Timeline timeline;
    private Timeline rotation;
    private SequentialTransition seq;


    public PopupPresenter(Stage stage, PopupView view, String text, double width, double height, double startTime) {
        this.view = view;
        updateView(text);
        popup(stage, width, height, startTime);
    }

    public PopupPresenter(Stage stage, PopupView view, String text, double width, double height, double duration, boolean calculatingMove) {
        this.view = view;
        if (calculatingMove) {
            updateView(text, true);
        } else {
            updateView(text);
        }
        popup(stage, width, height, duration);
    }


    private void popup(Stage primaryStage, double width, double height, double duration) {
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
        stage.show();
        double primaryStageHeight = primaryStage.getHeight();
        stage.setY((primaryStageHeight - stage.getHeight())/2);
        KeyFrame firstKeyFrame = new KeyFrame(Duration.seconds(duration - 0.25),
                event -> {
                    try {
                        fadeOut(view);
                    } catch (NullPointerException ignored) {
                    }
                });
        KeyFrame secondKeyFrame = new KeyFrame(Duration.seconds(duration),
                event -> {
                    try {
                        stage.close();
                        timeline.stop();
                    } catch (NullPointerException ignored) {
                    }
                });
        timeline = new Timeline(firstKeyFrame, secondKeyFrame);
        fadeIn(view);
        timeline.setDelay(Duration.seconds(0.1));
        timeline.play();
    }


    private void updateView(String text) {
        view.getLabel().setText(text);
    }
    private void updateView(String text, boolean calculatingMove) {
        KeyValue kv1 = new KeyValue(view.getImageView().rotateProperty(),360);
        KeyFrame kf1 = new KeyFrame(Duration.millis(1750), kv1);
        KeyFrame kf2 = new KeyFrame(Duration.millis(2000), e -> {
            seq.stop();
        });
        rotation = new Timeline(kf1, kf2);
        seq = new SequentialTransition(rotation);
        view.getLabel().setText(text);
        view.getVBox().getChildren().clear();
        view.getVBox().getChildren().addAll(view.getImageView(), view.getLabel());
        view.getVBox().setPadding(new Insets(20, 0, 20, 0));
        seq.play();
    }

    private void fadeIn(Pane object){
        KeyValue kv1 = new KeyValue(object.opacityProperty(), 1);
        Timeline scaling = new Timeline( new KeyFrame(Duration.millis(250), kv1));
        seq = new SequentialTransition(scaling);
        object.setOpacity(0);
        seq.play();
    }
    private void fadeOut(Pane object){
        KeyValue kv1 = new KeyValue(object.opacityProperty(), 0);
        Timeline scaling = new Timeline( new KeyFrame(Duration.millis(250), kv1));
        seq = new SequentialTransition(scaling);
        object.setOpacity(1);
        seq.play();
    }
}