package chess.client.sharedCode.communication;

import java.io.Serializable;

public class ConnectionMessage implements Serializable {
    public String username;
    public String password;

    public ConnectionMessage(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
