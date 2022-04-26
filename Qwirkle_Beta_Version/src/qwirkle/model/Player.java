package qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Seifeldin Ismail
 */
public class Player {
    private String name;
    private Deck deck;
    private Grid grid;
    private Bag bag;
    private final MoveValidator moveValidator;


    public Player(String name, Bag bag, Grid grid) {
        this.name = name;
        this.deck = new Deck();
        deck.refill(bag);
        this.grid = grid;
        this.bag = bag;
        this.moveValidator = new MoveValidator(this.getDeck());
    }

    public boolean makeMove(Tile tile, Move.Coordinate coord){
        Move move = new Move(tile, coord);

        if (deck.getTilesInDeck().contains(tile)) {
            grid.boardAddMove(move);
            deck.getTilesInDeck().remove(move.getTile());
            return true;
        }
        return false;
    }

    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    public Deck getDeck() {
        return deck;
    }

    public Bag getBag() {
        return bag;
    }

    public void makeMove(Move move) {
        makeMove(move.getTile(), move.getCoordinate());
    }

    public Grid getBoard() {
        return grid;
    }

    public void save(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_player(player_id, player_name)
                                 VALUES (nextval('player_id_seq'),?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setString(1,getName());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }
//        System.out.printf("Saved player %s\n",name);
    }

    public String getName(){
        return name;
    }
}
