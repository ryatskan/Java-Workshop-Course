package chess.server.chesslib.local;

import chess.server.chesslib.game.Board;
import chess.server.chesslib.helper.PlayerMovement;
import chess.server.chesslib.mainlogic.Logic;
import chess.server.chesslib.results.GameResult;

import java.util.LinkedList;
import java.util.List;


public abstract class LocalGame {
    public Board board = Board.getInitializedBoard();
    Player white, black;
    private final boolean endWhenIncorrect;
    protected boolean print = false;

    LocalGame(boolean endWhenIncorrect) {
        this.endWhenIncorrect = endWhenIncorrect;
    }

    protected GameResult newGame() {
        List<String> moves = new LinkedList<>();
        Player currPlayer = white;
        PlayerMovement lastMove = null;
        PlayerMovement movement;

        do {
            movement = currPlayer.move(board);
            if (movement == null) {
                return new GameResult(board, moves, lastMove, true);
            } else {
                lastMove = movement;
            }
            boolean succeeded = board.makeMove(movement);
            if (succeeded) { //If move was legal and successfully made
                if (print) {
                    System.out.println(board.toString());
                    System.out.println();
                }
                moves.add(lastMove.standardPrint());
                if (currPlayer == white) {
                    currPlayer = black;
                } else {
                    currPlayer = white;
                }
            } else {

                if (!endWhenIncorrect) {
                    System.out.println("Illegal move!");
                } else {
                    return new GameResult(board, moves, lastMove, false);
                }
            }
        } while (Logic.getGameStatus(board) == chess.client.sharedCode.gamerelated.GameStatus.Ongoing);//
        System.out.println("GAME WON!");
        return new GameResult(board, moves, lastMove, true);
    }




}
    /*
     * Updates board properties, like Castling, enPassant..

    protected void updateBoardProperties(Board board, Board.PlayerMovement move) {
        if (move.from)
            // CASTLING RIGHTS
            CastlingRight cr = Logic.newCastlingRight(board, move);
        board.setCastlingRights(cr, move.color);

    }
*/

