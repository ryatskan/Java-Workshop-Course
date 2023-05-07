package chess.server.chesslib.local;

import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.results.GameResult;

import java.util.LinkedList;
import java.util.List;
/*
 * A game run based on given moves for each player - essentially pre-defined game.
 * Useful for checking correctness of the project.
 */
public class PreRunGame extends LocalGame {
    int totalMoves;
    public PreRunGame(List<String> moves) {
        super(true);
        totalMoves = moves.size();
        List<String> white = new LinkedList<>(), black = new LinkedList<>();
        for(int i=0;i<moves.size();i++) {
            if (i%2==0) {white.add(moves.get(i));}
            else { black.add(moves.get(i));
            }
        }
        this.white = new PreRunPlayer(white, Color.White);
        this.black = new PreRunPlayer(black,Color.Black);
    }

    public GameResult startGame() {
        return newGame();
    }
}
