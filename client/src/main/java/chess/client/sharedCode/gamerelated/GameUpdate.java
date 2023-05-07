package chess.client.sharedCode.gamerelated;

import chess.client.sharedCode.helper.Color;

import java.io.Serializable;
import java.util.Set;

/*
 * The object that refreshes all information inside a Client, when it is inside a game.
 */
public class GameUpdate implements Serializable {
    String gameName;
    String whitePlayerName;
    String blackPlayerName;
    // Board update
    Set<ClientPiece> positionList;
    Color currTurn;
    GameStatus gameStatus;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public void setWhitePlayerName(String whitePlayerName) {
        this.whitePlayerName = whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public void setBlackPlayerName(String blackPlayerName) {
        this.blackPlayerName = blackPlayerName;
    }

    public Set<ClientPiece> getPositionList() {
        return positionList;
    }

    public void setPositionList(Set<ClientPiece> positionList) {
        this.positionList = positionList;
    }
    public Color getYourColor(String username) {
        if (username.equals(this.blackPlayerName)) return Color.Black;
        if (username.equals(this.whitePlayerName)) return Color.White;
        throw new RuntimeException("Unexpected");
    }

    public Color getCurrTurn() {
        return currTurn;
    }

    public void setCurrTurn(Color currTurn) {
        this.currTurn = currTurn;
    }

    public GameStatus getGameResult() {
        return gameStatus;
    }

    public void setGameResult(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
