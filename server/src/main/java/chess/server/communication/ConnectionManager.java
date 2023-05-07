package chess.server.communication;

import chess.client.sharedCode.communication.ActionInServer;
import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.communication.ConnectionMessage;
import chess.client.sharedCode.gamerelated.GameStatus;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.PieceEncoding;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Chat;
import chess.client.sharedCode.helper.Color;
import chess.client.sharedCode.helper.TableEntry;
import chess.server.database.CrudDatabase;
import chess.server.database.User;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Implementing the remote interface
public class ConnectionManager implements ActionInServer {
    CrudDatabase handleDatabase;
    GameController games;
    UserController users;

    public ConnectionManager(CrudDatabase handleDatabase, UserController users, GameController games) {
        this.handleDatabase = handleDatabase;
        this.users = users;
        this.games = games;
    }

    @Override
    /*
     * Handle connection request
     */
    public ConfirmReply connectionRequest(ConnectionMessage Message) throws RemoteException {
        String username = Message.username, password = Message.password;
        if (username == null || password == null) throw new RuntimeException("Username or Password is null");
        if (username.contains(" ")) return new ConfirmReply(false, "No spaces allowed in username");
        if (password.contains(" ")) return new ConfirmReply(false, "No spaces allowed in password");
        Optional<User> op = handleDatabase.findById(username);
        if (op.isPresent()) {
            if (op.get().getPassword().equals(password)) {
                return users.addUserIfNotConnected(op.get().getUsername());
            } else {
                System.out.println("Incorrect Password to login to User : " + op.get());
                return new ConfirmReply(false, "Incorrect Password");
            }
            // does not exist a user with that name
        } else {
            ConfirmReply reply = legalCredentials(username, password);
            if (!reply.OK) return reply;
            User user = new User(username, password);
            try {
                handleDatabase.save(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Added new User: " + username);
            users.addUserIfNotConnected(user.getUsername());
            return new ConfirmReply(true, "");
        }
    }

    private ConfirmReply legalCredentials(String username, String password) {
        if (username.length() < 2) return new ConfirmReply(false, "Username is too short!");
        if (password.length() < 2) return new ConfirmReply(false, "Password is too short!");
        if (password.length() > 12) return new ConfirmReply(false, "Password is too long!");
        if (username.length() > 12) return new ConfirmReply(false, "Username is too long!");
        return new ConfirmReply(true);
    }

    /*
     * Sends a message "s" from "from" to "to"
     */
    @Override
    synchronized public void sendMessage(String to, String from, String s) throws RemoteException {
        Optional<User> toUser = handleDatabase.findById(to);
        Optional<User> fromUser = handleDatabase.findById(from);

        if (fromUser.isPresent() && toUser.isPresent()) {

            fromUser.get().addMsgToChat(to, from + ": " + s);
            toUser.get().addMsgToChat(from, from + ": " + s);
            handleDatabase.save(fromUser.get());
            handleDatabase.save(toUser.get());


            try {
                // Update both the clients of the new chat.
                users.sendChat(to, toUser.get().getChatWith(from));
                users.sendChat(from, fromUser.get().getChatWith(to));
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Sent message from " + from + ", to " + to);
        } else {
            System.out.println("Failed to send a message from " + from + ", to " + to);
        }
    }

    /*
     * Returns a chat of a player with a different player
     */
    @Override
    synchronized public Chat getChat(String username, String with) throws RemoteException {
        Optional<User> user = handleDatabase.findById(username);
        if (user.isPresent()) {
            Chat x = user.get().getChatWith(with);
            System.out.println("Send chat");
            return x;
        }
        System.out.println("Failed to send chat of + " + username + " with " + with);
        return null;
    }

    // === GAME RELATED === //
    /*
     * Creates a new game with a given name and is either singleplayer or multiplayer.
     * Adds the given username as the given color.
     */
    @Override
    public ConfirmReply createGame(String username, String gameName, Color color, boolean singlePlayer) throws RemoteException {
        boolean exist = games.gameExist(gameName);
        if (exist) {
            System.out.println("Attempted to a create a game that exists: " + gameName);
            return new ConfirmReply(false, "Game already exist");
        }
        if (gameName.trim().length() < 2 || gameName.trim().length() > 10) {
            System.out.println("Attempted to a create a game with short/long name: " + gameName);
            return new ConfirmReply(false, "Game name is illegal");
        }
        if (games.createGame(username, gameName, color, singlePlayer)) {
            System.out.println("Created a new game: " + gameName);
            if (singlePlayer) users.refreshGameInClient(username, games.getGameUpdate(gameName));
            return new ConfirmReply(true, "");
        } else {
            System.out.println("Attempted to create a game: " + gameName);
            return new ConfirmReply(false, "Failed to create a Game!");
        }
    }

    /*
     * Client request to join a multiplayer game.
     */
    @Override
    public ConfirmReply joinGame(String username, String gameName) throws RemoteException {
        ConfirmReply reply = games.canJoinMultGame(username, gameName);

        if (!reply.OK) {
            System.out.println(reply.message);
            return new ConfirmReply(false, "FAILED TO LOGIN");
        } else {

            games.joinMultGame(username, gameName);
            // UPDATE THE BOARD IN THE USER THAT JUST JOINED

            GameUpdate gameUpdate = games.getGameUpdate(gameName);
            users.refreshGameInClient(username, gameUpdate);

            ConfirmReply reply2 = games.getOpponentInMult(username, gameName);

            if (!reply2.OK) {
                return new ConfirmReply(false, "Could not get opponent. Reason: " + reply2.message);
            } else {
                ConfirmReply reply3 = users.refreshGameInClient(reply2.message, gameUpdate);
                if (reply3.OK) {
                    System.out.println("User: '" + username + "' has joined the game: '" + gameName + "'.");
                    return new ConfirmReply(true, "");
                } else {
                    // NOTIFY THAT A PLAYER HAS JOINED THE GAME.
                    System.out.println("Failed to refresh in username: '" + reply2.message + "'.");
                    return new ConfirmReply(false, "failed to refresh.");
                }
            }
        }
    }

    @Override
    public boolean isPromotion(String user, String gameName, Square from, Square to) throws RemoteException {
        return games.isPromotion(user, gameName, from, to);
    }

    /*
     * Client request to make a move.
     */
    @Override
    public void makeMove(String user, String gameName, Square from, Square to, PieceEncoding promotion) throws RemoteException {
        // CHECK IF MOVE IS CORRECT
        Optional<GameStatus> result = games.makeMove(user, gameName, from, to, promotion);
        if (result.isPresent()) {


            GameUpdate gameUpdate = games.getGameUpdate(gameName);
            users.refreshGameInClient(user, gameUpdate);

            if (!games.isSinglePlayer(gameName)) {
                ConfirmReply reply2 = games.getOpponentInMult(user, gameName);
                users.refreshGameInClient(reply2.message, gameUpdate);
            }
            if (result.get() != GameStatus.Ongoing)
                games.removeGame(gameName);
            System.out.println("User " + user + " successfully made a move");
        } else {
            terminateGame(gameName, "Critical error. please restart the game");
            System.out.println("FAILED TO MAKE A MOVE");
        }
    }

    @Override
    public Set<Square> getLegalMoves(String username, String gameName, Square from) throws RemoteException {
        if (username == null || gameName == null || from == null) throw new RemoteException("One of input is null");
        return games.getLegalMoves(username, gameName, from);
    }

    @Override
    public ConfirmReply leaveGame(String player, String gameName) throws RemoteException {
        String opponent;
        // Must be inside a game to leave it.
        if (games.isInGame(player, gameName)) {
            if (games.isMultiPlayer(gameName) && games.getOpponentInMult(player, gameName).OK) {
                opponent = games.getOpponentInMult(player, gameName).message;
                users.endGame(opponent, "Your opponent has left the game");
            }
            // anyway, remove the game from the list.
            games.removeGame(gameName);
            return new ConfirmReply(true);
        } else {
            return new ConfirmReply(false, "Player not in this game");
        }
    }

    @Override
    public List<TableEntry> getAllGames() throws RemoteException {
        return games.getAllMultGames();
    }

    /*
     * A request to offer a draw in a game.
     */
    @Override
    public void offerDraw(String player, String gameName) throws RemoteException {
        if (games.isSinglePlayer(gameName)) throw new RuntimeException("You are in singleplayer game");
        ConfirmReply reply = games.getOpponentInMult(player, gameName);
        if (!reply.OK) {
            System.out.println("Failed to find opponent to offer draw in game " + gameName);
        } else {
            ConfirmReply reply2 = users.offerDraw(reply.message);
            if (reply2.OK) {
                System.out.println("User '" + player + "' successfully sent draw offer to '" + reply.message + "'.");
            } else {
                System.out.println("Failed to offer draw to user '" + reply.message + "'.");
            }
        }
    }

    @Override
    public void acceptDraw(String username, String gameName, boolean accept) throws RemoteException {
        if (accept) {
            terminateGame(gameName, "DRAW!");
        }
    }

    synchronized void terminateGame(String gameName, String print) {
        Set<String> players = games.getPlayers(gameName);
        games.removeGame(gameName);
        players.forEach(x -> users.endGame(x, print));
    }

    synchronized void handleUserWithError(String username) {
        String gameName = games.getGameOrNull(username);
        if (gameName != null) {
            if (games.isMultiPlayer(gameName)) {
                ConfirmReply reply = games.getOpponentInMult(username, gameName);
                String opponent = reply.message;
                games.removeGame(gameName);
                users.endGame(opponent, "Your opponent has logged out");
            } else games.removeGame(gameName);
        }
        users.removeUser(username);
        System.out.println("Removed user " + username);
    }
}
