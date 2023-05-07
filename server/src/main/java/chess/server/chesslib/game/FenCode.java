package chess.server.chesslib.game;

import chess.client.sharedCode.gamerelated.PieceEncoding;

import static chess.server.chesslib.mainlogic.Logic.columnSize;
import static chess.server.chesslib.mainlogic.Logic.rowSize;
import static chess.server.chesslib.helper.CastlingRight.*;
import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;

import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.helper.CastlingRight;
import org.testng.Assert;
import org.testng.annotations.Test;
/*
 * FenCode is a way to store a chess game position. It is essentially a string - the toString()
 * output of this class.
 */
public class FenCode {
    public static String initializedFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private String pieceState = null;
    private Color turn;
    private CastlingRight wCastlingRight;
    private CastlingRight bCastlingRight;

    private Square enPassant = null;

    public FenCode(String fen) {
        String[] fenSplit = fen.split(" ");
        pieceState = fenSplit[0];

        // TURN
        if (fenSplit[1].equals("w")) {
            turn = White;
        } else {
            turn = Black;
        }
        // CASTLING RIGHTS
        String castlingRightsInput = fenSplit[2];
        wCastlingRight = handleCastlingRights(castlingRightsInput, "K", "Q");
        bCastlingRight = handleCastlingRights(castlingRightsInput, "k", "q");


        // ENPASSANT
        String enPassantInput = fenSplit[3];
        if (enPassantInput.equals("-")) {
            enPassant = null;
        } else {
            enPassant = Square.strToSquare(enPassantInput.toUpperCase());
            if (enPassant == null) throw new RuntimeException("incorrect fen");
        }

        // HALF & FULL MOVES, NO SUPPORT FOR THESE IN THE CURRENT VERSION
        try {
       //     halfmoveClock = Integer.parseInt(fenSplit[4]);
       //     fullmoveClock = Integer.parseInt(fenSplit[5]);
        } catch (Exception e) {
            System.out.println(fenSplit);
        }
    }
    CastlingRight handleCastlingRights(String castlingRights, String kChar, String qChar) {
        CastlingRight cr;
        if (castlingRights.contains(kChar)) {
            if (castlingRights.contains(qChar)) {
                cr = Both;
            } else {
                cr = SmallCastle;
            }
        } else {
            if (castlingRights.contains(qChar)) {
                cr = BigCastle;
            } else {
                cr = None;
            }
        }
        return cr;
    }

    FenCode() {
    }
    /*
     * Outputs a board corresponding to this fen code
     */
    public Board toBoard() {
        BoardData bd = new BoardData();
        PieceStorage ps = new PieceStorage(bd);
        handlePieces(ps);
        bd.setTurn(turn);
        bd.setBlackCastlingRight(wCastlingRight);
        bd.setWhiteCastlingRight(bCastlingRight);
        bd.setEnpassant(enPassant);
        return new Board(ps,bd);
    }

    /*
     * Helper method.
     * When outputting a new board corresponding to this fen,
     * we need to place all the pieces on the board.
     */
    private void handlePieces(PieceStorage ps) {
        int row = 7, column = 0;
        for (int i = 0; i < pieceState.length(); i++) {
            char c = pieceState.charAt(i);
            if (c =='/'){
                row--;
                column = 0;
            } else{
                if (Character.isDigit(c)) {
                    column += Character.getNumericValue(c);
                } else {
                    PieceEncoding pe = PieceEncoding.charToEncoding(c);
                    Square sq= Square.toSquare(row,column);
                    if (pe == null || sq==null) {
                        throw new RuntimeException("Illegal fen: " + this.pieceState);
                    }
                    ps.addNewPiece(pe.toNewPiece(), sq);
                    column++;
                }
            }
        }
    }

    public static FenCode boardToFen(Board board) {
        FenCode fen = new FenCode();
        fen.wCastlingRight = board.getCastlingRight(White);
        fen.bCastlingRight = board.getCastlingRight(Black);
        fen.pieceState = boardToPieceState(board);
       // fen.fullmoveClock = 0;
       // fen.halfmoveClock = 0;
        fen.enPassant = board.getEnpassant();

        fen.turn = board.getTurn();
        return fen;
    }
    // Helper method
    static String boardToPieceState(Board board) {
        StringBuilder output = new StringBuilder();
        int space;
        PieceEncoding currPiece;
        for (int i = rowSize - 1; i >= 0; i--) {
            space = 0;
            for (int j = 0; j < columnSize; j++) {
                currPiece = board.getPieceAt(i, j);
                if (currPiece == null) {
                    space++;
                } else {
                    if (space == 0) {
                        output.append(currPiece);
                    } else {
                        output.append(space);
                        output.append(currPiece);
                        space = 0;
                    }
                }
            }
            if (space != 0) {
                output.append(space);
            }
            if (i != 0) output.append('/');
        }
        return output.toString();
    }

    public boolean equalTo(FenCode fc) {
        return fc.enPassant == enPassant && fc.bCastlingRight == bCastlingRight &&
                fc.pieceState.equals(pieceState) && fc.wCastlingRight == wCastlingRight
                && turn == fc.turn;
    }

    public static FenCode getInitializedFen() {
        return new FenCode(initializedFen);
    }
    @Test
    void generalTest() {
        FenCode fc = new FenCode(initializedFen);
        Assert.assertEquals(fc.pieceState, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        fc.enPassant = null;
        fc.wCastlingRight = Both;
        fc.bCastlingRight = Both;
        fc.turn = White;

        fc = new FenCode("rn6/p2kbn1r/Pp1pqpp1/1PpPp1Pp/1BP2P1P/2QKPN1B/4b3/RN5R w - - 0 1");
        Board board = fc.toBoard();
        Assert.assertEquals(board.toString(),
                """
                        r | n |   |   |   |   |   |   |\s
                        p |   |   | k | b | n |   | r |\s
                        P | p |   | p | q | p | p |   |\s
                          | P | p | P | p |   | P | p |\s
                          | B | P |   |   | P |   | P |\s
                          |   | Q | K | P | N |   | B |\s
                          |   |   |   | b |   |   |   |\s
                        R | N |   |   |   |   |   | R |"""
        );
        Assert.assertTrue(FenCode.boardToFen(board).equalTo(fc)) ;
    }

    @Override
    public String toString() {
        try {
            String enPassantString;
            if (enPassant != null) {
                enPassantString = enPassant.toString();
            } else {
                enPassantString = "-";
            }
            return this.pieceState + " " + this.turn.toChar() + " " + CastlingRight.toString(wCastlingRight, bCastlingRight) + " " + enPassantString; //+ " " + halfmoveClock + " " + fullmoveClock;
        } catch (Exception e) {
            System.out.println("R");
            return "H";
        }
    }
}
