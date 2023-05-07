package chess.server.chesslib.game;

import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.gamerelated.gamepieces.GamePiece;
import chess.client.sharedCode.gamerelated.gamepieces.Pawn;
import chess.server.chesslib.helper.CastlingRight;
import chess.server.chesslib.helper.MoveType;
import chess.server.chesslib.helper.PlayerMovement;

import static chess.client.sharedCode.gamerelated.Square.*;
import static chess.client.sharedCode.helper.Color.White;

/**
 * Contains all the information about game pieces
 * Physically store the GamePieces in the tile array
 * But keep the position of each piece and other data in PieceData
 */
public class PieceStorage {
    public static final int size = 64;
    GamePiece[] squareData = new GamePiece[size];
    Board board;
    BoardData gameData;

    public PieceStorage(BoardData gameData) {
        this.gameData = gameData;
    }

    public void setBoard(Board board) {
        this.board = board;
        gameData.setMap(board);
    }

    /*
     * Transforms a 2d array element (square of a board)
     * To a 1d index of the tileDatabase array.
     */
    public static int toMapIndex(Square sq) {
        return (((sq.numVal() / 10) -1) * 8 + (sq.numVal() % 10) - 1);
    }
    void setPiece(Square square, GamePiece gp) {
        int i = toMapIndex(square);
        if (square == null) throw new RuntimeException();
        squareData[i] = gp;
    }
    GamePiece getPieceOrNull(Square square) {
        //if (square == null) throw new RuntimeException("Square is Null!");
        int i = toMapIndex(square);
        return squareData[i];
    }

    public GamePiece getPieceNotNull(Square square) {
        if (square == null) throw new RuntimeException("Square is Null!");
        return getPieceOrNull(square);
    }

    /*
     * Deletes a piece from the Storage.
     */
    public boolean removePiece(Square square) {
        GamePiece gp = getPieceOrNull(square);
        if (gp != null) {
            squareData[toMapIndex(square)] = null;
            return true;
        } else {
            return false;
        }
    }
    /*
     * Adds a new piece in the given square. If there was a piece in
     * the square before, remove it.
     */
    public void addNewPiece(GamePiece piece, Square square) {
        if (square == null || piece == null) throw new RuntimeException();
        setPiece(square, piece);
    }

    public boolean normalMove(Square from, Square to) {
        if (from == null || to == null) throw new RuntimeException();
        // update in data
        GamePiece gp = getPieceOrNull(from);
        if (gp ==null) return false;
        // HANDLE GAME DATA SIDE
    //    gameData.changeSquareOfPiece(gp.getPieceEncoding(), from, to);
        gameData.updateCastlingRightsFrom(getPieceOrNull(from).getPieceEncoding(),from);
        gameData.updateCastlingRightsTo(getPieceOrNull(to),to);

        //
        // If double move of pawn, update enpassant.
        if (gp instanceof Pawn && Math.abs(to.getRow() - from.getRow()) == 2) {
            gameData.setEnpassant(Square.toSquare((from.getRow() + to.getRow())/2,from.getColumn()));
        } else gameData.setEnpassant(null);

        // If
        //HANDLE PIECESTORAGE SIDE
        setPiece(to, gp);
        setPieceNull(from);
        return true;
    }
    void setPieceNull(Square sq) {
        squareData[toMapIndex(sq)] = null;
    }



    public void doCastle(MoveType castle) {
        switch (castle) {
            case WhiteBigCastling -> {
                normalMove(A1, D1);
                normalMove(E1, C1);
                gameData.setWhiteCastlingRight(CastlingRight.None);
            }
            case WhiteSmallCastling -> {
                normalMove(H1, F1);
                normalMove(E1, G1);
                gameData.setWhiteCastlingRight(CastlingRight.None);

            }
            case BlackSmallCastling -> {
                normalMove(H8, F8);
                normalMove(E8, G8);
                gameData.setBlackCastlingRight(CastlingRight.None);
            }
            case BlackBigCastling -> {
                normalMove(A8, D8);
                normalMove(E8, C8);
                gameData.setBlackCastlingRight(CastlingRight.None);
            }
            default -> throw new RuntimeException();
        }
    }

    public void doEnpasse(PlayerMovement move, MoveType type) {
        Square enpasse = gameData.getEnpasse();
        int row;
        normalMove(move.from, move.to);
        if (White  == move.color) {row =4;} else row = 3;
        if (!removePiece(Square.toSquare(row, enpasse.getColumn()))) throw new RuntimeException();
    }

    public void doPromotion(PlayerMovement move) {
        normalMove(move.from, move.to);
        try {
            setPiece(move.to, move.promotion.toNewPiece(move.color));
        } catch (Exception e) {
            System.out.println();
            throw new RuntimeException();
        }
    }
}
