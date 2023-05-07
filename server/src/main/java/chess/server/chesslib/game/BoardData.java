package chess.server.chesslib.game;

import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.gamerelated.gamepieces.GamePiece;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.helper.CastlingRight;
import chess.server.chesslib.helper.PlayerMovementWithType;
import chess.server.chesslib.mainlogic.BoardMaps;

import java.util.*;

import static chess.client.sharedCode.gamerelated.Square.*;
import static chess.server.chesslib.helper.CastlingRight.*;

/*
 * Stores data about the pieces.
 * NOT the main storage of pieces, which is the Tiles array.
 *
 */
public class BoardData {
    // === Variables ===/
    private BoardMaps map;
    public int moves;
    private Color turn;
    private CastlingRight whiteCastlingRight;
    private CastlingRight blackCastlingRight;
    private Square enPassant = null;
    List<FenCode> fenList = new LinkedList<>();
    List<Set<String>> pseudoLegalMovesList = new LinkedList<>();
    List<Set<PlayerMovementWithType>> legalMovesList =new LinkedList<>();



    // === Access & Modify Variables === //
    public Square getEnpasse() {
        return enPassant;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }
    public void oppositeTurn() {
        setTurn(getTurn().getOpposite());
    }
    public void setEnpassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    public void setWhiteCastlingRight(CastlingRight inputCastlingRight) {
        if (inputCastlingRight == null) throw new RuntimeException();
        whiteCastlingRight = inputCastlingRight;
    }
    public void removeOneWhiteCastlingRight(CastlingRight inputCastlingRight) {
        setWhiteCastlingRight(remove(getWhiteCastlingRight(), inputCastlingRight));
    }
    public void removeOneBlackCastlingRight(CastlingRight inputCastlingRight) {
        setBlackCastlingRight( remove(getBlackCastlingRight(), inputCastlingRight));
    }
    public void setBlackCastlingRight(CastlingRight inputCastlingRight) {
        if (inputCastlingRight == null) {
            throw new RuntimeException();
        }
        blackCastlingRight = inputCastlingRight;
    }

    public CastlingRight getWhiteCastlingRight() {
        return whiteCastlingRight;
    }

    public CastlingRight getBlackCastlingRight() {
        return blackCastlingRight;
    }

    public void setMap(Board board) {
        if (board == null) throw new RuntimeException();
        this.map = new BoardMaps(board);
    }
    public void addLegalMoves(Set<PlayerMovementWithType> legalMoves) {
        this.legalMovesList.add( legalMoves);
    }
    public void addPseudoLegalMoves(Set<String> legalMoves) {
        this.pseudoLegalMovesList.add(legalMoves);
    }

    public List<Set<String>> getPseudoLegalMovesList() {
        return pseudoLegalMovesList;
    }


    public List<Set<String>> getLegalMovesList() {
        List<Set<String>> output = new LinkedList<>();

        legalMovesList.forEach(set -> {
            Set<String> newSet = new HashSet<>();
            set.forEach(move -> newSet.add(move.standardPrint()));
            output.add(newSet);});
        return output;
    }
    public List<Set<PlayerMovementWithType>> getLegalMoves() {
        return legalMovesList;
    }

    public BoardMaps getMap() {
        return map;
    }

    public void increaseMoveCounter() {
        moves++;
    }

    public void addFen(Board inputBoard) {
        fenList.add(FenCode.boardToFen(inputBoard));
    }

    public List<FenCode> getFenList() {
        return fenList;
    }

    public Color getTurn() {
        return turn;
    }
    public void updateCastlingRightsFrom(PieceEncoding pe, Square sq) {
        switch (pe) {

            case k -> setBlackCastlingRight(None);
            case K -> setWhiteCastlingRight(None);
            case r -> {
                if (sq == A8) {
                    removeOneBlackCastlingRight(CastlingRight.BigCastle);
                }
                if (sq == H8) {
                    removeOneBlackCastlingRight(CastlingRight.SmallCastle);
                }
            }
            case R -> {
                if (sq == A1) {
                    removeOneWhiteCastlingRight(CastlingRight.BigCastle);
                }
                if (sq == H1) {
                    removeOneWhiteCastlingRight(CastlingRight.SmallCastle);
                }
            }
        }
    }

    public void updateCastlingRightsTo(GamePiece gp, Square to) {
        if (gp == null) return;
        updateCastlingRightsFrom(gp.getPieceEncoding(), to);
    }
}

