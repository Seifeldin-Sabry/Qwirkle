package qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Turn {
    private final ArrayList<Move> moveList;
    private int points;
    private final long startTimer;
    private Timestamp time_played;
    private double turnDuration;


    public Turn() {
        this.moveList = new ArrayList<>();
        startTimer = System.nanoTime();
    }

    public int getPoints() {
        return points;
    }

    public double getTurnDuration() {
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

    public void endTurn(Grid grid){
        long end = System.nanoTime();
        this.turnDuration = (double) TimeUnit.NANOSECONDS.toSeconds(end - startTimer) / 1000;
        this.time_played = new Timestamp(end);
        calcScore(grid);
    }


    /**
     * case -1 -> only 1 tile was placed, so we add the horizontal + vertical line length
     * IF tile is connected to both a vertical line and a horizontal line, then + 2 + vertLine and HorizLine
     *
     * case 0(horizontal) ->
     * FOR every move on the horizontal line, add the vertical axis
     * for every first move we calculate the points in the direction the tiles were placed
     * @param grid : the grid of the game
     */
    private void calcScore(Grid grid){
        AtomicInteger score = new AtomicInteger();
        boolean firstMoveFlag = false;

        switch (grid.determineDirection(this)){
            case -1 ->{
                int sizeHorizontal = grid.getConnectedHorizontalArray(getMoves().get(0).getCoordinate()).size();
                int sizeVertical = grid.getConnectedVerticalArray(getMoves().get(0).getCoordinate()).size();
                if (sizeHorizontal >= 1  && sizeVertical >= 1) { // means that Tile is connected in both directions
                    if (sizeHorizontal == 5)score.addAndGet(12);
                    if (sizeVertical == 5)score.addAndGet(12);
                    else score.set(sizeHorizontal+sizeVertical+2);
                }
                else if (sizeVertical >= 1 || sizeHorizontal >= 1){
                    boolean qwirkle = false;// tile connected to either direction
                    if (sizeHorizontal == 5){
                        score.addAndGet(12); //checking for five because we are not including the tile we're placing
                        qwirkle = true;
                    }
                    if (sizeVertical == 5){
                        score.addAndGet(12);
                        qwirkle = true;
                    }
                    if(!qwirkle)score.set(sizeHorizontal+sizeVertical+1);
                }
                // tile is not connected to any direction (+1 is the to include the tile itself)
                else score.set(sizeHorizontal + sizeVertical + 1);
                setPoints(score.get());
            }
            case 0 ->{
                for (Move move:getMoves()) {
                    if (!firstMoveFlag){ // first move to calculate in the same direction line
                        List<Tile> tiles = grid.getConnectedHorizontalArray(move.getCoordinate());
                        tiles.add(move.getTile());
                        if (tiles.size() == 6) score.addAndGet(12);
                        else tiles.forEach(tile -> score.getAndIncrement());
                        firstMoveFlag = true;
                    }
                    // +1 is to include the tile itself along with the rest of the tiles
                    int size = grid.getConnectedVerticalArray(move.getCoordinate()).size() + 1;
                    if(size == 5) score.addAndGet(12);
                    else {
                        if (size > 1) score.addAndGet(size);
                    }

                }
                setPoints(score.get());
            }
            case 1 ->{
                for (Move move:getMoves()) {
                    if (!firstMoveFlag){
                        List<Tile> tiles = grid.getConnectedVerticalArray(move.getCoordinate());
                        tiles.add(move.getTile());
                        if (tiles.size() == 6) score.addAndGet(12);
                        else tiles.forEach(tile -> score.getAndIncrement());
                        firstMoveFlag = true;
                    }
                    int size = grid.getConnectedHorizontalArray(move.getCoordinate()).size() + 1;
                    if(size == 5) score.addAndGet(12);
                    else {
                        if (size > 1) score.addAndGet(size);
                    }
                }
                setPoints(score.get());
            }
            default -> setPoints(0);
        }
    }














    public void save(int turn_no){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_turn(playersession_id,turn_no,points,time_spent,time_of_play)
                         VALUES (currval('playersession_id_seq'),?,?,?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setInt(1,turn_no);
            ptsmt.setInt(2,getPoints());
            ptsmt.setDouble(3, Double.parseDouble("%.3f".formatted(turnDuration)));
            ptsmt.setTimestamp(4,time_played);
            ptsmt.executeUpdate();
            ptsmt.close();

            moveList.forEach(move -> {
                int move_no = moveList.indexOf(move) + 1;
                move.save(turn_no, move_no);
            });
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_turn");
        }
        System.out.println("Saved turnNo " +turn_no );
    }
}
