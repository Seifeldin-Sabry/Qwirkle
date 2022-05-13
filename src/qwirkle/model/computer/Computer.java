package qwirkle.model.computer;

import qwirkle.data.Database;
import qwirkle.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import static qwirkle.model.Grid.MID;

public class Computer extends Player {

    final Random randomTileChooser;
    private LevelOfDifficulty levelOfDifficulty;

    public enum LevelOfDifficulty {
        EASY, AI

    }

    public Computer(Bag bag, Grid grid) {
        super("Computer", bag, grid);
        this.levelOfDifficulty = LevelOfDifficulty.EASY;
        randomTileChooser = new Random();
    }


    /**
     *
     * @return the best turn respective of the level of difficulty, null if no turn can be made
     */
    public Turn makeTurn() {
        boolean firstTurn = getBoard().getUsedSpaces().isEmpty();
        Turn turn;
        if (firstTurn) {
            turn = playFirstTurn();
        } else {
            turn = getRandomMove(getMoveValidator().getAllValidMoves(getBoard()));
            if (turn == null) {
                trade();
                return null;
            }
        }
        return turn;
    }

    private Turn playFirstTurn() {
        Set<ArrayList<Tile>> tileCombos = getMoveValidator().getLargestCombinations();
        //never empty in first turn
        int randomCombo = randomTileChooser.nextInt(tileCombos.size());
        ArrayList<Tile> tileCombo = new ArrayList<>(tileCombos.stream().toList().get(randomCombo));
        return firstTurnInRandomDirection(tileCombo);
    }

    private Turn getRandomMove(HashMap<Move, Set<Turn>> allmoves) {
        if (allmoves.isEmpty()) {
            return null;
        }
        int randomMove = randomTileChooser.nextInt(allmoves.size());
        int randomTurn = randomTileChooser.nextInt(allmoves.get(allmoves.keySet().stream().toList().get(randomMove)).size());
        return allmoves.get(allmoves.keySet().stream().toList().get(randomMove)).stream().toList().get(randomTurn);
    }

    /**
     * trades
     */
    public void trade(){
        tradeRandomNumTiles();
    }

    void tradeRandomNumTiles() {
        ArrayList<Tile> tilesToTrade = new ArrayList<>();
        ArrayList<Tile> tilesInDeck = new ArrayList<>(getDeck().getTilesInDeck());
        //has to be at least 1 tile to trade
        int randomTileNumToTrade = randomTileChooser.nextInt(getDeck().getTilesInDeck().size()) == 0 ? 1 : randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
        for (int i = 0; i < randomTileNumToTrade; i++) {
            int randomTileIndex = randomTileChooser.nextInt(tilesInDeck.size());
            tilesToTrade.add(tilesInDeck.get(randomTileIndex));
            tilesInDeck.remove(randomTileIndex);
        }
        getDeck().trade(getBag(),tilesToTrade);
    }

    Turn firstTurnInRandomDirection(ArrayList<Tile> combinationTiles) {
        ArrayList<Move> moves = new ArrayList<>();
        moves.add(new Move(combinationTiles.get(0), new Move.Coordinate(MID, MID)));

        //nothing advanced in terms of which direction to go
        int direction = randomTileChooser.nextInt(2);
        //0 for horizontal, 1 for vertical
        switch (direction) {
            case 0 -> {
                for (int i = 1; i < combinationTiles.size(); i++) {
                    int column = MID + (i);
                    Move move = new Move(combinationTiles.get(i), new Move.Coordinate(MID, column));
                    moves.add(move);
                }
            }
            case 1 -> {
                for (int i = 1; i < combinationTiles.size(); i++) {
                    int row = MID + (i);
                    Move move = new Move(combinationTiles.get(i), new Move.Coordinate(row, MID));
                    moves.add(move);
                }
            }
        }
        return new Turn(moves);
    }

    void setLevelOfDifficulty(LevelOfDifficulty levelOfDifficulty) {
        this.levelOfDifficulty = levelOfDifficulty;
    }

    LevelOfDifficulty getLevelOfDifficulty() {
        return levelOfDifficulty;
    }

    @Override
    public void save(){
        try {
            Connection conn = Database.getInstance().getConnection();
            String sql = """
                         INSERT INTO int_player(player_id, player_name, difficulty)
                                 VALUES (nextval('player_id_seq'),?,?);
                         """;
            PreparedStatement ptsmt = conn.prepareStatement(sql);
            ptsmt.setString(1,getName());
            ptsmt.setString(2,levelOfDifficulty.toString());
            ptsmt.executeUpdate();
            ptsmt.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error while saving to int_player");
        }
    }
}

