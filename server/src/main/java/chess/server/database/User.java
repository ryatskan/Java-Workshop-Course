package chess.server.database;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import chess.client.sharedCode.helper.Chat;

import java.util.*;
/*
 * The object saved in the database. Represents a user.
 */
@Entity
public class User {
    @Id
    //  @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String username;

    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    Set<Chat> chats = new HashSet<>();
    // NO SUPPORT FOR THESE VARIABLES IN CURRENT VERSION
    private int amountOfLogin = 0;
    private int wins = 0;
    private int draws = 0;
    private int games = 0;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        chats = new HashSet<>();
    }

    /*
     * Adds a new message (String) to the chat with chatString.
     */
    @Transactional
    public boolean addMsgToChat(String with, String chatString) {
        Optional<Chat> x = getByString(with);

        if (x.isEmpty()) {

            Chat newChat = new Chat(with, new LinkedList<>());

            newChat.addMsg(chatString);

            chats.add(newChat);
        } else {
            System.out.println("X14");
            x.get().addMsg(chatString);
        }

        return true;
    }

    private Optional<Chat> getByString(String with) {
        for (Chat x : chats) {
            if (x.with.equals(with)) return Optional.of(x);
        }
        return Optional.empty();
    }
    public Set<String> getAllChats() {
        Set<String> output = new HashSet<>();
        chats.forEach(x->output.addAll(x.chats));
        return output;
    }
    public Chat getChatWith(String with) {
        Optional<Chat> op = getByString(with);
        if (op.isPresent()) return op.get();
        else {
            Chat newChat = new Chat(with, new LinkedList<>());
            chats.add(newChat);
            return newChat;
        }
    }
    void addWin() {
        wins++;
        games++;
    }

    void addLose() {
        games++;
    }

    void addDraw() {
        draws++;
        games++;
    }

    void addLogin() {
        amountOfLogin++;
    }

    @Override
    public String toString() {
        return username;
    }

    public User() {
        //chats = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
