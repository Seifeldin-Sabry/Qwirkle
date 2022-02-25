package qwirkle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author Seifeldin Ismail
 */
public class Computer extends Player  {
    private Iterator<Tile> tileIterator = getHand().listIterator();
    private Random random = new Random();
    private final LevelOfDifficulty difficultyLevel;


    public Computer(Grid grid, Bag bag, Score score, boolean isPlayingFirst, LevelOfDifficulty difficultyLevel) {
        super("Computer", PlayerColor.BLUE, grid, bag, score, isPlayingFirst);
        this.difficultyLevel = difficultyLevel;
    }

    @Override
    public Move determineMove() {
        Move move = new Move();
        List<Tile> possible = new ArrayList<>(); 
        switch (difficultyLevel){
            case EASY -> {
               
            }
            case MEDIUM ->  {
                int firstOption;
                int secondOption;
                int thirdOption;
                return null;
            }
            //tries to get 3
            case HARD -> {

            }


        }
        return null;
    }

    enum LevelOfDifficulty{
        EASY, MEDIUM, HARD
    }

    @Override
    public String toString() {
        return super.toString() +"Computer{" +
                "tileIterator=" + tileIterator +
                ", random=" + random +
                ", difficultyLevel=" + difficultyLevel +
                '}';
    }
}
