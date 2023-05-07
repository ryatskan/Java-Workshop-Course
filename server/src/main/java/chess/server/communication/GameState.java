package chess.server.communication;

import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.helper.PlayerMovement;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameState {
    boolean isPromotion(String username, Square from, Square to);
    Set<String> getPlayers();

    boolean isInGame(String player);

    GameUpdate getGameUpdate();

    Color getTurn();

    Optional<Color> getColor(String username);

    Set<Square> getLegalMoves(Square from);

    boolean makeMove(String user, String gameName, PlayerMovement playerMovement);

    GameStatus getCurrentGameStatus();
}
