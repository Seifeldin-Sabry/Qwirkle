package qwirkle.model;

import qwirkle.data.Database;
import qwirkle.model.computer.Computer;

import java.sql.*;
import java.util.Set;

/**
 * @author : Seifeldin Ismail
 */
public class GameSession {

    private final PlayerSession playerHumanSession;
    private final PlayerSession playerComputerSession;
    private final Timestamp startTime;
    private long gameDuration;
    private final Grid grid;
    private final Bag bag;


    /**
     * @param humanName:       name for player1
     * @param difficultyLevel  : difficulty level for computer
     * @param isPlayerStarting : is player1 (human) starting?
     *                         These can be slightly refactored later with chained constructors if we want to implement 4 players
     * @implNote setCurrentSession: sets the first player session
     * based on who starts first with Comparator
     */
    public GameSession(String humanName, Computer.LevelOfDifficulty difficultyLevel, boolean isPlayerStarting) {
        grid = new Grid();
        bag = new Bag();
        startTime = new Timestamp(System.currentTimeMillis());
        playerHumanSession = new PlayerSession(humanName, bag, grid, isPlayerStarting);
        playerComputerSession = new PlayerSession(bag, grid, difficultyLevel, !isPlayerStarting);
        setFirstPlayerSession();
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
        if (playerHumanSession.isActive()) {
            return playerHumanSession;
        }
        if (playerComputerSession.isActive()) {
            return playerComputerSession;
        }
        return null;
    }
    private void setFirstPlayerSession(){
        if (playerHumanSession.isPlayerStarting()) {
            playerHumanSession.setActive(true);
        } else {
            playerComputerSession.setActive(true);
        }
    }

    public void setNextPlayerSession() {
        getActivePlayerSession().getPlayer().getDeck().refill(getBag());
        if (getActivePlayerSession().equals(playerHumanSession)) {
            playerComputerSession.setActive(true);
            playerHumanSession.setActive(false);
        } else {
            playerComputerSession.setActive(false);
            playerHumanSession.setActive(true);
        }
        addTurnToActiveSession();
    }

    private void addTurnToActiveSession() {
        getActivePlayerSession().add(new Turn());
    }

    public boolean isGameOver() {
        return getBag().getAmountOfTilesLeft() == 0 && getActivePlayerSession().getPlayer().getDeck().getTilesInDeck().size() == 0;
    }

    public void setEndTime() {
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        gameDuration = (endTime.getTime() - startTime.getTime()) / 1000;
    }

    public void addExtraPoints() {
        if (getActivePlayerSession().equals(playerHumanSession)) {
            int playerPoints = getPlayerSession().getLastTurn().getPoints();
            getPlayerSession().getLastTurn().setPoints(playerPoints + 6);
        } else {
            int computerPoints = getComputerSession().getLastTurn().getPoints();
            getComputerSession().getLastTurn().setPoints(computerPoints + 6);
        }
    }

    //It checks if there is a single valid move left
    public boolean hasNoMoreMoves(PlayerSession playerSession) {
        if (getBag().getAmountOfTilesLeft() == 0) {
            Set<Move> allEdges = getGrid().getAllEdges();
            for (Move move : allEdges) {
                for (int side = 0; side < move.getCoordinate().getAdjacentCoords().length; side++) {
                    final Move.Coordinate adjacentCoord = move.getCoordinate().getAdjacentCoords()[side];
                    if (getGrid().isEmpty(adjacentCoord.getRow(), adjacentCoord.getColumn())) {
                        for (Tile t : playerSession.getPlayer().getDeck().getTilesInDeck()) {
                            Move possibleMove = new Move(t, adjacentCoord);
                            if (getGrid().isValidMove(possibleMove)) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
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
                VALUES (NEXTVAL('game_id_seq'),?,?)
                """;
        try (PreparedStatement ptsmt = conn.prepareStatement(sql)) {
            ptsmt.setTimestamp(1, startTime);
            ptsmt.setLong(2, gameDuration);
            ptsmt.executeUpdate();

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rsTiles = stmt.executeQuery("SELECT * FROM INT_TILE");
            if (rsTiles.last()) {
                if (rsTiles.getRow() == getBag().getDbTiles().size()) {
                    isTilesInDataBase = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while saving to int_game-session");
            e.printStackTrace();
        }
        if (!isTilesInDataBase) getBag().getDbTiles().forEach(Tile::save);
    }

}
