package chess.client.sharedCode.gamerelated;

import chess.client.sharedCode.helper.Color;

import static chess.client.sharedCode.helper.Color.White;
/*
 * The Result of a chess game.
 */
public enum GameStatus {
    Ongoing,
    Draw,
    WhiteWon,
    BlackWon;

    public static GameStatus toGameResult(Color color) {
        if (color == null) throw new RuntimeException();
        if (color == White) return WhiteWon;
        return BlackWon;
    }

    @Override
    public String toString() {
        switch (this) {
            case Draw -> {return "Game has ended as Draw";}
            case WhiteWon -> {return "Game has ended. White Won.";}
            case BlackWon -> {return "Game has ended. Black Won.";}
            case Ongoing -> {return "Game has not ended yet.";}
            default -> throw new RuntimeException();
        }
    }
}
