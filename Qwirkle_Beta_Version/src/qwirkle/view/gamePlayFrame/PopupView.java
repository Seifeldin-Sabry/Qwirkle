package qwirkle.view.gamePlayFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PopupView extends StackPane {

    private Label label;
    private VBox vBox;

    PopupView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        label = new Label();
        vBox = new VBox(20);
    }

    private void layoutNodes() {
        label.setStyle("-fx-font-size: 30; -fx-font-family: 'Comic Sans MS'; -fx-font-style: italic;");
        label.setPrefWidth(450);
        label.setPrefHeight(200);
        label.setAlignment(Pos.CENTER);
//        vBox.setAlignment(Pos.CENTER);
//        vBox.getChildren().addAll(label);
        getChildren().add(label);
        setStyle("-fx-background-radius: 45; -fx-background-color: rgba(255,255,255, 0.8); -fx-border-width: 3;");
        setPadding(new Insets(20, 50, 20, 50));

    }

    public VBox getVBox() {
        return vBox;
    }

    Label getLabel() {
        return label;
    }
}
