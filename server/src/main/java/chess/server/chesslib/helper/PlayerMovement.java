package chess.server.chesslib.helper;

import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;

import static chess.client.sharedCode.helper.Color.White;

/*
 * Represents a movement on the board. A piece of a certain COLOR is moved FROM a square TO a square, and can possibly
 * be a PROMOTION.
 */
public class PlayerMovement {
    public Square from;
    public Square to;
    public Color color;
    public PieceEncoding promotion = null;

    public PlayerMovement(Square from, Square to) {
        this.from = from;
        this.to = to;
    }
    public PlayerMovement(Square from, Square to, PieceEncoding promotion) {
        this(from,to);
        this.promotion = promotion;
    }

    public PlayerMovement() {
    }

    @Override
    public String toString() {
        return "FROM: " + from.toString() + " TO: " + to.toString() + " PROMOTION: " + this.promotion;
    }

    public void setPromotion(PieceEncoding promotion) {
        this.promotion = promotion;
    }


    public String standardPrint() {
        if (promotion == null) {
            return from.toString().toLowerCase() + to.toString().toLowerCase();
        } else {
            return from.toString().toLowerCase() + to.toString().toLowerCase() + promotion.toString().toLowerCase();
        }
    }


    public PlayerMovement(Square from, Square to, Color color) {
        this(from, to);
        this.color = color;
    } //TODO

    public PlayerMovement(Square from, Square to, Color color, PieceEncoding pe) {
        this(from, to, color);
        setPromotion(pe);
    }

    public PlayerMovement(String pm, Color color) {
        fillFromString(pm, color);
        this.color = color;
    }

    private void fillFromString(String pm, Color color) {
        if (pm.length() < 4 || pm.length() > 5) throw new RuntimeException();
        if (pm.length() == 5) {
            char promotionType = pm.charAt(4);
            if (color == White) {
                promotion = PieceEncoding.charToEncoding(Character.toUpperCase(promotionType));
            } else {
                promotion = PieceEncoding.charToEncoding(promotionType);
            }
        }
        from = Square.strToSquare(pm.substring(0, 2));
        to = Square.strToSquare(pm.substring(2, 4));
    }
}
