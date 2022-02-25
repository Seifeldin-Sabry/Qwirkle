package appName.model.game;

import java.util.prefs.Preferences;

public class Player {
    private final String name;
    private final boolean isHuman;
    private Bag playerBag;

    //Constructor
    public Player(){
        this.name = "Computer";
        this.isHuman = false;
    }

    public Player(String name) {
        this.name = name;
        this.isHuman = true;
        setDefaultName(name);
    }


    //Getters && Setters
    public String getName() {
        return this.name;
    }


    public boolean isHuman() {
        return isHuman;
    }

    public Bag getPlayerBag() {
        return this.playerBag;
    }
    public void setPlayerBag(Bag bag) {
        this.playerBag = bag;
    }


    //Methods

    public void setDefaultName(String defaultName) {
        Preferences prefs = Preferences.userNodeForPackage(Player.class);
        prefs.put(getName(), defaultName);
    }

    public String readDefaultName() {
        Preferences prefs = Preferences.userNodeForPackage(Player.class);
        return prefs.get(getName(), "default");
    }
    @Override
    public String toString() {
        return String.format("Player: %s\nTiles to play with:\n%s", getName(), getPlayerBag());
    }
}
