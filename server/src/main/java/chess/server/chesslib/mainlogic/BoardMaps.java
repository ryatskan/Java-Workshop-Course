package chess.server.chesslib.mainlogic;

import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.gamerelated.PieceMovement;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.gamerelated.gamepieces.GamePiece;
import chess.client.sharedCode.gamerelated.gamepieces.Pawn;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.game.PieceStorage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static chess.client.sharedCode.gamerelated.Square.getMovement;
import static chess.client.sharedCode.gamerelated.Square.toSquare;
import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;
import static chess.server.chesslib.game.PieceStorage.toMapIndex;

public class BoardMaps {
    public static BoardMaps initializedBoard = new BoardMaps(FenCode.getInitializedFen().toBoard());
    /**
     * The value of each square map represents:
     * in threatMap - the squares that threaten this square (piece)
     * in eatMap - the squares that this square (piece) can eat
     * in moveMap - the squares that this square (piece) can move to
     * Notice: all of this move are actually pseudo-moves.
     */
    // === The three object we would like to generate === //
    private final List<Square>[] threatMap = new ArrayList[PieceStorage.size];
    private final List<Square>[] eatMap = new ArrayList[PieceStorage.size];
    private final List<Square>[] moveMap = new ArrayList[PieceStorage.size];

    // === Methods to get the maps === //
    public List<Square> getPseudoMoves(Square sq) {
        return moveMap[toMapIndex(sq)];
    }

    public List<Square> getPseudo2Eats(Square sq) {
        return eatMap[toMapIndex(sq)];
    }

    List<Square> getPseudoThreats(Square sq) {
        return threatMap[toMapIndex(sq)];
    }

    // === Methods to generate the maps === //

    public BoardMaps(Board board) {
        initializeMoveMap(board);
        initializeEatMap(board);
        initializeThreatMap(board);
    }

    public List<Square> getPseudoThreats(Board board, Square sq, Color color) {
        List<Square> output = new LinkedList<>();
        List<Square> threats = getPseudoThreats(sq);
        for (Square e : threats) {
            if (board.getPieceAt(e) != null && board.getPieceAt(e).getColor().getOpposite() == color) {
                output.add(e);
            }
        }
        return output;
    }


    /*
     * Initializes a move map. location X in the list is are the possible normal pseudo moves
     * the piece in square X can make.
     */
    private void initializeMoveMap(Board board) {
        for (Square sq : Square.values()) {
            moveMap[toMapIndex(sq)] = updateMoveMap(board, sq);
        }
    }

    /*
     * Initializes an eat map. location X in the list is are the possible normal pseudo eating moves
     * the piece in square X can make.
     */
    private void initializeEatMap(Board board) {
        for (int i = 0; i < eatMap.length; i++)
            eatMap[i] = new ArrayList<>();

        for (Square sq : Square.values()) {
            int index = toMapIndex(sq);
            // Pawns require "special treatment" because their normal move and eating moves are different
            if (board.getPieceAt(sq) instanceof Pawn) {
                for (PieceMovement move : ((Pawn) board.getPieceAt(sq)).eatingMoves) { //TODO FIX DOUPLICATES
                    Square newPosition = getMovement(sq, move);
                    if (newPosition != null) { // if threatened square not out of bound
                        GamePiece possible = board.getPieceAt(newPosition);
                        if (possible != null && possible.getColor().getOpposite()
                                == board.getColorAtOrNull(sq)) {
                            eatMap[index].add(newPosition);
                        }
                    }
                }
            } else { // For all other pieces, their normal moves include all the eating moves.
                List<Square> moves = moveMap[index];
                for (Square movingSq : moves) {
                    if (board.getPieceAt(movingSq) != null) eatMap[index].add(movingSq);
                }
            }
        }
    }

    /*
     * Initializes a threat map. location X in the list is are the squares of the pieces that can
     * make a pseudo normal move and eat the piece in square X.
     */
    private void initializeThreatMap(Board board) {
        for (int i = 0; i < threatMap.length; i++)
            threatMap[i] = new ArrayList<>();
        for (Square sq : Square.values()) {
            int index = toMapIndex(sq);
            // For every square X, if Y is in the eatMap of sq X, add X to the threatMap of sq Y.
            List<Square> eatMoves = moveMap[index];
            eatMoves.addAll(eatMap[index]);
            for (Square eatMove : eatMoves) {
                int eatIndex = toMapIndex(eatMove);
                threatMap[eatIndex].add(sq);
            }

            // special case to handle
            Square to;
            if (board.getPieceAt(sq) instanceof Pawn) {
                if (sq.getRow() == 6 && board.getPieceColor(sq) == White) {
                    to = toSquare(7, sq.getColumn() + 1);
                    if (to != null) threatMap[toMapIndex(to)].add(sq);
                    to = toSquare(7, sq.getColumn() - 1);
                    if (to != null) threatMap[toMapIndex(to)].add(sq);
                }
                if (sq.getRow() == 1 && board.getPieceColor(sq) == Black) {
                    to = toSquare(0, sq.getColumn() + 1);
                    if (to != null) threatMap[toMapIndex(to)].add(sq);
                    to = toSquare(0, sq.getColumn() - 1);
                    if (to != null) threatMap[toMapIndex(to)].add(sq);
                }
            }


        }

    }


