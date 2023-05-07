package chess.client.sharedCode.communication;

import java.io.Serializable;

public class ConfirmReply implements Serializable {
    public boolean OK;
    public String message;

    public ConfirmReply(boolean OK, String message) {
        super();
        this.OK = OK;
        this.message = message;
    }
    public ConfirmReply(boolean OK) {
        this.OK = OK;
        this.message = "";
    }

}
