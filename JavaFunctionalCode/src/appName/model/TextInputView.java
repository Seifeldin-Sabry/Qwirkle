package appName.model;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class TextInputView {
    //private attributes
    private String textArea;
    private Button button;

    //Constructor
    public TextInputView(){
//        initialiseNodes();
//        layoutNodes();
    }

    //Methods with business logic
    public void initialiseNodes(){
        this.button = new Button("Submit");
        this.textArea = String.valueOf(new TextArea("Insert your name"));
    }
    private void layoutNodes(){
        BorderPane.setAlignment(this.button, Pos.CENTER);
        BorderPane.setMargin(this.button,new Insets(10));
//        this.setCenter(this.textArea);
//        this.setBottom(this.button);
    }

    //Getters
    public String getTextArea() {
        return textArea;
    }

    Button getButton() {
        return button;
    }
}
