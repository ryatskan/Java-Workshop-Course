package chess.client.sharedCode.gamerelated;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/*
 * Represents a chess piece, used by the client.
 */
public class ClientPiece implements Serializable {
    private final Square square;
    static HashMap<PieceEncoding, String> fileNames = new HashMap<>();
    static HashMap<PieceEncoding, File> path = new HashMap<>();
    private PieceEncoding piece;

    /*
     * Loads all the images.
     */
    static {
        for (PieceEncoding pe : PieceEncoding.values()) {


            switch (pe) {
                case b -> fileNames.put(pe, "bBishop");

                case B -> fileNames.put(pe, "wBishop");

                case k -> fileNames.put(pe, "bKing");

                case K -> fileNames.put(pe, "wKing");

                case p -> fileNames.put(pe, "bPawn");

                case P -> fileNames.put(pe, "wPawn");

                case r -> fileNames.put(pe, "bRook");

                case n -> fileNames.put(pe, "bKnight");

                case N -> fileNames.put(pe, "wKnight");

                case R -> fileNames.put(pe, "wRook");

                case q -> fileNames.put(pe, "bQueen");

                case Q -> fileNames.put(pe, "wQueen");
            }
        }
        // Handle normal pieces
        for (PieceEncoding pe : PieceEncoding.values()) {
            String regPath = fileNames.get(pe) + "";
            File img = loadIcon(regPath);
            path.put(pe, img);
        }

    }


    public PieceEncoding getPiece() {
        return piece;
    }

    public void setPiece(PieceEncoding piece) {
        this.piece = piece;
    }

    public int getRow() {
        return square.getRow();
    }


    public int getColumn() {
        return square.getColumn();
    }


    public ClientPiece(Square square, PieceEncoding piece) {
        this.square = square;
        this.piece = piece;
    }

    public File getImage() {
        return path.get(piece);
    }

    static File loadIcon(String name) {
        try {
            File file = new File("src/main/resources/icons/" + name + ".png");//BlackBishop.png");
            return file;
        } catch (Exception e) {
            System.out.println("Failed to load image: " + name);
            System.exit(1);
            return null;
        }
    }

    public Square getSquare() {
        return square;
    }

    public static void main(String[] args) {
    }
}
