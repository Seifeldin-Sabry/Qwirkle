package qwirkle.model;

import qwirkle.data.Database;

import java.sql.*;

/**
 * @author : Seifeldin Ismail
 */
public class GameSession {

    private final PlayerSession playerHumanSession;
    private final PlayerSession playerComputerSession;
    private final Timestamp startTime;
    private Timestamp endTime;
    private long gameDuration;
    private final Grid grid;
    private final Bag bag;



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
        playerHumanSession = new PlayerSession(humanName,bag,grid,isPlayerStarting);
        playerComputerSession = new PlayerSession(bag, grid, difficultyLevel, !isPlayerStarting);
        addTurnToActiveSession();
    }


    public PlayerSession getPlayerSession() {
        return playerHumanSession;
    }

    public PlayerSession getComputerSession() {
        return playerComputerSession;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

   public PlayerSession getActivePlayerSession() {
        if (playerHumanSession.isActive()){
            return playerHumanSession;
        } else return playerComputerSession;
    }

    public void setNextPlayerSession() {
        playerHumanSession.setActive(!playerHumanSession.isActive());
        playerComputerSession.setActive(!playerComputerSession.isActive());
        addTurnToActiveSession();
    }

    private void addTurnToActiveSession() {
        getActivePlayerSession().add(new Turn());
    }


    //must have in updateView
    public boolean isGameOver(){
        return getBag().getAmountOfTilesLeft() == 0 && (playerHumanSession.getPlayer().getDeck().getTilesInDeck().size()
                == 0 || playerComputerSession.getPlayer().getDeck().getTilesInDeck().size() == 0);
    }

    public void setEndTime(){
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
            if (rsTiles.last()){
                if (rsTiles.getRow() == getBag().getDbTiles().size()){
                    isTilesInDataBase = true;
                }
            }
        }catch (SQLException e){
            System.out.println("Error while saving to int_game-session");
            e.printStackTrace();
        }
        System.out.println("Saved game-session");
        if (!isTilesInDataBase) getBag().getDbTiles().forEach(Tile::save);
    }


}
