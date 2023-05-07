package chess.server.chesslib.mainlogic;

import chess.client.sharedCode.gamerelated.gamepieces.King;
import chess.client.sharedCode.gamerelated.gamepieces.Pawn;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.game.FenCode;
import chess.server.chesslib.helper.PlayerMovement;
import chess.server.chesslib.helper.PlayerMovementWithType;
import chess.server.chesslib.helper.MoveType;
import chess.client.sharedCode.gamerelated.Square;
import java.util.*;

import static chess.client.sharedCode.gamerelated.PieceEncoding.*;
import static chess.server.chesslib.mainlogic.Logic.isLegalCastlingMove;

/*
 * Generates all the legal (and pseudo-legal) moves of a board.
 */
public class MoveGenerator {
    public static Set<String> getPseudoLegalMoveStringList(Board board) {
        Set<String> output = new HashSet<>();
        getPseudoLegalMoveList(board).forEach(e -> output.add(e.standardPrint()));
        return output;
    }
    public static Set<PlayerMovementWithType> getInitalizedLegals() {
        return getLegalMoves(FenCode.getInitializedFen().toBoard());
    }

    public static Set<PlayerMovementWithType> getLegalMoves(Board board) {
        Set<PlayerMovementWithType> output = new HashSet<>();
        getPseudoLegalMoveList(board).forEach(e -> {
            if (Logic.isPseudoMoveLegalMove(board, e, e.moveType)) output.add(e);
        });
        return output;
    }
    public static Set<String> getLegalMovesStringList(Board board) {
        Set<String> output = new HashSet<>();
        getLegalMoves(board).forEach(e -> output.add(e.standardPrint()));
        return output;
    }

    public static Set<PlayerMovementWithType> getPseudoLegalMoveList(Board board) {
        // NORMAL MOVES
        Set<PlayerMovementWithType> output = new HashSet<>();
        for (Square from : Square.values()) {
            handleSquare(board, board.getMoveMap(), from, output);
        }
        return output;
    }

    private static void handleSquare(Board board, BoardMaps map, Square from, Set<PlayerMovementWithType> output) {
        if (board.getPieceAt(from) != null) {
            if (board.getPieceAt(from) instanceof Pawn) {
                // PROMOTION, ENPASSANT,
                output.addAll(generatePawnMoves(board, from, board.getTurn()));

            }   // CASTLING
            else if (board.getPieceAt(from) instanceof King) {
                output.addAll(generateCastlingMoves(board, from, board.getTurn()));
            }
            output.addAll(generateRestOfMoves(board, map, from, board.getTurn()));
        }

        // Add movetype
        output.forEach(move -> {
            move.moveType = Logic.isPseudoMove(board, move);
            if (move.moveType == null) throw new RuntimeException();
        });
    }

    private static List<PlayerMovementWithType> generateCastlingMoves(Board board, Square from, Color turn) {
        MoveType moveType;
        List<PlayerMovementWithType> output = new LinkedList<>();
        if (Logic.getInitialKingPosition(turn) != from) return output;
        List<PlayerMovementWithType> castlingMoves = Logic.getCastlingMoves(turn);

        for (PlayerMovementWithType move : castlingMoves) {
            moveType = isLegalCastlingMove(board, move);
            if (moveType != null) {
                output.add(move);
            }
        }
        return output;
    }


    private static List<PlayerMovementWithType> generatePawnMoves(Board board, Square from, Color color) {
        List<PlayerMovementWithType> output = new LinkedList<>();
        Pawn pawn = (Pawn) board.getPieceAt(from);
        if (pawn.getColor() != color) return output; // MUST BE OF SAME COLOR.
        List<Square> toSqs = new LinkedList<>();
        pawn.eatingMoves.forEach(move -> {
            Square sq = Square.getMovement(from, move);
            if (sq != null) toSqs.add(sq);
        });
        pawn.normalMoves.forEach(move -> {
            Square sq = Square.getMovement(from, move);
            if (sq != null) toSqs.add(sq);
        });


        toSqs.forEach(sq -> {
            PlayerMovementWithType move = new PlayerMovementWithType(from, sq, color);
            PlayerMovement tempMove = new PlayerMovement(from, sq, color, PieceEncoding.q); // JUST FOR TEST
            // ENPASSANT

            //PROMOTION
            if (Logic.isLegalPawnPromotion(board, tempMove)) {
                Arrays.stream(PieceEncoding.values()).forEach(pe -> {
                    if (pe != k && pe != K && pe != p && pe != P)
                        output.add(new PlayerMovementWithType(from, sq, color, pe));
                });
            }else if (Logic.isPseudoMove(board, move) != null) {
                output.add(move);
            }
        });
        return output;
    }

    /*
     * Generate all moves not done by a king or a pawn - meaning no special cases to consider.
     */
    private static List<PlayerMovementWithType> generateRestOfMoves(Board board, BoardMaps map, Square from, Color color) {
        List<PlayerMovementWithType> output = new LinkedList<>();
        // all left to generate are normal moves
        List<Square> eatSqs = map.getPseudo2Eats(from);//map.eatMap[toMapIndex(from)];
        List<Square> moveToSqs = map.getPseudoMoves(from);//moveMap[toMapIndex(from)];

        moveToSqs.forEach(e -> {
            if (!(Logic.isLegalPawnPromotion(board, new PlayerMovementWithType(from, e, color, q)))) {
                if (color == board.getPieceColor(from)) {
                PlayerMovementWithType bMove = new PlayerMovementWithType(from, e, color);
                output.add(bMove);
            }
        }});
        return output;
    }


}
