package qwirkle.model.computer;

import qwirkle.model.Move;
import qwirkle.model.Turn;

import java.util.HashMap;
import java.util.Set;
/**
 * @author: Seifeldin Sabry
 */
public interface QwirkleEngineAI {
    HashMap<Move,Set<Turn>> removeAllTurnsThatCanMakeOpponentQwirkle(HashMap<Move, Set<Turn>> allMoves);
    HashMap<Move,Set<Turn>> removeAllTurnsThatContainLessThanScoreFive(HashMap<Move, Set<Turn>> allMoves);
    HashMap<Move,Set<Turn>> getTurnsThatHaveMultipleRowsOrColumns(HashMap<Move, Set<Turn>> allMoves);
    Turn getMostProfitableTurn(HashMap<Move, Set<Turn>> allMoves);
}

