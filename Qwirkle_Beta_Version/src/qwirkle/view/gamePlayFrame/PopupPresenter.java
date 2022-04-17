package qwirkle.view.gamePlayFrame;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
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
    private Timeline rotation;


    PopupPresenter(Stage stage, PopupView view, String text, double width, double height, double duration) {
        this.view = view;
        updateView(text);
        popup(stage, width, height, duration);
    }

    PopupPresenter(Stage stage, PopupView view, String text, double width, double height, double duration, boolean computerPlayed) {
        this.view = view;
        updateView(text, computerPlayed);
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
    private void updateView(String text, boolean computerPlayed) {
        KeyValue kv1 = new KeyValue(view.getImageView().rotateProperty(),360);
        rotation = new Timeline( new KeyFrame(Duration.millis(2000), kv1));
        SequentialTransition seq = new SequentialTransition(rotation);
        view.getLabel().setText(text);
        view.getVBox().getChildren().clear();
        view.getVBox().getChildren().addAll(view.getImageView(), view.getLabel());
        view.getVBox().setPadding(new Insets(20, 0, 20, 0));
        seq.play();
    }

    private void rotate(){
        view.getImageView().setRotate(view.getImageView().getRotate() + 45);
    }
}