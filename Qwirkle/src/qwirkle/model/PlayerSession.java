package qwirkle.model;

import qwirkle.data.Database;
import qwirkle.model.Computer.LevelOfDifficulty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

/**
 * @author Seifeldin Ismail
 */
public class PlayerSession {

    private final Player player;
    private boolean isPlayerStarting;
    private final List<Turn> turnsPlayed;



    //regular player constructor
    public PlayerSession(String humanName, Bag bag, boolean isPlayerStarting) {
        this.player = new Player(humanName,bag);
        this.isPlayerStarting = isPlayerStarting;
        this.turnsPlayed = new ArrayList<>();
    }

    //Computer constructor
    public PlayerSession(Bag bag, Grid grid, LevelOfDifficulty difficulty, boolean isPlayerStarting) {
        this.player = new Computer(bag,grid, difficulty);
        this.turnsPlayed = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPlayerStarting() {
        return isPlayerStarting;
    }

    public List<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }

    public void addTurn(Turn turn){
        turnsPlayed.add(turn);
    }

    public int getTotalScore(){
        OptionalInt score = turnsPlayed.stream().mapToInt(Turn::getPoints).reduce(Integer::sum);
        try {
            return score.getAsInt();
        }catch (NoSuchElementException e){
            return 0;
        }
    }

    public double getTotalTimeSpent(){
        OptionalDouble timeSpent = turnsPlayed.stream().mapToDouble(Turn::getTurnDuration).reduce(Double::sum);
        try {
            return timeSpent.getAsDouble();
        }catch (NoSuchElementException e){
            return 0;
        }
    }

    public void save(){
        getPlayer().save();
        savePlayerSession();
        saveTurns();
        saveScore();
    }

    private void savePlayerSession(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_playersession(playersession_id, player_id, game_id)
                         VALUES (nextval('playersession_id_seq'),currval('player_id_seq'),currval('game_id_seq'));
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
//            ptsmt.setInt(1,playersession_id);
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_playersession");
        }
        System.out.println("Saved playerSession");
    }

    private void saveScore(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_score(playersession_id,total_score, tot_time_spent_turns)
                         VALUES (currval('playersession_id_seq'),?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
//            ptsmt.setInt(1, player_seq_ID);
//            ptsmt.setInt(2, game_id);
            ptsmt.setInt(1,getTotalScore());
            ptsmt.setDouble(2,getTotalTimeSpent());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_score");
        }
        System.out.println("Saved score");
    }

    private void saveTurns(){
        //+1 because index of 1st is 0
        for (Turn turn : turnsPlayed) {
            turn.save( turnsPlayed.indexOf(turn) + 1);
        }
    }

    //for testing
    @Override
    public String toString() {
        return "Current player turn: " + getPlayer().getName();
    }
}
