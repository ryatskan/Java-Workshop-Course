package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

/*
 * Represents the Pieces.Queen piece
 */
public class Queen extends GamePiece {

    public Queen(Color color) {
        this.color = color;
    }
    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.Q;
        } else {
            return PieceEncoding.q;
        }
    }
}
