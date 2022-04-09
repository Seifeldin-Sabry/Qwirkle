package src.qwirkle.view.statisticsFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class StatisticsView extends BorderPane {
    private ImageView logo;
    private Text text;
    private Label title;
    private Button back;
    private Button rules;
    private Button quit;
    private Button tab1;
    private Button tab2;
    private Button tab3;
    private Button tab4;
    private VBox vBox;
    private HBox tabs;
    private HBox menuButtons;

    public StatisticsView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        logo = new ImageView(new Image("images/logoMAX.png"));
        back = new Button("Back");
        title = new Label("Coming soon");
    }

    private void layoutNodes() {
        logo.setPreserveRatio(true);
        logo.setFitWidth(500);
        logo.setSmooth(true);
        logo.setCache(true);
        setAlignment(logo, Pos.TOP_CENTER);
        setMargin(logo, new Insets(40, 0, 0, 0));
        logo.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 );");
        setTop(logo);

        title.setStyle("-fx-font-size: 85; -fx-font-weight: bold;");
        setCenter(title);
        title.setAlignment(Pos.CENTER);

        back.setStyle("-fx-background-color: #FF5733; -fx-font-size: 28; -fx-text-fill: #fff; -fx-effect: " +
                "dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,10,10 ); -fx-cursor: hand; -fx-background-radius: 5;");
        setMargin(back, new Insets(0, 0,50,0));
        back.setPrefSize(220, 60);
        setBottom(back);
        setAlignment(back, Pos.CENTER);
    }

    Button getBack() {
        return back;
    }

    Button getRules() {
        return rules;
    }

    Button getQuit() {
        return quit;
    }

    Button getTab1() {
        return tab1;
    }

    Button getTab2() {
        return tab2;
    }

    Button getTab3() {
        return tab3;
    }

    Button getTab4() {
        return tab4;
    }
}
