package qwirkle.model;

import qwirkle.data.Database;

import java.sql.*;
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
     * @implNote   setCurrentSession: sets the first player session
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
        getNextPlayerSession();
    }

    public Turn startCurrentPlayerTurn(){
        return getCurrentPlayerSession().getPlayer().startTurn();
    }

    public void addTurnToCurrentPlayerSession(Turn currentTurn){
        getCurrentPlayerSession().addTurn(currentTurn);
    }
    /**
     *
     * @return playersession currently playing
     */
    public PlayerSession getCurrentPlayerSession(){
        return currentSession;
    }


    /**
     *
     * @return: playerSession: player whose turn it is to play next
     */
    public PlayerSession getNextPlayerSession(){
        List<PlayerSession> sessions = getPlayerSessions();
        switch (sessions.size()){//case for how many players
            case 2 -> {
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
            }

            case 3 -> {
                if (isFirstTurn()){
                    currentSession  = sessions.get(0);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 0) {
                    currentSession = sessions.get(1);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 1) {
                    currentSession = sessions.get(2);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 2){
                    currentSession = sessions.get(0);
                }
            }
            case 4 -> {
                if (isFirstTurn()){
                    currentSession  = sessions.get(0);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 0) {
                    currentSession = sessions.get(1);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 1) {
                    currentSession = sessions.get(2);
                    return currentSession;
                }
                if (sessions.indexOf(currentSession) == 2){
                    currentSession = sessions.get(3);
                }
                if (sessions.indexOf(currentSession) == 3){
                    currentSession = sessions.get(0);
                    return currentSession;
                }
            }
        }
        return null;
    }


    /**
     * Adds all players ot a list and sorts them based on the moves played and whether they get to start first or not
     * @return List of valid player-session
     */
    public ArrayList<PlayerSession> getPlayerSessions(){
        ArrayList<PlayerSession> sessions = new ArrayList<>();
        sessions.add(playerHumanSession);
        sessions.add(playerComputerSession);
        sessions.sort(ORDER_BY_STARTING_THEN_MOVES_PLAYED);
        return sessions;
    }

    private boolean isFirstTurn() {
        boolean isFirstTurn = true;
        for (PlayerSession playerSession:getPlayerSessions() ) {
            if (playerSession.getTurnsPlayed().size() > 0) {
                isFirstTurn = false;
                break;
            }
        }
        return isFirstTurn;
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



    public void save() {

        boolean isTilesInDataBase = false;
        Database database = Database.getInstance();
        Connection conn = database.getConnection();
        String sql = """
                         INSERT INTO int_gamesession(game_id,start_time,game_duration)
                         VALUES (nextval('game_id_seq'),?,?)
                         """;
        try (PreparedStatement ptsmt = conn.prepareStatement(sql)){
            ptsmt.setTimestamp(1,startTime);
            ptsmt.setLong(2,gameDuration);
            ptsmt.executeUpdate();

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rsTiles = stmt.executeQuery("SELECT * FROM INT_TILE");
            //checks if tiles already exist in database
            if (rsTiles.last()){
                if (rsTiles.getRow() == getBag().getDbTiles().size()){
                    isTilesInDataBase = true;
                }
            }
        }catch (SQLException e){
            System.out.println("Error while saving to int_game-session");
            e.printStackTrace();
        }
//        ID = game_id;
        System.out.println("Saved game-session");
        if (!isTilesInDataBase) getBag().getDbTiles().forEach(Tile::save);

    }
}
