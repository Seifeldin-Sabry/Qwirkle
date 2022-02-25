package appName.view;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class AppNameView extends BorderPane {

    //private Node attributes
    private Button clickMe;
    public AppNameView(){
        initialiseNodes();
        layoutNodes();
    }

    public void initialiseNodes(){
        //Create and configure controls
        this.clickMe = new Button("Click me!");
    }
    public void layoutNodes(){
        this.setCenter(this.clickMe);
    }

    //Package private getter for controls to use in the presenter class
    Button getClickMe(){
        return this.clickMe;
    }
}
