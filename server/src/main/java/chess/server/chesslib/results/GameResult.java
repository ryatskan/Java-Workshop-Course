package chess.server.chesslib.results;

import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.helper.PlayerMovement;

import java.util.List;
/*
 * A Result object for this chess library.
 */
public class GameResult extends AbstractResult {
    public String lastMove;

    public GameResult(Board board, List<String> moves, PlayerMovement lastMove, boolean fullyEnded) {
        this.moves = moves;
        this.lastPosition = FenCode.boardToFen(board).toString();
        this.lastMove = lastMove.toString();
        fenCodeList = board.getFenList();
        this.fullyEnded = fullyEnded;
        this.pseudoLegalMoves = board.getPseudoLegalMovesList();
        this.legalMoves = board.getLegalMovesStringList();
        this.gameResult = board.getGameStatus();
    }
}
