package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

/*
 * Represents the Pieces.Rook piece
 */
public class Rook extends GamePiece {
    public Rook(Color color) {
        this.color = color;
    }
    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.R;
        } else {
            return PieceEncoding.r;
        }
    }
}
