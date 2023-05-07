package chess.server.chesslib.local;


import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.helper.PlayerMovement;

public interface Player {

    PlayerMovement move(Board board);

    void notifyIllegalMovement();

    void endGame(Color winner);
    Color getColor();
}
