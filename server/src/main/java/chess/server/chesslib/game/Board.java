package chess.server.chesslib.game;

import chess.client.sharedCode.gamerelated.ClientPiece;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.gamerelated.gamepieces.GamePiece;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.helper.CastlingRight;
import chess.server.chesslib.helper.MoveType;
import chess.server.chesslib.helper.PlayerMovement;
import chess.server.chesslib.helper.PlayerMovementWithType;
import chess.server.chesslib.mainlogic.BoardMaps;
import chess.server.chesslib.mainlogic.Logic;
import chess.server.chesslib.mainlogic.MoveGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static chess.client.sharedCode.gamerelated.PieceEncoding.K;
import static chess.client.sharedCode.gamerelated.PieceEncoding.k;
import static chess.client.sharedCode.gamerelated.Square.*;
import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;
import static chess.server.chesslib.helper.MoveType.PawnPromotion;
import static chess.server.chesslib.mainlogic.Logic.*;
import static chess.server.chesslib.mainlogic.MoveGenerator.getPseudoLegalMoveStringList;

/*
 * Represents a Board. It is mostly a high level object, and the "dirty" work is done in its two
 * helper objects.
 */
public class Board {
    /* === Keeps only two objects
    data - stores the data
    piecesStorage - stores the location of pieces
     */
    private final BoardData data;
    private final PieceStorage piecesStorage;

    public Board(PieceStorage ps, BoardData data) {
        this.data = data;
        this.piecesStorage = ps;
        //    data.addFen(this);
        piecesStorage.setBoard(this);
    }


    public static Board getInitializedBoard() {
        return FenCode.getInitializedFen().toBoard();
    }


    public void move(PlayerMovementWithType move, boolean updateData) {
        move(move, move.moveType, updateData);

    }

    /*
     * Moves a piece from a square to another
     */
    public void move(PlayerMovement move, MoveType type, boolean updateData) {
        if (move.from == null) throw new RuntimeException();
        switch (type) {
            case Normal -> piecesStorage.normalMove(move.from, move.to);
            case Enpasse -> piecesStorage.doEnpasse(move, type);
            case WhiteBigCastling, WhiteSmallCastling, BlackSmallCastling, BlackBigCastling -> piecesStorage.doCastle(type);
            case PawnPromotion -> piecesStorage.doPromotion(move);
        }
        data.oppositeTurn();
        data.addFen(this);
        data.setMap(this);
        if (updateData) {
            data.addLegalMoves(MoveGenerator.getLegalMoves(this));
            data.addPseudoLegalMoves(getPseudoLegalMoveStringList(this));
        }
        data.increaseMoveCounter();
    }

    private void move(Square from, Square to) {
        if (!piecesStorage.normalMove(from, to)) {
            throw new RuntimeException();
        }
    }

    public GamePiece getPieceAt(Square tile) {
        if (tile == null) {
            throw new RuntimeException("Square is Null!");
        }
        return piecesStorage.getPieceNotNull(tile);
    }

    public Color getColorAtOrNull(Square title) {
        if (title == null) throw new RuntimeException();
        GamePiece gp = getPieceAt(title);
        if (gp == null) return null;
        return gp.getColor();
    }

    /*
     * Returns true if two squares have pieces of the same color.
     */
    public boolean areSameColor(Square sq1, Square sq2) {
        Color clr1 = getColorAtOrNull(sq1);
        Color clr2 = getColorAtOrNull(sq2);
        if (clr1 == null || clr2 == null) {
            throw new RuntimeException();
        }
        return clr1 != clr2;
    }

    public boolean isBlankSquare(Square sq) {
        return this.getPieceAt(sq) == null;
    }

    /*
     * Returns true if all given squares are blank.
     */
    public boolean areBlankSquares(Square... sqs) {
        for (Square sq : sqs) {
            if (!isBlankSquare(sq)) return false;
        }
        return true;
    }

    public PieceEncoding getPieceEncodingAt(Square tile) {
        if (tile == null) throw new RuntimeException("Square is null!");
        GamePiece gp = piecesStorage.getPieceNotNull(tile);
        if (gp == null) return null;
        return gp.getPieceEncoding();
    }

    /*
     * Outputs the TileNum of the king of the given color.
     */
    public Square getKingSquare(Color threatened) {

        for (Square sq : Square.values()) {
            if (White == threatened) {
                if (getPieceAt(sq) != null && getPieceAt(sq).getPieceEncoding() == K) return sq;
            } else {
                if (getPieceAt(sq) != null && getPieceAt(sq).getPieceEncoding() == k) return sq;
            }
        }
        throw new RuntimeException();
    }

    public Color getPieceColor(Square sq) {
        if (getPieceAt(sq) == null) {
            {
                throw new RuntimeException();
            }
        }
        return getPieceAt(sq).getColor();
    }

    /*
     * Returns the board in a format easier for the client -
     * list of pieces with their location on the board
     */
    public List<Square> getPieces() {
        List<Square> pieces = new LinkedList<>();
        for (Square sq : Square.values()) {
            GamePiece gp = getPieceAt(sq);
            if (gp != null) {
                pieces.add(sq);
            }
        }
        return pieces;
    }

