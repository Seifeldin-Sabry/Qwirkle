package qwirkle.model;

import java.util.*;

import static qwirkle.model.Board.MID;

public class Computer extends Player {
    enum LevelOfDifficulty{
        EASY, MEDIUM, HARD
    }

    private LevelOfDifficulty levelOfDifficulty;

    public Computer(Bag bag, Board grid,LevelOfDifficulty levelOfDifficulty) {
        super("Computer", bag, grid);
        this.levelOfDifficulty = levelOfDifficulty;
    }


    public void makeMove() {
        Set<Move> allMovesPossible = getBoard().getUsedSpaces();
        List<Move> movesToMake = new ArrayList<Move>();
        switch (levelOfDifficulty){
            case EASY -> {
                List<Move> validMoves = new ArrayList<>();

                    if (allMovesPossible.isEmpty()){
                        Random randomTileChooser = new Random();
                        int randomTileInHandIndex = randomTileChooser.nextInt(getHand().size());
                        Move moveMade = new Move(getHand().get(randomTileInHandIndex),new Move.Coordinate(MID,MID));
                        makeMove(moveMade);
                    }

            }
        }

    }



    public Set<Move> getAllPossibleMoves(){
        Set<Move> allMoves = new HashSet<>();
        Set<Move> allPlayedMoves = getBoard().getUsedSpaces();
        if (allPlayedMoves.isEmpty())return null; //indicates that its the first move
        for (Move playedMoves: allPlayedMoves) {

        }
        return null;
    }
}

