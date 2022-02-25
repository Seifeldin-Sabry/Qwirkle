package appName.model.game;

import appName.model.TextInputView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Session {
    //Attributes
    private final Player player;
    private final Player computer;
    private int turnsCounter = 0;
    private Turn turn;
    private boolean gameOver = false;
    private final Bag gameBag = new Bag();
    private Board board = new Board();
    private final Timestamp gameStart;
    private Timestamp gameEnd;
    private Score score;

    //Constructor
    public Session() {
        //Generate Players
        this.player = new Player("Sakis");
        this.computer = new Player();
        //Generate and empty "board" (2D List of played tiles)
        this.board = new Board();
        //Generate initial bag
        this.gameBag.generateTiles();
        this.gameStart = new Timestamp(currentTimeMillis());
        firstTilesDistribution();
        String firstPlayer = whoPlaysFirst();
        initializeTurns(firstPlayer, getBoard());
    }

    //Getters & Setters
    public Player getPlayer() {
        return this.player;
    }

    public Player getComputer() {
        return this.computer;
    }

    public Bag getGameBag() {
        return gameBag;
    }

    public Turn getTurn() {
        return this.turn;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board){
        this.board = board;
    }

    //Methods
    //First Tiles distribution
    public void firstTilesDistribution() {
        Random random = new Random();
        int indexPlayer;
        int indexComputer;
        int size = getGameBag().getTileList().size();
        List<Tile> tilesForPlayer = new ArrayList<>();
        List<Tile> tilesForComputer = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            indexPlayer = random.nextInt(size);
            tilesForPlayer.add(new Tile(getGameBag().getTileList().get(indexPlayer).getKeyName(),
                    getGameBag().getTileList().get(indexPlayer).getIconPath(),
                    getGameBag().getTileList().get(indexPlayer).getColor(),
                    getGameBag().getTileList().get(indexPlayer).getShape()));
            this.gameBag.removeTilesFromTheBag(indexPlayer);
            size--;
            indexComputer = random.nextInt(size);
            tilesForComputer.add(new Tile(getGameBag().getTileList().get(indexComputer).getKeyName(),
                    getGameBag().getTileList().get(indexComputer).getIconPath(),
                    getGameBag().getTileList().get(indexComputer).getColor(),
                    getGameBag().getTileList().get(indexComputer).getShape()));
            this.gameBag.removeTilesFromTheBag(indexComputer);
            size--;
        }
        this.player.setPlayerBag(new Bag(tilesForPlayer));
        this.computer.setPlayerBag(new Bag(tilesForComputer));
    }

    //Who plays first
    public String whoPlaysFirst() {
        TextInputView input = new TextInputView();
        String name = input.getTextArea();
        return "Computer";
    }

    //Turns main iteration
    private void initializeTurns(String firstPlayer, Board board) {
        while (!gameOver) {
            if (this.player.getPlayerBag().getTileList().size() == 0) {
                this.gameOver = true;
            } else if (this.computer.getPlayerBag().getTileList().size() == 0) {
                this.gameOver = true;
            } else {
                //Human player plays first
                if (firstPlayer.equals(getPlayer().getName())) {
                    refillBag(getPlayer());
                    this.turn = new Turn(getPlayer(), getBoard());
                    this.board = this.turn.getBoard();
                    refillBag(getComputer());
                    this.turn = new Turn(getComputer(), getBoard());
                    this.board = this.turn.getBoard();
                    //Computer plays first
                } else if (firstPlayer.equals(getComputer().getName())) {
                    refillBag(getComputer());
                    this.turn = new Turn(getComputer(), getBoard());
                    this.board = this.turn.getBoard();
                    refillBag(getPlayer());
                    this.turn = new Turn(getPlayer(), getBoard());
                    this.board = this.turn.getBoard();
                }
                this.turnsCounter = this.turnsCounter + 1;
            }
        }
        this.gameEnd = new Timestamp(currentTimeMillis());
    }

    //Checks if player's back is full and refills it if not
    public void refillBag(Player player) {
        if (player.getPlayerBag().getTileList().size() != 6 && getGameBag().getTileList().size() > 0) {
            int missingTiles = 6 - player.getPlayerBag().getTileList().size();
            Random random = new Random();
            List<Tile> tmpList = new ArrayList<>();
            int totalTilesLeft = getGameBag().getTileList().size();
            int index = random.nextInt(totalTilesLeft);
            for (int i = 0; i < missingTiles; i++) {
                tmpList.add(getGameBag().getTileList().get(index));
                this.gameBag.removeTilesFromTheBag(index);
                totalTilesLeft--;
            }
            player.getPlayerBag().setTileList(tmpList);
        }
    }

    //toString
    @Override
    public String toString() {
        return String.format("%s%sPieces left in the bag: %d", getPlayer(), getComputer()
                , getGameBag().getTileList().size());
    }
}
