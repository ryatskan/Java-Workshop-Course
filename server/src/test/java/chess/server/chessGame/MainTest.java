package chess.server.chessGame;

import chess.server.chesslib.results.GameResult;
import chess.server.chesslib.results.ReferenceGameResult;
import chess.server.chesslib.results.AbstractResult;
import chess.server.chesslib.local.PreRunGame;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import com.github.bhlangonijr.chesslib.pgn.PgnIterator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MainTest {
    List<Game> load() throws Exception {
        List<Game> list = new LinkedList<>();
        PgnHolder pgn = new PgnHolder("src/main/resources/Adams.pgn");
        pgn.setLazyLoad(true);
        pgn.loadPgn();
        List<Game> games = pgn.getGames();
        list.addAll(games);

        return list;
    }

    @Test
    public void test() throws Exception {
        List<Game> list = load();
        for (int i = 1401; i < list.size(); i++) {
            boolean success = testGame(list.get(i));
            if (success) System.out.println("Compared game " + i + " successfully");
            Assert.assertTrue(success);
        }

    }
    // Test without loading the entire pgn, causing heap exception for big files.
    @Test
    public void testBigFile() throws Exception {
        try (PgnIterator pgn = new PgnIterator("src/main/resources/Adams.pgn")) {
            int i = 0;
            Iterator var3 = pgn.iterator();

            while (var3.hasNext()) {
                Game game = (Game) var3.next();
                boolean success = testGame(game);
                Assert.assertTrue(success);
                System.out.println("Compared game " + i + " successfully");
                i++;
            }
        }

    }

    private boolean testGame(Game e) {
        ReferenceGameResult rGame = ReferenceGameResult.ReferenceGameResult(e);
        GameResult actualGame = new PreRunGame(rGame.getMoves()).startGame();

        return AbstractResult.compare(rGame, actualGame);
    }
}