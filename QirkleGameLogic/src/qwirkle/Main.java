package qwirkle;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import qwirkle.view.AppNamePresenter;
import qwirkle.view.AppNameView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        AppNameView view = new AppNameView();
        new AppNamePresenter(view);

        primaryStage.setScene(new Scene(view));
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
