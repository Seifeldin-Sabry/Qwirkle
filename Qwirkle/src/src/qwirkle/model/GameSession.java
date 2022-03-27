package src.qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author : Seifeldin Ismail
 */
public class GameSession {
    private PlayerSession currentSession;


    private final PlayerSession playerHumanSession;
    private final PlayerSession playerComputerSession;
/*
    private final PlayerSession player3;
    private final PlayerSession player4;
*/
private final Timestamp startTime;
    private Timestamp endTime;
    private long gameDuration;
    private final Grid grid;
    private final Bag bag;


    private final Comparator<PlayerSession> ORDER_BY_TURNS = Comparator.comparingInt(firstPlayerSession -> firstPlayerSession.getTurnsPlayed().size());

    private final Comparator<PlayerSession> ORDER_BY_STARTING_THEN_MOVES_PLAYED = (firstPlayerSession, secondPlayerSession) -> {
        if (Boolean.compare(firstPlayerSession.isPlayerStarting(),secondPlayerSession.isPlayerStarting()) == 0){
            return ORDER_BY_TURNS.compare(firstPlayerSession,secondPlayerSession);
        }
        return Boolean.compare(secondPlayerSession.isPlayerStarting(),firstPlayerSession.isPlayerStarting());
    };


    /**
     * @method  setCurrentSession: sets the first player session
     *          based on who starts first with Comparator
     * @param humanName: name for player1
     * @param difficultyLevel : difficulty level for computer
     * @param isPlayerStarting : is player1 (human) starting?
     * These can be slightly refactored later with chained constructors if we want to implement 4 players
     */
    public GameSession(String humanName, Computer.LevelOfDifficulty difficultyLevel, boolean isPlayerStarting) {
        grid = new Grid();
        bag = new Bag();
        startTime = new Timestamp(System.currentTimeMillis());
        playerHumanSession = new PlayerSession(humanName,bag,isPlayerStarting);
        playerComputerSession = new PlayerSession(bag, grid, difficultyLevel, !isPlayerStarting);
//        player3 = null; //for the future
//        player4 = null; //for the future
        setCurrentSession();
    }

    /**
     *
     * @return: current playerSession: player whose turn it is to play
     */
    public PlayerSession getCurrentSession() {
        return currentSession;
    }

    public PlayerSession getPlayerHumanSession() {
        return playerHumanSession;
    }

    public PlayerSession getPlayerComputerSession() {
        return playerComputerSession;
    }

    /**
     *
     * @return: playerSession: player whose turn it is to play next
     */
    public PlayerSession getNextPlayerSession(){
        List<PlayerSession> sessions = getPlayerSessions();
        if (isFirstTurn()){
            currentSession  = sessions.get(0);
            return currentSession;
        }
        if (sessions.indexOf(currentSession) == 0) {
            currentSession = sessions.get(1);
            return currentSession;
        }
        if (sessions.indexOf(currentSession) == 1) {
            currentSession = sessions.get(0);
            return currentSession;
        }
        return null;
    }


    /**
     * Adds all players ot a list and sorts them based on the moves played and whether they get to start first or not
     * @return
     */
    public ArrayList<PlayerSession> getPlayerSessions(){
        ArrayList<PlayerSession> sessions = new ArrayList<>();
        sessions.add(playerHumanSession);
        sessions.add(playerComputerSession);
        sessions.sort(ORDER_BY_STARTING_THEN_MOVES_PLAYED);
        return sessions;
    }

    private boolean isFirstTurn() {
        return playerHumanSession.getTurnsPlayed().size() == 0 && playerComputerSession.getTurnsPlayed().size() == 0;
    }


    private void setCurrentSession(){
        currentSession = getPlayerSessions().get(0);
    }

    //must have in updateView
    public boolean isGameOver(){
        return getBag().getAmountOfTilesLeft() == 0;
    }

    void setEndTime(){
        endTime =  new Timestamp(System.currentTimeMillis());
        gameDuration = (endTime.getTime() - startTime.getTime()) / 1000;
    }

    public Bag getBag() {
        return bag;
    }

    public Grid getGrid() {
        return grid;
    }


    public void save(Connection connection){
        saveSession(connection);
        saveTiles(connection);
    }

    private void saveSession(Connection connection){
        Database database = Database.getInstance();
        try {
            Connection conn = connection;
            String sql = """
                         INSERT INTO int_gamesession(game_id,start_time,end_time,game_duration, date_played)
                         VALUES (nextval('game_id_seq'),?,?,?,?)
                         """;
            PreparedStatement ptsmt = connection.prepareStatement(sql);
            ptsmt.setTimestamp(1,startTime);
            ptsmt.setTimestamp(2,endTime);
            ptsmt.setLong(3,gameDuration);
            Date dateToSave = new Date(endTime.getTime());
            ptsmt.setDate(4, dateToSave);
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            System.out.println("Error while saving to int_gamesession");
        }
    }

    private void saveTiles(Connection connection){
        Database database = Database.getInstance();
        try {
            Connection conn = connection;
            saveSession(connection);
            String sql = """
                         INSERT INTO int_tile(tile_id, game_id, shape, color)
                         VALUES (?,currval('game_id_seq'),?,?)
                         """;
            PreparedStatement ptsmt = connection.prepareStatement(sql);
            for (Tile tile : getBag().getTiles()) {
                ptsmt.setInt(1, tile.getTile_id());
                ptsmt.setString(2, tile.getShape().toString());
                ptsmt.setString(3, tile.getColor().toString());
                ptsmt.executeUpdate();
            }
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_tiles");
        }
    }
}
