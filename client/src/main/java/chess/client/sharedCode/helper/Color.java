package chess.client.sharedCode.helper;

public enum Color {
    White, Black;
    public Color getOpposite() {
        if (this == White) return Black;
        return White;
    }
    public Character toChar() {
        if (this == White) return 'w';
        return 'b';
    }
}