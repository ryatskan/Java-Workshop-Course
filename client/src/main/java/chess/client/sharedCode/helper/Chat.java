package chess.client.sharedCode.helper;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

// Represents a chat between two players.
@Embeddable
public class Chat implements Serializable {
    public String with;
    public List<String> chats;

    public Chat() {
        chats = new LinkedList<>();
        with = "";
    }

    public void addMsg(String msg) {
        chats.add(msg);
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    public void setChats(List<String> chats) {
        this.chats = chats;
    }

    public Chat(String with, List<String> chats) {
        this.with = with;
        this.chats = chats;
    }
}
