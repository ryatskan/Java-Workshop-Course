package chess.client.sharedCode.gamerelated;

import chess.client.sharedCode.gamerelated.gamepieces.*;
import chess.client.sharedCode.helper.Color;
public enum PieceEncoding {
    b(0), B(1), k(2), K(3), p(4), P(5), r(6),
    n(7), N(8), R(9), q(10), Q(11),
    ;
    int i;
    PieceEncoding(int i) {
        this.i = i;
    }
    public int value() {
        return i;
    }

    public static PieceEncoding charToEncoding(char c) {
        switch (c) {
            case 'b':
                return b;
            case 'B':
                return B;
            case 'k':
                return k;
            case 'K':
                return K;
            case 'p':
                return p;
            case 'P':
                return P;
            case 'r':
                return r;
            case 'n':
                return n;
            case 'N':
                return N;
            case 'R':
                return R;
            case 'q':
                return q;
            case 'Q':
                return Q;
            default:
                return null;
        }
    }
    public static PieceEncoding charToEncoding(char ch, Color color) {
        assert charToEncoding(ch) != null;
        return charToEncoding(ch).toNewPiece(color).getPieceEncoding();
    }

    public GamePiece toNewPiece() {
        return switch (this) {
            case b -> new Bishop(Color.Black);
            case B -> new Bishop(Color.White);
            case k -> new King(Color.Black);
            case K -> new King(Color.White);
            case n -> new Knight(Color.Black);
            case N -> new Knight(Color.White);
            case p -> new Pawn(Color.Black);
            case P -> new Pawn(Color.White);
            case r -> new Rook(Color.Black);
            case R -> new Rook(Color.White);
            case q -> new Queen(Color.Black);
            case Q -> new Queen(Color.White);
        };
    }
    public GamePiece toNewPiece(Color color) {
        return switch (this) {
            case b,B -> new Bishop(color);
            case k,K -> new King(color);
            case n,N -> new Knight(color);
            case p,P -> new Pawn(color);
            case r,R -> new Rook(color);
            case q,Q -> new Queen(color);
        };
    }
    public Color getColor() {
        return switch (this) {
            case b, k,n,p,r,q  -> Color.Black;
            case B, K, N, P,R,Q -> Color.White;
        };
    }
}
