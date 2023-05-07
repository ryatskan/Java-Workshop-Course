package chess.server.communication;

import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.gamerelated.ClientPiece;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.helper.PlayerMovement;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;

/*
 * Class that controls a 2-player game, from the server perpsective.
 */
public class MultGameState implements GameState {
    //   internetGame game = new internetGame();
    Board board;
    String gameName;
    String blackPlayerName;
    String whitePlayerName;

    public MultGameState(String playerName, Color color, String gameName ) {
        this.gameName = gameName;
        if (color == Black) {
            blackPlayerName = playerName;
        } else {
            whitePlayerName = playerName;
        }
        board = FenCode.getInitializedFen().toBoard();
    }


    /*
     * Adds a new player to the game.
     * If isWhite add it as the white player, else add as the black player.
     */
    ConfirmReply addPlayer(String username, boolean isWhite) {
        if (isWhite) {
            if (whitePlayerName == null) {
                whitePlayerName = username;
                return new ConfirmReply(true);
            } else {
                return new ConfirmReply(false, "Failed to add " + username + " as new player.");
            }
        } else {
            if (blackPlayerName == null) {
                blackPlayerName = username;
                return new ConfirmReply(true);
            } else {
                return new ConfirmReply(false, "Failed to add " + username + " as new player.");
            }
        }
    }
    /*
     * If game is not full, add a new player to any available color.
     */
    ConfirmReply addPlayerToAvailableColor(String username) {
        if (whitePlayerName == null) return addPlayer(username, true);
        else if (blackPlayerName == null) return addPlayer(username, false);
        return new ConfirmReply(false, "Game is full!");
    }

    /*
     * Checks if a username can join the game. Right now the only condition is that it is not full.
     */
    ConfirmReply canJoin(String username) {
        if ((username.equals(blackPlayerName) || (username.equals(whitePlayerName)))) {
            return new ConfirmReply(false, "User " + username + " is already in game.");
        }
        if ((blackPlayerName == null || whitePlayerName == null )) {
            return new ConfirmReply(true);// return new ConfirmReply(true, "Added Player to game: " + username);
        }
        return new ConfirmReply(false, "game " + gameName + " is full!");
    }
    /*
     * Returns the color of the given player.
     */
    public Optional<Color> getColor(String player) {
        if (player.equals(blackPlayerName)) return Optional.of(Black);
        if (player.equals(whitePlayerName)) return Optional.of(White);
        return Optional.empty();
    }
    String getPlayer(Color color) {
        return getPlayer(color == White);
    }
    String getPlayer(boolean isWhite) {
        if (isWhite) return whitePlayerName;
        return blackPlayerName;
    }

    public Optional<String> getOpponent(String player) {
        return Optional.of(getPlayer(getColor(player).get().getOpposite()));
    }
    /*
     * Transforms the current board into an object readable for the client.
     */
    public Set<ClientPiece> getPieces() {
        return board.toPositions();
    }
    public boolean isInGame(String username) {
        return (username.equals(getPlayer(false)) || username.equals(getPlayer(true)));
    }
    /*
     * Returns a player in game the ent
     */
    public GameUpdate getGameUpdate() {
        GameUpdate gameUpdate = new GameUpdate();
        gameUpdate.setGameResult(board.getGameStatus());
        gameUpdate.setGameName(gameName);
        gameUpdate.setCurrTurn(getTurn());
        gameUpdate.setWhitePlayerName(whitePlayerName);
        gameUpdate.setBlackPlayerName(blackPlayerName);
        Set<ClientPiece> pieces = getPieces();
        gameUpdate.setPositionList(pieces);
        return gameUpdate;
    }
    /*
     * Outputs the legal possible moves.
     */
    public Set<Square> getLegalMoves(Square from) {
        Set<Square> output = new HashSet<>();
        board.getCurrentLegalMoves().forEach(move -> {
            if (move.from == from) output.add(move.to);
        });
        return output;
    }

    public boolean makeMove(String user, String gameName, PlayerMovement move) {
        return board.makeMove(move);
    }
    @Override
    public boolean isPromotion(String username, Square from, Square to) {
        Color color = getColor(username).get();
        return board.isPromotion(from, to, color);
    }

    @Override
    public GameStatus getCurrentGameStatus() {
        return board.getGameStatus();
    }

    public Color getTurn() {
        return board.getTurn();
    }

    @Override
    public Set<String> getPlayers() {
        Set<String> output =new HashSet<>();
        output.add(getPlayer(White));
        output.add(getPlayer(Black));
        return output;
    }
}
