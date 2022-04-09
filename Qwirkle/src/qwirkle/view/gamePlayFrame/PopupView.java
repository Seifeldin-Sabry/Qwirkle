package src.qwirkle.view.gamePlayFrame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class PopupView extends Pane {
    private Label label;

    PopupView() {
        initialiseNodes();
        layoutNodes();
    }

    private void initialiseNodes() {
        label = new Label();
    }

    private void layoutNodes() {
        label.setStyle("-fx-font-size: 30; -fx-font-family: 'Comic Sans MS'; -fx-font-style: italic;");
        label.setPrefWidth(450);
        label.setPrefHeight(200);
        label.setAlignment(Pos.CENTER);
        this.getChildren().add(label);
        setStyle("-fx-background-radius: 45; -fx-background-color: rgba(255,255,255, 0.8); -fx-border-width: 3;");
        setPadding(new Insets(200, 150, 50, 150));
    }

    Label getLabel() {
        return label;
    }
}
