package chess.client.sharedCode.gamerelated.gamepieces;


import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

/*
 * Represents the Pieces.Bishop piece
 */
public class Bishop extends GamePiece {
    public Bishop(Color color) {
        this.color = color;
    }
    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.B;
        } else {
            return PieceEncoding.b;
        }
    }
}
