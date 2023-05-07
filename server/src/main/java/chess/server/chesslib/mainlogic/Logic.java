package chess.server.chesslib.mainlogic;

import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.gamerelated.gamepieces.King;
import chess.client.sharedCode.gamerelated.gamepieces.Pawn;
import chess.client.sharedCode.helper.Color;
import chess.server.chesslib.game.Board;
import chess.server.chesslib.helper.MoveType;
import chess.server.chesslib.helper.PlayerMovement;
import chess.server.chesslib.helper.PlayerMovementWithType;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static chess.client.sharedCode.gamerelated.Square.*;
import static chess.client.sharedCode.helper.Color.Black;
import static chess.client.sharedCode.helper.Color.White;
import static chess.server.chesslib.helper.MoveType.BlackBigCastling;
import static java.lang.Math.abs;


/*
 * Handles logic queries in the game.
 */
public class Logic {
    public static final int rowSize = 8;
    public static final int columnSize = 8;


    // === Global Methods === //

    public static GameStatus getGameStatus(Board board) {
        Set<PlayerMovementWithType> legalMoves = board.getCurrentLegalMoves();
        if (legalMoves.size() == 0) {
            if (isCurrentPlayerChecked(board)) {
                return GameStatus.toGameResult(board.getTurn().getOpposite());
            } else {
                return GameStatus.Draw;
            }
        } else return GameStatus.Ongoing;
    }

    public static MoveType isPseudoMove(Board board, PlayerMovement move) {
        MoveType output;
        if (!preCheckLegality(board, move)) {
            System.out.println("Failed pre-check legality");
            return null;
        } else if (isLegalPawnPromotion(board, move)) { // PROMOTION
            return MoveType.PawnPromotion;
        } else if (isNormalPseudoMove(board, move)) { // REGULAR MOVE
            return MoveType.Normal;
        } else if (isPseudoLegalEnPassant(board, move)) { // ENPASSE
            return MoveType.Enpasse;
        } else if ((output = isLegalCastlingMove(board, move)) != null) { // CASTLING
            return output;
        } else {
            return null;
        }
    }


    public static boolean isPseudoMoveLegalMove(Board board, PlayerMovement move, MoveType moveType) {
        BoardMaps mp = board.getMoveMap();
        switch (moveType) {
            case Enpasse, Normal, PawnPromotion -> {
                Board clone = board.getClone();
                clone.move(move, moveType, false);
                return !isCheck(clone, clone.getMoveMap(), board.getTurn());
            }
            // CASTLING
            default -> {
                return !areThreatened(board, mp, board.getTurn(), moveType.castlingToKingPassingSquares());
            }
        }
    }

    public static boolean isLegalPawnPromotion(Board board, PlayerMovement move) {
        if (!((board.getPieceAt(move.from) instanceof Pawn) && (move.promotion != null))) return false;
        if ((board.getTurn() == White && move.from.getRow() == 6 && move.to.getRow() == 7)
                || (board.getTurn() == Black && move.from.getRow() == 1 && move.to.getRow() == 0)) {
            if (abs(move.to.getColumn() - move.from.getColumn()) == 1) {
                return board.getPieceAt(move.to) != null &&
                        board.getPieceAt(move.to).getColor() == board.getTurn().getOpposite();
            }
            if (abs(move.to.getColumn() - move.from.getColumn()) == 0) {
                return board.getPieceAt(move.to) == null;
            }
        }
        return false;
    }


    public static Square getInitialKingPosition(Color color) {
        if (color == White) return E1;
        return E8;
    }

    // === Non Public Methods === //
    protected static List<PlayerMovementWithType> getCastlingMoves(Color color) {
        List<PlayerMovementWithType> output = new LinkedList<>();
        if (color == White) {
            output.add(new PlayerMovementWithType(E1, C1, White));
            output.add(new PlayerMovementWithType(E1, G1, White));
        } else {
            output.add(new PlayerMovementWithType(E8, C8, Black));
            output.add(new PlayerMovementWithType(E8, G8, Black));
        }
        return output;
    }

    protected static MoveType isLegalCastlingMove(Board board, PlayerMovement move) {
        if (isCheck(board, board.getMoveMap(), board.getTurn())) {
            return null;
        }
        MoveType moveType = isCastle(board, move);
        if (moveType == null) return null;
        if (board.canCastle(moveType) &&
                nothingInBetweenCastle(board, moveType) && !areThreatened(board, board.getMoveMap(), board.getTurn(), moveType.castlingToKingPassingSquares())) {
            return moveType;
        }
        return null;
    }

    private static Boolean isNormalPseudoMove(Board board, PlayerMovement move) {
        BoardMaps mp = board.getMoveMap();
        List<Square> pMoves = mp.getPseudoMoves(move.from);
        List<Square> eMoves = mp.getPseudo2Eats(move.from);
        return pMoves.contains(move.to) || eMoves.contains(move.to);
    }

    private static boolean nothingInBetweenCastle(Board board, MoveType moveType) {
        return board.areBlankSquares(moveType.castlingToPassingSquares());
    }

    private static MoveType isCastle(Board board, PlayerMovement move) {
        if (!(board.getPieceAt(move.from) instanceof King)) return null;
        if (board.getTurn() == White) {
            if (move.to == C1) {
                return MoveType.WhiteBigCastling;
            } else {
                if (move.to == G1)
                    return MoveType.WhiteSmallCastling;
            }
        } else {
            if (move.to == C8) {
                return BlackBigCastling;
            } else {
                if (move.to == G8)
                    return MoveType.BlackSmallCastling;
            }
        }
        return null;
    }

    /*
     * If the move is a legal en passant return true.
     */
    private static boolean isPseudoLegalEnPassant(Board board, PlayerMovement move) {
        Square temp = board.getEnpassant();
        if (temp == null || move.to != temp) return false;
        // CHECK IF THE ROW IS OK
        if (board.getTurn() == White && move.from.getRow() == 4 &&
                abs(move.from.getColumn() - temp.getColumn()) == 1) return true;
        return board.getTurn() == Black && move.from.getRow() == 3 &&
                abs(move.from.getColumn() - temp.getColumn()) == 1;
    }


    private static boolean areThreatened(Board board, BoardMaps mp, Color color, Square... sqs) {
        for (Square sq : sqs) {
            if (mp.getPseudoThreats(board, sq, color).size() != 0) return true;
        }
        return false;
    }

    private static boolean isCurrentPlayerChecked(Board board) {
        return isCheck(board, board.getMoveMap(), board.getTurn());
    }

    /*
     * Only moves that can cause check are pseudo and legal normal moves
     */
    private static boolean isCheck(Board board, BoardMaps mp, Color threatened) {
        Square kingSq = board.getKingSquare(threatened);
        // check every enemy piece if it threatens (normal move) the king
        return mp.getPseudoThreats(board, kingSq, threatened).size() != 0;
    }

    /*
     * Legality checks common to all types of moves
     */
    private static boolean preCheckLegality(Board board, PlayerMovement move) {
        return board.getPieceAt(move.from) != null;
    }
}