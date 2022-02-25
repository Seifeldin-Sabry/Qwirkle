package appName.model;

import appName.model.game.DBListener;
import appName.model.game.Session;

public class Tester {
    public static void main(String[] args) {
        DBListener newListener = new DBListener();
        Session newSession = new Session();
        System.out.println(newSession);
    }
}
