package chess.server.chesslib.local;

import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.helper.PlayerMovement;

import static chess.server.chesslib.mainlogic.Stockfish.makeStockfishMovement;

public class StockFishPlayer implements Player {

    @Override
    public PlayerMovement move(Board board) {
        String fen = FenCode.boardToFen(board).toString();
        try {
            String move = makeStockfishMovement(fen);
            return new PlayerMovement(move, board.getTurn());
        } catch (Exception e ) {
            System.out.println("ERROR");
            System.exit(1);
        }
        return null;
    }

    @Override
    public void notifyIllegalMovement() {

    }

    @Override
    public void endGame(Color winner) {
        System.out.println("GAME ENDED");
    }

    @Override
    public Color getColor() {
        return null;
    }
}