    // === Private Methods === //
    /*
     * Update the move map for a single square
     */
    private List<Square> updateMoveMap(Board board, Square sq) {
        if (sq == null) throw new RuntimeException("Square is null!");
        List<Square> output = new ArrayList<>();
        PieceEncoding pe = board.getPieceEncodingAt(sq);
        if (pe == null) return output;
        switch (pe) {
            case p -> handlePawn(board, output, sq, Color.Black);
            case P -> handlePawn(board, output, sq, White);
            case k, K -> handleKing(board, output, sq);
            case B, b -> handleBishop(board, output, sq);
            case q, Q -> handleQueen(board, output, sq);
            case R, r -> handleRook(board, output, sq);
            case N, n -> handleKnight(board, output, sq);
        }
        return output;
    }

    void handlePawn(Board board, List<Square> output, Square sq, Color color) {
        if ((sq.getRow() == 6 && color == White) || (color == Black && sq.getRow() == 1)) return; //IS PROMOTION
        Square oneSq, doubleSq;
        if (color == White) { // IF WHITE PIECE
            oneSq = sq.getMovement(1, 0); // ONE MOVE
            if (board.getPieceAt(oneSq) == null) {
                output.add(oneSq);
                doubleSq = sq.getMovement(2, 0); // DOUBLE MOVE
                if (sq.getRow() == 1 && board.getPieceAt(doubleSq) == null)
                    output.add(doubleSq);
            }
        } else { // IF BLACK PIECE
            oneSq = sq.getMovement(-1, 0); // ONE MOVE
            if (board.getPieceAt(oneSq) == null) {
                output.add(oneSq);
                doubleSq = sq.getMovement(-2, 0); // DOUBLE MOVE
                if (sq.getRow() == 6 && board.getPieceAt(doubleSq) == null)
                    output.add(doubleSq);
            }
        }
    }


    void handleKing(Board board, List<Square> squares, Square sq) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Square newSq = toSquare(sq.getRow() + i, sq.getColumn() + j);
                if (newSq != null && newSq != sq && (board.isBlankSquare(newSq) || board.areSameColor(newSq, sq)))
                    squares.add(newSq);
            }
        }
    }

    void handleKnight(Board board, List<Square> squares, Square sq) {
        int[] possible = {-2, -1, 1, 2};
        for (int x : possible)
            for (int y : possible) {
                Square newSq = toSquare(sq.getRow() + x, sq.getColumn() + y);
                if (x * x != y * y && newSq != null && (board.isBlankSquare(newSq) || board.areSameColor(sq, newSq)))
                    squares.add(newSq);
            }
    }

    void handleQueen(Board board, List<Square> squares, Square sq) {
        handleBishop(board, squares, sq);
        handleRook(board, squares, sq);
    }

    private void handleRook(Board board, List<Square> squares, Square sq) {
        addUntilReachPiece(board, squares, sq, 1, 0);
        addUntilReachPiece(board, squares, sq, -1, 0);
        addUntilReachPiece(board, squares, sq, 0, -1);
        addUntilReachPiece(board, squares, sq, 0, 1);
    }

    private void handleBishop(Board board, List<Square> squares, Square sq) {
        addUntilReachPiece(board, squares, sq, 1, 1);
        addUntilReachPiece(board, squares, sq, -1, -1);
        addUntilReachPiece(board, squares, sq, 1, -1);
        addUntilReachPiece(board, squares, sq, -1, 1);
    }

    /*
     * Adds all the squared encountered in a path (defined by up, right), until reaching a piece
     * or the end of board. Adds all of this to the squares object.
     */
    private void addUntilReachPiece(Board board, List<Square> squares, Square sq, int up, int right) {
        Square tempSquare = toSquare(sq.getRow(), sq.getColumn());
        while (true) {
            tempSquare = toSquare(tempSquare.getRow() + up, tempSquare.getColumn() + right);
            if (tempSquare == null) {
                break;
            }
            if (board.getPieceAt(tempSquare) == null) {
                squares.add(tempSquare);
            } else {
                if (board.areSameColor(tempSquare, sq)) squares.add(tempSquare);
                break;
            }
        }
    }
}
