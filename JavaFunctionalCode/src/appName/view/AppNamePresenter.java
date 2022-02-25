package appName.view;

import appName.model.TextInputView;

public class AppNamePresenter {

    private final TextInputView model;
    private final AppNameView view;

    public AppNamePresenter(TextInputView model, AppNameView view) {
        this.model = model;
        this.view = view;

        addEventHandler();
        updateView();
    }
    private void addEventHandler(){
        this.view.getClickMe().setOnAction(event -> System.out.println("CLicked button"));
    }
    private void updateView(){

    }
}
