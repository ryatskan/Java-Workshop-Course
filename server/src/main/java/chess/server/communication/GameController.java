package chess.server.communication;

import chess.client.sharedCode.*;
import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.gamerelated.*;
import chess.client.sharedCode.helper.Color;
import chess.client.sharedCode.helper.TableEntry;
import chess.server.chesslib.helper.PlayerMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;

import java.util.*;

/*
 * Controls all the games - multiplayer and singleplayer.
 */
public class GameController {
    // two central objects of the class
    HashMap<String, MultGameState> multGameState = new HashMap<>();
    HashMap<String, SingleGameState> singleGameState = new HashMap<>();


    public Optional<GameStatus> makeMove(String user, String gameName, Square from, Square to, PieceEncoding piece) {
        if (!gameExist(gameName)) {
            return Optional.empty();
        }
        GameState gState = getGameState(gameName);
        Optional<Color> color = gState.getColor(user);
        if (color.isEmpty()) return Optional.empty();

        if (gState.makeMove(user, gameName, new PlayerMovement(from,to,color.get(), piece))) {
            GameStatus gameResult = gState.getCurrentGameStatus();
            return Optional.of(gameResult);//return gState.makeMove(move,user );
        } else         return Optional.empty();
    }


    public boolean gameExist(String gameName) {
        boolean isInMult = multGameState.containsKey(gameName);
        boolean isInSingle = singleGameState.containsKey(gameName);
        if (isInSingle && isInMult) throw new RuntimeException("user is in two games!");
        return isInSingle || isInMult;
    }
    public String getGameOrNull(String username) {
        for (Map.Entry<String, MultGameState> x : multGameState.entrySet()) {
            if (x.getValue().isInGame(username)) return x.getKey();
        }
        return null;
    }

    public boolean createGame(String username, String game, Color color, boolean singlePlayer) {
        if (!singlePlayer) {
            this.multGameState.put(game, new MultGameState(username, color, game));
        } else {
            // A singleplayer game doesnt need to keep a game name
            this.singleGameState.put(game, new SingleGameState(username, color, game));
        }
        return true;
    }

    public ConfirmReply joinMultGame(String username, String gameName) {
        try {
            return multGameState.get(gameName).addPlayerToAvailableColor(username);
        } catch (Exception e) {
            return new ConfirmReply(false, e.getMessage());
        }
    }

    public ConfirmReply canJoinMultGame(String username, String gameName) {
        if (!multGameState.containsKey(gameName)) {
            return new ConfirmReply(true, "Tried to join a game that does not exist!");
        }
        ConfirmReply reply = multGameState.get(gameName).canJoin(username);
        if (reply.OK) {
            return new ConfirmReply(true, "Can not join game:" + gameName + "." + "reason: " + reply.message);

        } else {
            return new ConfirmReply(true, "");
        }
    }

    /*
     * Returns all the multiplayer active games, to be used by clients.
     */
    public List<TableEntry> getAllMultGames() {
        List<TableEntry> output = new LinkedList<>();
        multGameState.forEach((key, state) ->
                output.add(new TableEntry(state.gameName, state.whitePlayerName, state.blackPlayerName)));
        return output;
    }
    public List<String> getAllGameNames() {
        List<String> output = new LinkedList<>();
        multGameState.forEach((key, value) -> output.add(key));
        singleGameState.forEach((key, value) -> output.add(key));
        return output;
    }

    /*
     * Gets the opponent of the given player
     */
    ConfirmReply getOpponentInMult(String player, String game) {
        try {
            String output = multGameState.get(game).getOpponent(player).get();
            return new ConfirmReply(true, output);
        } catch (Exception e) {
            return new ConfirmReply(false, "Failed to bring");
        }
    }

    Set<String> getPlayers(String gameName) {
        return getGameState(gameName).getPlayers();
    }

    /*
     * Ends a game. winnerPlayer is the winner.
     */
    public void removeGame(String gameName) {
        System.out.println("Game " + gameName + " has ended.");
        if (isMultiPlayer(gameName)) {
            multGameState.remove(gameName);
        } else {
            singleGameState.remove(gameName);
        }
    }
    public boolean isInGame(String player, String gameName) {
        return getGameState(gameName).isInGame(player);
    }

    public GameUpdate getGameUpdate(String gameName) {
        return getGameState(gameName).getGameUpdate();
    }

    public Set<Square> getLegalMoves(String username, String gameName, Square from) {
        GameState gameState = getGameState(gameName);
        if (gameState.getTurn() == gameState.getColor(username).get()) {
            return gameState.getLegalMoves(from);
        } else {
            throw new RuntimeException("Not your turn!");
        }
    }

    GameState getGameState(String gameName) {
        GameState mult = multGameState.get(gameName);
        GameState single = singleGameState.get(gameName);
        if (mult == null) {
            if (single != null) return single;
        } else {
            if (single == null) return mult;
        }
        throw new RuntimeException("getGameState error");
    }
    public boolean isSinglePlayer(String gameName) {
        return getGameState(gameName) instanceof SingleGameState;
    }
    public boolean isMultiPlayer(String gameName) {
        return !isSinglePlayer(gameName);
    }

    public boolean isPromotion(String user, String gameName, Square from, Square to) {
        if (!gameExist(gameName)) {
            throw new RuntimeException("GAME DOESNT EXIST");//new ConfirmReply(false, "Game does not exist");
        }
        GameState gState = getGameState(gameName);
        return gState.isPromotion(user, from, to);
    }
}
