package chess.server.chesslib.local;

import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.helper.PlayerMovement;

import java.util.List;
/*
 * The player for PreRunGame
 */
public class PreRunPlayer implements Player {
    Color color;
    List<String> moves;

    public PreRunPlayer(List<String> moves, Color gColor) {
        color = gColor;
        this.moves=moves;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public PlayerMovement move(Board board) {
        if (moves.isEmpty()) return null;
        String move =  moves.get(0);
        moves.remove(0);
        return new PlayerMovement(move, color);
    }

    @Override
    public void notifyIllegalMovement() {

    }

    @Override
    public void endGame(Color winner) {
        System.out.println(winner);
    }
}