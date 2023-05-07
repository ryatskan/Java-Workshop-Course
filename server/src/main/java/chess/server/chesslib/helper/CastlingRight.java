package chess.server.chesslib.helper;

import chess.client.sharedCode.helper.Color;

import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;
import static chess.server.chesslib.helper.MoveType.*;
/*
 * Represents the castling right of a player. A player can either castle on both sides, big castle,
 * small castle or no castle at all.
 */
public enum CastlingRight {
    None,
    BigCastle,
    SmallCastle,
    Both;
    public static CastlingRight remove(CastlingRight cr, CastlingRight remove) {
        if (remove == Both) return None;
        if (remove == None) return cr;
        if (remove == cr) return None;
        if (remove == BigCastle && cr == Both) return SmallCastle;
        if (remove == SmallCastle && cr == Both) return BigCastle;
        // Not equal, either not None. If cr==Both
        if (remove == SmallCastle && cr == BigCastle) return BigCastle;
        if (remove == BigCastle && cr == SmallCastle) return SmallCastle;
        return None;
    }

    public boolean contains(MoveType moveType) {
        if (this == Both) return true;
        if (this == None) return false;
        if (this == SmallCastle)
            return (moveType == WhiteSmallCastling || moveType == BlackSmallCastling);
        return (moveType == WhiteBigCastling || moveType == BlackBigCastling);
    }
    public static String toString(CastlingRight black, CastlingRight white) {
        String output = white.toString(White) + black.toString(Black);
        if (output.equals("")) return "-"; // If no castling rights for both colors.
        return output;
    }
    public String toString(Color color) {
        String output = "";
        switch (this) {
            case BigCastle -> output =  "q";
            case SmallCastle -> output =  "k";
            case Both -> output =  "kq";
        }
        if (color == White) {
            return output.toUpperCase();}
        else return output;
    }
}
