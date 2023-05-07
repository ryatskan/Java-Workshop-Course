package chess.server.chesslib.helper;

import chess.client.sharedCode.gamerelated.Square;

import static chess.client.sharedCode.gamerelated.Square.*;

/*
 * Represents a type of move in the game.
 */
public enum MoveType {
    Normal,
    Enpasse,
    WhiteBigCastling,
    WhiteSmallCastling,
    BlackSmallCastling,
    BlackBigCastling,
    PawnPromotion;

    public Square[] castlingToPassingSquares() {
        return switch (this) {

            case WhiteBigCastling -> new Square[]{B1, C1, D1};
            case WhiteSmallCastling -> new Square[]{F1, G1};
            case BlackSmallCastling -> new Square[]{F8, G8};
            case BlackBigCastling -> new Square[]{B8, C8, D8};
            default -> throw new RuntimeException();
        };
    }

    public Square[] castlingToKingPassingSquares() {
        return switch (this) {

            case WhiteBigCastling -> new Square[]{C1, D1};
            case WhiteSmallCastling -> new Square[]{F1, G1};
            case BlackSmallCastling -> new Square[]{F8, G8};
            case BlackBigCastling -> new Square[]{C8, D8};
            default -> throw new RuntimeException();
        };
    }

    public CastlingRight toCastlingRight() {
        switch (this) {
            case WhiteBigCastling, BlackBigCastling -> {
                return CastlingRight.BigCastle;
            }
            case WhiteSmallCastling, BlackSmallCastling -> {
                return CastlingRight.SmallCastle;
            }
            default -> throw new RuntimeException();
        }
    }


}
