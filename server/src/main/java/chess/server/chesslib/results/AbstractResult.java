package chess.server.chesslib.results;

import chess.server.chesslib.game.FenCode;
import java.util.List;
import java.util.Set;
/*
 * Represents the result of a game, with information summarizing the game.
 * Contains static method for comparison.
 */
public abstract class AbstractResult {
    public String lastPosition;
    List<FenCode> fenCodeList;
    List<String> moves;
    List<Set<String>> pseudoLegalMoves;
    List<Set<String>> legalMoves;
    boolean fullyEnded;
    chess.client.sharedCode.gamerelated.GameStatus gameResult;
    public AbstractResult() {}

    public AbstractResult(List<FenCode> fenList, List<String> moves, List<Set<String>> pseudoLegalMoves, List<Set<String>> legal, boolean b, chess.client.sharedCode.gamerelated.GameStatus result, String lastBoard) {
        this.fenCodeList = fenList;
        this.moves = moves;
        this.pseudoLegalMoves = pseudoLegalMoves;
        this.legalMoves = legal;
        this.fullyEnded = b;
        this.lastPosition = lastBoard;
        this.gameResult = result;
    }

    static int compareFenList(List<FenCode> fenCodes1, List<FenCode> fenCodes2, List<String> moves) {
        int i;
        for (i=0;i<Math.min(fenCodes1.size(), fenCodes2.size());i++) {
            FenCode fen1 = fenCodes1.get(i), fen2 = fenCodes2.get(i);
            if (!fen1.equalTo(fen2)) {
                System.out.println("BEFORE:");
                System.out.println(fenCodes1.get(i-1).toBoard().toString());
                System.out.println("OUR BOARD:");
                System.out.println(fen1.toBoard().toString());
                System.out.println();
                System.out.println("REFERENCE:");
                System.out.println(fen2.toBoard().toString());
                System.out.println(moves.get(i));
                return i;
            }
        }
        return i*1000;
    }

    public static boolean compare(ReferenceGameResult refG, GameResult g1) {
        if (!g1.fullyEnded) {
            System.out.println("Game Not Fully Ended!");
            return false;
        }
        if (!refG.moves.equals( g1.getMoves())) {
            System.out.println("Unequal Moves");
            return false;
        }
        // FEN CODE
        int i = compareFenList(g1.getFenCodeList(), refG.getFenCodeList() , refG.moves);
        if (i < Math.max(refG.getFenCodeList().size(),
                g1.getFenCodeList().size())) {
            System.out.println("Less than");
            return false;
        }
        /*if (!g1.getPseudoLegalMoves().equals(refG.getPseudoLegalMoves())) {
            System.out.println("Unequal pseudo legal moves!");
            int k=getFirstUnequal(g1.getPseudoLegalMoves(), refG.getPseudoLegalMoves());
            System.out.println(g1.fenCodeList.get(k).toBoard().toString());
            System.out.println(g1.fenCodeList.get(k).toString());
            return false;
        }*/
        if (!g1.getLegalMoves().equals(refG.getLegalMoves())) {
            System.out.println("Unequal legal moves!");
            int k=getFirstUnequal(g1.getLegalMoves(), refG.getLegalMoves());
            System.out.println(g1.fenCodeList.get(k).toBoard().toString());
            System.out.println(g1.fenCodeList.get(k).toString());
            return false;
        }
        if (refG.getGameResult() != g1.getGameResult()) {
            return false;
        }
        //if (Logic.isCheckMate(new FenCode(g1.lastPosition).toBoard(),

        return true;
    }

    private static int getFirstUnequal(List<Set<String>> g1,
                                       List<Set<String>> reference) {
        if (reference.size() != g1.size()) {
            throw new RuntimeException();
        }
        boolean BREAK = false;
        for (int i=0;i<g1.size();i++) {
            Set<String> refSet = reference.get(i);
            Set<String> g1Set = g1.get(i);
            if (!refSet.equals(g1Set)) {
                if (refSet.size() != g1Set.size()) {
                   System.out.println("UNEQUAL SIZE AT " + i);
                    BREAK = true;
                }
                for (String str: refSet) {
                    if (!g1Set.contains(str)) {
                        System.out.println(str + " - is not in ACTUAL AT " + i);
                        BREAK = true;
                    }
                }
                for (String str: g1Set) {
                    if (!refSet.contains(str)) {
                        System.out.println(str + " - is not in REF AT " + i);
                    BREAK = true;
                    }
                }
            }
            if (BREAK) return i;
        }
        throw new RuntimeException();
    }


    public List<FenCode> getFenCodeList() {
        return fenCodeList;
    }

    public void setFenCodeList(List<FenCode> fenCodeList) {
        this.fenCodeList = fenCodeList;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    public List<Set<String>> getPseudoLegalMoves() {
        return pseudoLegalMoves;
    }

    public void setPseudoLegalMoves(List<Set<String>> pseudoLegalMoves) {
        this.pseudoLegalMoves = pseudoLegalMoves;
    }

    public List<Set<String>> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(List<Set<String>> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public boolean isFullyEnded() {
        return fullyEnded;
    }

    public void setFullyEnded(boolean fullyEnded) {
        this.fullyEnded = fullyEnded;
    }

    public chess.client.sharedCode.gamerelated.GameStatus getGameResult() {
        return gameResult;
    }
}
