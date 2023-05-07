package chess.client.sharedCode.gamerelated.gamepieces;

import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents the Pieces.Pawn piece
 */
public class Pawn extends GamePiece {

    public ArrayList<PieceMovement> eatingMoves = new ArrayList<>();
    public ArrayList<PieceMovement> normalMoves = new ArrayList<>();

    public Pawn(Color color)
    {
        this.color = color;
        if (color == Color.White) {
            normalMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(1,0));
            eatingMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(1, -1));
            eatingMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(1, 1));
        } else {
            normalMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(-1,0)) ;
            eatingMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(-1, -1));
            eatingMoves.add(new chess.client.sharedCode.gamerelated.PieceMovement(-1, 1));

        }
    }

    public PieceEncoding getPieceEncoding() {
        if (color == Color.White) {
            return PieceEncoding.P;
        } else {
            return PieceEncoding.p;
        }
    }

    public List<PieceMovement> getEatingMoves() {
        return eatingMoves;
    }
}
