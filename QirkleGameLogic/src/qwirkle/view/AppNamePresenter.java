package qwirkle.view;

public class AppNamePresenter {


    private final AppNameView view;

    public AppNamePresenter( AppNameView view) {

        this.view = view;

        addEventHandlers();
        updateView();
    }

    private void addEventHandlers() {
        // get controls from view (by accessing the package-private getters)
        // and add event handlers, listeners, ...
    }

    private void updateView() {
        /* fills view*/
    }
}
