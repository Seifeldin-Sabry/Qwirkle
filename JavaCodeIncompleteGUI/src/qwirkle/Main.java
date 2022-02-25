package qwirkle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import qwirkle.view.QwirkleGameScreenPresenter;
import qwirkle.view.QwirkleGameScreenView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        QwirkleGameScreenView view = new QwirkleGameScreenView();
        new QwirkleGameScreenPresenter(view);
        primaryStage.setScene(new Scene(view));
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("You cant leave this, unless you press ESCAPE");
        primaryStage.show();
    }

    public static void main(String[] args) {
        /*
            Add this option inside your VM-option
            Edit configuration (top right corner)

            --module-path "/home/seif/Documents/lib/javafx-sdk-17.0.2/lib" --add-modules javafx.controls,javafx.fxml

            Don't forget to change your path
         */
        launch(args);
    }
}
