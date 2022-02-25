package appName;
import appName.model.TextInputView;
import appName.view.AppNamePresenter;
import appName.view.AppNameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Runner extends Application {
    //    --module-path "/Users/sakis/Documents/javafx-sdk-17.0.2/lib" --add-modules=javafx.controls,javafx.media
    @Override
    public void start(Stage primaryStage) {
        TextInputView model = new TextInputView();
        AppNameView view = new AppNameView();

        new AppNamePresenter(model, view);
        primaryStage.setScene(new Scene(view));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
