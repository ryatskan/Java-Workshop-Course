package chess.server.chesslib.results;

import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.server.chesslib.game.FenCode;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.game.Game;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/*
 * A Result for external chess library, used to check correctness of this project
 */
public class ReferenceGameResult extends AbstractResult {

    public ReferenceGameResult(List<FenCode> fenList, List<String> moves, List<Set<String>> pseudoLegal, List<Set<String>> legal, boolean b, GameStatus result, String lastBoard) {
        super(fenList, moves, pseudoLegal, legal, b, result,lastBoard);
    }

    public static ReferenceGameResult ReferenceGameResult(Game g) {
        Board board = new Board();
        List<FenCode> fenList = new LinkedList<>();
        List<Set<String>> legalMovesSetList = new LinkedList<>();
        List<Set<String>> pseudoLegalMovesSetList = new LinkedList<>();

        List<String> moves = new LinkedList<>();
        g.getCurrentMoveList().forEach(e -> moves.add(e.toString()));
        // fenList.add(new FenCode(board.getFen()));
        for (int i = 0; i < g.getCurrentMoveList().size(); i++) {
            board.doMove(g.getCurrentMoveList().get(i));

            Set<String> pseudoLegalMoves = new HashSet<>();
            Set<String> legalMoves = new HashSet<>();

            board.pseudoLegalMoves().forEach(move -> pseudoLegalMoves.add(move.toString()));
            board.legalMoves().forEach(move -> legalMoves.add(move.toString()));

            pseudoLegalMovesSetList.add(pseudoLegalMoves);
            legalMovesSetList.add(legalMoves);
            fenList.add(new FenCode(board.getFen()));
        }
        String lastBoard = board.getFen();
        return new ReferenceGameResult(fenList, moves, pseudoLegalMovesSetList,
                legalMovesSetList, true, toProjectGameResult(board), lastBoard);
    }
    static GameStatus toProjectGameResult(Board r) {
        if (r.isStaleMate()) return GameStatus.Draw;
        if (r.isMated()) {
            if (r.getSideToMove() == Side.WHITE) return GameStatus.BlackWon;
            return GameStatus.WhiteWon;
        }
        return GameStatus.Ongoing;
    }
}
