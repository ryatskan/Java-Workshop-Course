package chess.server.chesslib.helper;

import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;

/*
 * A movement that has its type attached to it.
 * Useful for move analysis, used after checking what kind of move is it.
 */
public class PlayerMovementWithType extends PlayerMovement {
    public MoveType moveType = null;

    public PlayerMovementWithType(Square from, Square to, Color color) {
        super(from, to, color);
    }

    public PlayerMovementWithType(Square from, Square to, Color color, PieceEncoding pe) {
        super(from, to, color, pe);
    }
    public PlayerMovementWithType(PlayerMovement move, MoveType mt) {
        this(move.from, move.to, move.color);
        this.moveType = mt;
    }

}
