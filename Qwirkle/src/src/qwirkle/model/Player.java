package src.qwirkle.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Seifeldin Ismail
 */
public class Player {
    private String name;
    private Deck deck;
    private Grid grid;
    private int points = 0;

    public Player(String name, Bag bag) {
        this.name = name;
        this.deck = new Deck();
        deck.refill(bag);
    }

    public Player(String name, Grid grid) {
        this.name = name;
        this.deck = new Deck();
        this.grid = grid;
    }

    public void makeMove(Tile tile, Move.Coordinate coord){
        Move move = new Move(tile, coord);

        if (deck.getTilesInDeck().contains(tile) && grid.validMove(move)) {
            grid.boardAddMove(move);
            deck.getTilesInDeck().remove(move.getTile());
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public void makeMove(Move move) {
        makeMove(move.getTile(), move.getCoordinate());
    }

    public Grid getBoard() {
        return grid;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void save(Connection connection){
        try {
            Connection conn = connection;
            String sql = """
                         INSERT INTO int_player(player_id, player_name)
                                 VALUES (nextval('player_id_seq'),?);
                         """;
            PreparedStatement ptsmt = connection.prepareStatement(sql);
            ptsmt.setString(1,getName());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }

    }

    public String getName(){
        return name;
    }
}
