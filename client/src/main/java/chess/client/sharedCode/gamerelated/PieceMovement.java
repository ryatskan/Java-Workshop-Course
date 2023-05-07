package chess.client.sharedCode.gamerelated;
/*
 * Represents a movement of a piece on the board.
 */
public class PieceMovement {
    private final int down;
    private final int right;

    public PieceMovement(int up, int right) {
        this.down = up;
        this.right = right;}
    public PieceMovement(int up, int right, int times) {
        this.down = up;
        this.right = right;
    }
    public int getRight() {
        return right;
    }

    public int getUp() {
        return down;
    }

}
