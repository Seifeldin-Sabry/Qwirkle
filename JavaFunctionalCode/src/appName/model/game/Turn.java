package appName.model.game;

import java.util.ArrayList;
import java.util.List;

public class Turn implements Runnable {
    private Score score;
    private long elapsedTime;
    private long time;
    private List<Tile> bufferList;
    private boolean finished;
    private Thread thread;
    private Board board;

    //Constructor
    public Turn(Player player, Board board) {
        this.board = board;
        this.finished = false;
        this.thread = new Thread(this, player.getName());
        initiateTurn(player);
    }

    //Getters & Setters
    public long getTime() {
        return time;
    }

    public List<Tile> getBufferList() {
        return this.bufferList;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public Score getScore() {
        return this.score;
    }

    public Board getBoard(){
        return this.board;
    }

    //Methods
    //Stores a temp list of played tiles
    public void setBufferList(Player player, Tile playedTile) {
        boolean removed = player.getPlayerBag().getTileList().remove(playedTile);
        if (removed) {
            this.bufferList.add(playedTile);
        }
    }

    //Initiate actions
    public void initiateTurn(Player player) {
        //Reset tmp tilesList for each turn
        assert false;
        this.bufferList = new ArrayList<>();
        if (this.bufferList.size() > 0) {
            this.bufferList.clear();
        }
        //Computer plays
        if (!player.isHuman()) {
            computerMoves();
            this.finished = true;
            //Player plays
        } else {
            timer();
            try {
                playerMoves();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.finished = true;
        }
    }

    //Computer playing algorithm
    private void computerMoves() {
        System.out.println("Computer plays");

    }

    //Player selection of moves
    private void playerMoves() throws InterruptedException {
        System.out.println("Player plays");
//        this.thread.wait();

    }

    //Elapsed time set after it is called second time
    public void timer() {
        long now = System.currentTimeMillis() / 1000;
        if (this.time == 0) {
            this.time = now;
            this.elapsedTime = 0;
        } else {
            this.elapsedTime = now - getTime();
            this.time = 0;
        }
    }

    //A player submits his move and ends his turn
    public void submit(Board board) {
        this.board = board;
        this.thread.notify();
        timer();
        this.finished = true;
    }

    public void exchangeTiles(List<Tile> swapped, List<Tile> gameTiles) {

    }

    public boolean isValid(){

        return false;
    }

    @Override
    public void run() {

    }
}
