package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class GamePiece {
    Color color;
    public Color getColor() {
        return color;
    }

    public abstract PieceEncoding getPieceEncoding();


}
