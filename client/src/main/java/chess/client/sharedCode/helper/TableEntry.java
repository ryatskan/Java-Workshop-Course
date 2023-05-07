package chess.client.sharedCode.helper;

import java.io.Serializable;
/*
 * A game entry
 */
public class TableEntry implements Serializable {
    private String gameName;
    private
    String white;
    private String black;

    public TableEntry(String gameName, String white, String black) {
        this.gameName = gameName;
        this.white = white;
        this.black = black;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getWhite() {
        if (white == null) return "*";

        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getBlack() {
        if (black == null) return "*";
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    @Override
    public String toString() {
        return "gameName: " + gameName + "; white:  " + white  + "; black: " + black;
    }
}
