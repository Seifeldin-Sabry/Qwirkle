package qwirkle.model;

public interface QwirkleEngine {
    void removeAllTurnsThatCanMakeOpponentQwirkle();
    void removeAllTurnsThatContainLessThanScoreFour();
    Turn getMostProfitableTurn();
    void tradeAI();
}
