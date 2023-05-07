package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

/*
 * Represents the Pieces.Knight piece
 */
public class Knight extends GamePiece {

    public Knight(Color color) {
        this.color = color;
    }
    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.N;
        } else {
            return PieceEncoding.n;
        }
    }
}
