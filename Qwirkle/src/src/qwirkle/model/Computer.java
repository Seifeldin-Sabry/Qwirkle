package src.qwirkle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static qwirkle.model.Grid.MID;

/**
 * @author Seifeldin Ismail
 */

public class Computer extends Player {
    public enum LevelOfDifficulty {
        EASY, AI
    }

    private int points;

    private LevelOfDifficulty levelOfDifficulty;

    public Computer(Bag bag, Grid grid, LevelOfDifficulty levelOfDifficulty) {
        super("Computer", bag, grid);
        this.levelOfDifficulty = levelOfDifficulty;
        points = 0;
    }


    public void makeMove() {
        Set<Move> allMovesPossible = getBoard().getUsedSpaces();
        List<Move> movesToMake = new ArrayList<Move>();
        switch (levelOfDifficulty) {
            case EASY -> {
                List<Move> validMoves = new ArrayList<>();
                if (allMovesPossible.isEmpty()) {
                    Random randomTileChooser = new Random();
                    int randomTileInHandIndex = randomTileChooser.nextInt(getDeck().getTilesInDeck().size());
                    Move moveMade = new Move(getDeck().getTilesInDeck().get(randomTileInHandIndex), new Move.Coordinate(MID, MID));
                    makeMove(moveMade);
                }
            }
        }
    }
    public int getPoints(){
        return points;
    }


}