    public BoardMaps getMoveMap() {
        return data.getMap();
    }

    public boolean canCastle(MoveType moveType) {
        CastlingRight bCastle = getCastlingRight(Black), wCastle = getCastlingRight(White);
        return switch (moveType) {
            case WhiteBigCastling, WhiteSmallCastling -> wCastle.contains(moveType);
            case BlackSmallCastling, BlackBigCastling -> bCastle.contains(moveType);
            default -> throw new RuntimeException();
        };
    }

    public List<Set<String>> getLegalMovesStringList() {
        return data.getLegalMovesList();
    }

    public PieceEncoding getPieceAt(int row, int column) {
        Square relevantSquare = Square.toSquare(row, column);
        if (relevantSquare == null) throw new RuntimeException();
        GamePiece gp = getPieceAt(relevantSquare);
        if (gp == null) return null;
        return gp.getPieceEncoding();
    }

    public Color getTurn() {
        return data.getTurn();
    }

    public Square getEnpassant() {
        return data.getEnpasse();
    }

    public List<Set<String>> getPseudoLegalMovesList() {
        return data.getPseudoLegalMovesList();
    }

    public List<Set<String>> getLegalMovesList() {
        return data.getLegalMovesList();
    }

    public List<Set<PlayerMovementWithType>> getLegalMoves() {
        return data.getLegalMoves();
    }

    public Set<PlayerMovementWithType> getCurrentLegalMoves() {
        if (data.moves == 0) return MoveGenerator.getInitalizedLegals();
        return data.getLegalMoves().get(data.getLegalMoves().size() - 1);
    }

    public List<FenCode> getFenList() {
        return data.getFenList();
    }

    public boolean makeMove(PlayerMovement move) {
        MoveType mt = isPseudoMove(this, move);
        if (mt == null) {
            System.out.println("Illegal PseudoMove");
            isPseudoMove(this, move);
            return false;
        }
        if (!isPseudoMoveLegalMove(this, move, mt)) {
            System.out.println("Illegal Move");
            return false;
        }
        move(move, mt, true);
        return true;
    }

    public Board getClone() {
        return FenCode.boardToFen(this).toBoard();
    }

    public CastlingRight getCastlingRight(Color color) {
        if (color == Black) {
            return data.getBlackCastlingRight();
        } else {
            return data.getWhiteCastlingRight();
        }
    }

    public Set<ClientPiece> toPositions() {
        Set<ClientPiece> output = new HashSet<>();
        for (Square sq : Square.values()) {
            if (getPieceAt(sq) != null) {
                ClientPiece cp = new ClientPiece(sq,
                        getPieceAt(sq).getPieceEncoding());
                output.add(cp);
            }
        }
        return output;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int j = rowSize - 1; j >= 0; j--) {
            for (int i = 0; i < columnSize; i++) {
                Square sq = Square.toSquare(j, i);
                GamePiece gp = piecesStorage.getPieceNotNull(sq);
                if (gp == null) {
                    sb.append(" ");
                } else {
                    sb.append(gp.getPieceEncoding());

                }
                sb.append(" | ");
            }
            // sb.append('\n');
            sb.append('\n');
        }
        return sb.toString().trim();
    }

    public GameStatus getGameStatus() {
        return Logic.getGameStatus(this);
    }

    public boolean isPromotion(Square from, Square to, Color color) {
        return getCurrentLegalMoves().stream().anyMatch(x -> x.moveType == PawnPromotion && x.from == from && x.to == to && x.color == color);
    }


    @Test
    void boardTest() {
        Board b = getInitializedBoard();
        Assert.assertEquals(b.toString(), """
                r | n | b | q | k | b | n | r |\s
                p | p | p | p | p | p | p | p |\s
                  |   |   |   |   |   |   |   |\s
                  |   |   |   |   |   |   |   |\s
                  |   |   |   |   |   |   |   |\s
                  |   |   |   |   |   |   |   |\s
                P | P | P | P | P | P | P | P |\s
                R | N | B | Q | K | B | N | R |""");
        b.move(A2, A4);
        b.move(A7, A5);
        b.move(B8, A6);
        b.move(B8, A6);
        b.move(A6, B4);

        b.move(A5, A4);
        Assert.assertEquals(b.toString(), """
                r |   | b | q | k | b | n | r |\s
                  | p | p | p | p | p | p | p |\s
                  |   |   |   |   |   |   |   |\s
                  |   |   |   |   |   |   |   |\s
                p | n |   |   |   |   |   |   |\s
                  |   |   |   |   |   |   |   |\s
                  | P | P | P | P | P | P | P |\s
                R | N | B | Q | K | B | N | R |""");
        //  Assert.assertEquals(this.toString(), "f");
    }

    @Test
    void testSquareClass() {
        Assert.assertEquals(Square.toSquare(0, 0), A1);
        Assert.assertEquals(Square.toSquare(1, 4), E2);
        Assert.assertEquals(Square.toSquare(7, 7), H8);
        Assert.assertEquals(Square.toSquare(1, 6), G2);
    }

}