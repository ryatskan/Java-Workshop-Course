package chess.server.communication;

import chess.client.sharedCode.gamerelated.ClientPiece;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.helper.PlayerMovement;
import chess.server.chesslib.mainlogic.Stockfish;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;

public class SingleGameState implements GameState{
    Board board;
    final String playerName;
    final String gameName;
    final Color playerColor;

    public SingleGameState(String playerName, Color color, String gameName ) {
        this.playerName = playerName;
        this.gameName = gameName;
        this.playerColor = color;
        board = FenCode.getInitializedFen().toBoard();
        if (color == Black) {
            makeStockfishMove();
        }
    }

    /*
     * Transforms the current board into an object readable for the client.
     */
    public Set<ClientPiece> getPieces() {
        return board.toPositions();
    }
    public boolean isInGame(String username) {
        return (username.equals(playerName));
    }
    /*
     * Returns a player in game the ent
     */
    public GameUpdate getGameUpdate() {
        GameUpdate gameUpdate = new GameUpdate();
        gameUpdate.setGameResult(board.getGameStatus());
        gameUpdate.setGameName(gameName);
        gameUpdate.setCurrTurn(getTurn());
        if (playerColor== White)  {
            gameUpdate.setWhitePlayerName(playerName);
            gameUpdate.setBlackPlayerName("Stockfish");
        } else {
            gameUpdate.setBlackPlayerName(playerName);
            gameUpdate.setWhitePlayerName("Stockfish");
        }
        Set<ClientPiece> pieces = getPieces();
        gameUpdate.setPositionList(pieces);
        return gameUpdate;
    }
    public Color getTurn() {
        return board.getTurn();
    }

    @Override
    public Optional<Color> getColor(String username) {
        if (!username.equals(this.playerName)) return Optional.empty();
        return Optional.of(playerColor);
    }

    @Override
    public Set<Square> getLegalMoves(Square from) {
        Set<Square> output = new HashSet<>();
        board.getCurrentLegalMoves().forEach(move -> {
            if (move.from == from) output.add(move.to);
        });
        return output;
    }

    public boolean makeMove(String user, String gameName, PlayerMovement move) {
        board.makeMove(move);
        return makeStockfishMove();
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

    private boolean makeStockfishMove() {
        try {
            String stockfishMove=  Stockfish.makeStockfishMovement(FenCode.boardToFen(board).toString());
            board.makeMove(new PlayerMovement(stockfishMove, board.getTurn()));
            return true;
        } catch (Exception e) {
            System.out.println("FAILED TO MAKE A MOVE");
            return false;
        }

    }

    @Override
    public Set<String> getPlayers() {
        Set<String> output= new HashSet<>();
        output.add(playerName);
        return output;
    }
}
