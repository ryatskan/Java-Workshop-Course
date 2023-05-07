package chess.server.chesslib.local;

import chess.server.chesslib.results.GameResult;

public class StockFishGame extends LocalGame{
    int totalMoves;
    public StockFishGame() {
        super(true);
        this.white = new StockFishPlayer();
        this.black = new StockFishPlayer();
        print = true;
    }

    public GameResult startGame() {
        GameResult gameResult = newGame();
        System.out.println(gameResult.getGameResult());
        ;
        return gameResult;
    }

    public static void main(String[] args) {
        new StockFishGame().startGame();
    }
}
