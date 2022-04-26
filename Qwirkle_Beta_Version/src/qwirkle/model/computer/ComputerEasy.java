package qwirkle.model.computer;

import qwirkle.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static qwirkle.model.Grid.MID;
import static qwirkle.model.computer.Computer.LevelOfDifficulty.*;

public class ComputerEasy extends Computer {


    public ComputerEasy(Bag bag, Grid grid) {
        super(bag, grid, EASY);
    }


    /**
     * @return the best turn respective of the level of difficulty, null if no turn can be made
     */
    @Override
    public Turn makeTurn() {
        boolean firstTurn = getBoard().getUsedSpaces().isEmpty();
        Turn turn = null;
        if (firstTurn) {
            turn = playFirstTurn();
            if (turn == null) {
                trade();
                return null;
            }
        }
//        turn = getRandomMove(getMoveValidator().getAllValidMoves(getBoard()));
        if (turn == null) {
            trade();
            return null;
        }
        return turn;
    }

    private Turn playFirstTurn() {
        HashMap<Move, Set<Turn>> allmoves = getMoveValidator().getAllValidMoves(getBoard());
        if (allmoves.isEmpty() && getBoard().isNotEmpty(MID, MID)) {
            trade();
            return null;
        }
        Set<Set<Tile>> tileCombos = getMoveValidator().getLargestCombinations();
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
    @Override
    public void trade() {
        tradeRandomNumTiles();
    }
}
