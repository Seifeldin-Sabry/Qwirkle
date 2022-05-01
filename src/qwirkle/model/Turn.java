package qwirkle.model;

import qwirkle.data.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author: Seifeldin Ismail
 */
public class Turn implements List<Move>{
    private final LinkedList<Move> moveList;
    private int points;
    private final long startTimer;
    private long turnDuration;


    public Turn() {
        this.moveList = new LinkedList<>();
        startTimer = System.currentTimeMillis();
    }

    public Turn(List<Move> moveList) {
        this.moveList = new LinkedList<>(moveList);
        startTimer = System.currentTimeMillis();
    }

    public int getPoints() {
        return points;
    }

    public long getTurnDuration() {
        return turnDuration;
    }

    public LinkedList<Move> getMoves() {
        return moveList;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public void removeLast() {
        moveList.removeLast();
    }


    public void endTurn(Grid grid){
        long end = System.currentTimeMillis();
        this.turnDuration = TimeUnit.MILLISECONDS.toSeconds(end - startTimer);
        calcScore(grid);
    }

    @Override
    public int size() {
        return moveList.size();
    }

    @Override
    public boolean isEmpty() {
        return moveList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return moveList.contains(o);
    }

    public Move getLastMove(){
        return moveList.getLast();
    }

    @Override
    public Iterator<Move> iterator() {
        return moveList.listIterator();
    }

    @Override
    public Object[] toArray() {
        return moveList.toArray();
    }

    @Override
    public boolean removeIf(Predicate<? super Move> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public Stream<Move> stream() {
        return List.super.stream();
    }

    @Override
    public void forEach(Consumer<? super Move> action) {
        moveList.forEach(action);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return moveList.toArray(a);
    }

    @Override
    public boolean add(Move move) {
        return moveList.add(move);
    }

    @Override
    public boolean remove(Object move) {
        return moveList.remove(move);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return moveList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Move> c) {
        return moveList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Move> c) {
        return moveList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return moveList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return moveList.retainAll(c);
    }

    @Override
    public void clear() {
        moveList.clear();
    }

    @Override
    public Move get(int index) {
        return moveList.get(index);
    }

    @Override
    public Move set(int index, Move element) {
        return moveList.set(index, element);
    }

    @Override
    public void add(int index, Move element) {
        moveList.add(index, element);
    }

    @Override
    public Move remove(int index) {
        return moveList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return moveList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return moveList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Move> listIterator() {
        return moveList.listIterator();
    }

    @Override
    public ListIterator<Move> listIterator(int index) {
        return moveList.listIterator(index);
    }

    @Override
    public List<Move> subList(int fromIndex, int toIndex) {
        return moveList.subList(fromIndex, toIndex);
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
    public int calcScore(Grid grid){


        AtomicInteger score = new AtomicInteger();
        boolean firstMoveFlag = false;

        switch (grid.determineDirection(this)){
            case -1 ->{
                int sizeHorizontal = grid.getConnectedHorizontalArray(getMoves().get(0).getCoordinate()).size();
                int sizeVertical = grid.getConnectedVerticalArray(getMoves().get(0).getCoordinate()).size();
                if (sizeHorizontal >= 1  && sizeVertical >= 1) { // means that Tile is connected in both directions
                    score.addAndGet(sizeHorizontal == 5 ? 12 : sizeHorizontal + 1);
                    score.addAndGet(sizeVertical == 5 ? 12 : sizeVertical + 1);
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
                return score.get();
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
                    if(size == 6) score.addAndGet(12);
                    else {
                        if (size > 1) score.addAndGet(size);
                    }

                }
                setPoints(score.get());
                return score.get();
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
                    if(size == 6) score.addAndGet(12);
                    else {
                        if (size > 1) score.addAndGet(size);
                    }
                }
                setPoints(score.get());
                return score.get();
            }
            default -> {
                setPoints(0);
                return 0;
            }
        }
    }

    public void save(int turn_no){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_turn(playersession_id,turn_no,points,time_spent)
                         VALUES (currval('playersession_id_seq'),?,?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setInt(1,turn_no);
            ptsmt.setInt(2,getPoints());
            ptsmt.setLong(3, turnDuration);
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
//        System.out.println("Saved turnNo " +turn_no );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turn: ").append("\n");
        moveList.forEach(move -> sb.append(move.toString()).append("\n"));
        return sb.toString();
    }
}
