package qwirkle.model.computer;

import qwirkle.model.Move;
import qwirkle.model.Turn;

import java.util.HashMap;
import java.util.Set;

public interface QwirkleEngineAI {
    HashMap<Move,Set<Turn>> removeAllTurnsThatCanMakeOpponentQwirkle(HashMap<Move, Set<Turn>> allMoves);
    HashMap<Move,Set<Turn>> removeAllTurnsThatContainLessThanScoreSix(HashMap<Move, Set<Turn>> allMoves);
    HashMap<Move,Set<Turn>> getTurnsThatHaveMultipleRowsOrColumns(HashMap<Move, Set<Turn>> allMoves);
    Turn getMostProfitableTurn(HashMap<Move, Set<Turn>> allMoves);
}