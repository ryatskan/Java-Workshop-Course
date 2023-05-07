package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents the Pieces.King piece
 */
public class King extends GamePiece {

    public King(Color color)
    {
        this.color = color;
    }
    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.K;
        } else {
            return PieceEncoding.k;
        }
    }
}

