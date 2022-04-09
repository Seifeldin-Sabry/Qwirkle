package src.qwirkle.view.introFrame;

import qwirkle.data.Database;

public class GameInitializationPresenter {

    GameInitializationView view;
    Database model;

    public GameInitializationPresenter(GameInitializationView view) {
        this.view = view;
        model = new Database();
        model.createDatabase();
        getError();
    }

    public boolean getError() {
        return model.getError();
    }
}
