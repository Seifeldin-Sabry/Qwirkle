package qwirkle.model;

import qwirkle.data.Database;
import qwirkle.model.Computer.LevelOfDifficulty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author Seifeldin Ismail
 */
public class PlayerSession implements List<Turn>{

    private final Player player;
    private boolean isPlayerStarting;
    private boolean isActive;
    private final LinkedList<Turn> turnsPlayed;


    //regular player constructor
    public PlayerSession(String humanName, Bag bag,Grid grid, boolean isPlayerStarting) {
        this.player = new Player(humanName,bag, grid);
        this.isPlayerStarting = isPlayerStarting;
        this.isActive = isPlayerStarting;
        this.turnsPlayed = new LinkedList<>();
    }

    //Computer constructor
    public PlayerSession(Bag bag, Grid grid, LevelOfDifficulty difficulty, boolean isPlayerStarting) {
        this.player = new Computer(bag,grid, difficulty);
        this.isPlayerStarting = isPlayerStarting;
        this.isActive = isPlayerStarting;
        this.turnsPlayed = new LinkedList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPlayerStarting() {
        return isPlayerStarting;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Turn> getTurnsPlayed() {
        return turnsPlayed;
    }

    public void addTurn(Turn turn){
        turnsPlayed.add(turn);
    }

    public void addTurn(){
        turnsPlayed.add(new Turn());
    }

    public int getTotalScore(){
        OptionalInt score = turnsPlayed.stream().mapToInt(Turn::getPoints).reduce(Integer::sum);
        try {
            return score.getAsInt();
        }catch (NoSuchElementException e){
            return 0;
        }
    }

    public long getTotalTimeSpent(){
        OptionalLong timeSpent = turnsPlayed.stream().mapToLong(Turn::getTurnDuration).reduce(Long::sum);
        try {
            return timeSpent.getAsLong();
        }catch (NoSuchElementException e){
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Current player turn: " + getPlayer().getName();
    }

    @Override
    public int size() {
        return turnsPlayed.size();
    }

    public Turn getLastTurn(){
        return turnsPlayed.getLast();
    }

    @Override
    public boolean isEmpty() {
        return turnsPlayed.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return turnsPlayed.contains(o);
    }

    @Override
    public Iterator<Turn> iterator() {
        return turnsPlayed.iterator();
    }

    @Override
    public void forEach(Consumer<? super Turn> action) {
        turnsPlayed.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return turnsPlayed.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return turnsPlayed.toArray(a);
    }

    @Override
    public boolean add(Turn moves) {
        return turnsPlayed.add(moves);
    }

    @Override
    public boolean remove(Object o) {
        return turnsPlayed.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return turnsPlayed.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Turn> c) {
        return turnsPlayed.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Turn> c) {
        return turnsPlayed.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return turnsPlayed.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super Turn> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return turnsPlayed.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<Turn> operator) {
        List.super.replaceAll(operator);
    }

    @Override
    public void clear() {
        turnsPlayed.clear();
    }

    @Override
    public Turn get(int index) {
        return turnsPlayed.get(index);
    }

    @Override
    public Turn set(int index, Turn element) {
        return turnsPlayed.set(index, element);
    }

    @Override
    public void add(int index, Turn element) {
        turnsPlayed.add(index, element);
    }

    @Override
    public Turn remove(int index) {
        return turnsPlayed.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return turnsPlayed.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return turnsPlayed.lastIndexOf(o);
    }

    @Override
    public ListIterator<Turn> listIterator() {
        return turnsPlayed.listIterator();
    }

    @Override
    public ListIterator<Turn> listIterator(int index) {
        return turnsPlayed.listIterator(index);
    }

    @Override
    public List<Turn> subList(int fromIndex, int toIndex) {
        return turnsPlayed.subList(fromIndex, toIndex);
    }


    public void removeLast() {
        turnsPlayed.removeLast();
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
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_playersession");
        }
    }

    private void saveScore(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_score(playersession_id,total_score, tot_time_spent_turns)
                         VALUES (currval('playersession_id_seq'),?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setInt(1,getTotalScore());
            ptsmt.setLong(2,getTotalTimeSpent());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_score");
        }
//        System.out.println("Saved score");
    }

    private void saveTurns(){
        //+1 because index of 1st is 0
        turnsPlayed.forEach(turn -> turn.save(turnsPlayed.indexOf(turn) + 1));
    }


}
