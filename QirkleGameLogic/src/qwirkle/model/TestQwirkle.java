package qwirkle.model;

import java.util.ArrayList;
import java.util.List;

import static qwirkle.model.Board.MID;

public class TestQwirkle {


    public static void main(String[] args) {
       Board board = new Board();
       Tile tileRs = new Tile(TileColor.RED, TileShape.SQUARE);
        Tile tileRs2 = new Tile(TileColor.RED, TileShape.SQUARE);
        Tile tileRd = new Tile(TileColor.RED, TileShape.DIAMOND);
        Tile tileRc = new Tile(TileColor.RED, TileShape.CIRCLE);
        Tile tileBc = new Tile(TileColor.BLUE, TileShape.CIRCLE);
        Tile tileBs = new Tile(TileColor.BLUE, TileShape.SQUARE);
        Tile tilePd = new Tile(TileColor.PURPLE, TileShape.DIAMOND);
        Tile tilePs = new Tile(TileColor.PURPLE,TileShape.SQUARE);
        Tile tilePc = new Tile(TileColor.PURPLE,TileShape.CIRCLE);
        Tile tilePe = new Tile(TileColor.PURPLE,TileShape.EIGHT_POINT_STAR);
        Move.Coordinate mid = new Move.Coordinate(MID,MID); //defining middle for first move

        //for testing AddMove() which checks if its a valid move first then adds
        //Placing a purple diamond in the centre
        List<Move> movesMade = new ArrayList<>();
        Move move1 = new Move(tilePd,mid); //Purple diamond
        if (board.validMove(move1,movesMade)){ //should print true
            System.out.println("true");
            movesMade.add(move1);
            board.boardAddMove(move1); //making the move
        }else System.out.println("false");


        //Placing in the middle again - should return false
        Move move2 = new Move(tileBc,mid);
        movesMade.add(move2);
        System.out.println(board.validMove(move2,movesMade));


        //placing a purple square to the left of the center - should return true
        Move move3 = new Move(tilePs,new Move.Coordinate(9,10));
        movesMade.add(move3);
        if (board.validMove(move3,movesMade)){
            System.out.println("true");
            board.boardAddMove(move3); //making the move if true
        }else System.out.println("false");

        //placing a purple square to the right of the center - should return false
        //because there is already a purple square in the line
        Move move4 = new Move(tilePs,new Move.Coordinate(11,10));
        movesMade.add(move4);
        if (board.validMove(move4,movesMade)){
            System.out.println("true");
            board.boardAddMove(move4); //making the move if true
        }else System.out.println("false");

        //placing a tile on its own - one square gap - should return false
        Move move5 = new Move(tilePe,new Move.Coordinate(12,10));
        movesMade.add(move5);
        if (board.validMove(move5,movesMade)){
            System.out.println("true");
            board.boardAddMove(move5); //making the move if true
        }else System.out.println("false");

        //placing a purple eight point star on the right of the centre - should print true
        Move move6 = new Move(tilePe,new Move.Coordinate(11,10));
        movesMade.add(move6);
        if (board.validMove(move6,movesMade)){ //should print true
            System.out.println("true");
            board.boardAddMove(move6); //making the move if true
        }else System.out.println("false");

        Move move7 = new Move(tilePc,new Move.Coordinate(8,10));
        movesMade.add(move7);
        if (board.validMove(move7,movesMade)){ //should print true
            System.out.println("true");
            board.boardAddMove(move7); //making the move if true
        }else System.out.println("false");

        Move move8 = new Move(tilePs,new Move.Coordinate(7,10));
        movesMade.add(move8);
        if (board.validMove(move8,movesMade)){ //should print true
            System.out.println("true");
            board.boardAddMove(move8); //making the move if true
        }else System.out.println("false");
//        Computer computer = new Computer(new Bag(),board, Computer.LevelOfDifficulty.EASY);
//        computer.fillHand();
//        computer.makeMove();
//        for (int i = 0; i < board.getRowCount(); i++) {
//            for (int j = 0; j < board.getColumnCount(); j++) {
//                if (board.isNotEmpty(i,j)){
//                    Move move = new Move(board.getTile(i,j),new Move.Coordinate(i,j));
//                    System.out.println(move);
//                }
//            }
//        }
//        System.out.println(computer.getHand());



    }

}
