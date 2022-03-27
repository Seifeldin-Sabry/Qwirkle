package src.qwirkle.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Turn {
    private final ArrayList<Move> moveList;
    private int points;
    private long startTimer;
    private Timestamp time_played;
    private long turnDuration;


    public Turn() {
        this.moveList = new ArrayList<>();
        startTimer = System.currentTimeMillis();

    }

    public int getPoints() {
        return points;
    }

    public long getTurnDuration() {
        return turnDuration;
    }

    public ArrayList<Move> getMoves() {
        return moveList;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public void addNullMove(){
        moveList.add(new Move());
    }

    public void addMove(Move move) {
        moveList.add(move);
    }

    public void addAllMoves(Move... moves){
        moveList.addAll(List.of(moves));
    }

    public void addAllMoves(Collection<Move> moves){
        moveList.addAll(moves);
    }

    public void endTurn(){
        long end = System.currentTimeMillis();
        this.turnDuration = (end - startTimer) / 1000;
        this.time_played = new Timestamp(end);
    }

    public void save(Connection connection,int turn_no){
        try {
            Connection conn = connection;
            String sql = """
                         INSERT INTO int_turn(playersession_id, turn_no, time_spent, time_played)
                         VALUES (currval('playersession_id_seq'),?,?,?);
                         """;
            PreparedStatement ptsmt = connection.prepareStatement(sql);
            ptsmt.setInt(1,turn_no);
            ptsmt.setLong(2,turnDuration);
            ptsmt.setTimestamp(3,time_played);
            ptsmt.executeUpdate();
            ptsmt.close();

            moveList.forEach(move -> {
                int move_no = moveList.indexOf(move) + 1;
                move.save(conn, turn_no, move_no);
            });
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_turn");
        }
    }

}
