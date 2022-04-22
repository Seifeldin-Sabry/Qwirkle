package qwirkle.view.gamePlayFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PopupView extends StackPane {

    private Label label;
    private ImageView imageView;
    private VBox vBox;

    PopupView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        label = new Label();
        Image img =  new Image("/images/hourglass.png",205, 167.5, true,true );
        imageView = new ImageView(img);
        vBox = new VBox(20);
    }

    private void layoutNodes() {
        label.setStyle("-fx-font-size: 30; -fx-font-family: 'Comic Sans MS'; -fx-font-style: italic;");
        label.setPrefWidth(450);
        label.setPrefHeight(200);
        label.setAlignment(Pos.CENTER);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(label);
        vBox.setPadding(new Insets(20, 0, 20, 0));
        getChildren().add(vBox);
        setStyle("-fx-background-radius: 45; -fx-background-color: rgba(255,255,255,0.94); -fx-border-width: 3;");
        setPadding(new Insets(20, 50, 20, 50));

    }

     VBox getVBox() {
        return vBox;
    }
    ImageView getImageView() {
        return imageView;
    }

    Label getLabel() {
        return label;
    }
}