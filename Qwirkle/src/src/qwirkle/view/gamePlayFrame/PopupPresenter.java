package src.qwirkle.view.gamePlayFrame;

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

    PopupPresenter(Stage stage, PopupView view, String text, double width, double height){
        this.view = view;
        updateView(text);
        createPopup(stage, width, height);
    }

    private void createPopup(Stage primaryStage, double width, double height){
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
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),
                event -> {
                    try {
                        stage.close();
                    } catch (NullPointerException ignored) {
                    }
                }));
        timeline.play();
    }


    private void updateView(String text){
        view.getLabel().setText(text);
    }
}
