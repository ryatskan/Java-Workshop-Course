package chess.server.communication;

import chess.client.sharedCode.communication.ActionInClient;
import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.helper.Chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UserController {
    HashMap<String, ActionInClient> remoteObject = new HashMap<>();

    public ActionInClient connect(String user) {
        try {
            Registry var2 = LocateRegistry.getRegistry(9999);
            return (ActionInClient) var2.lookup(user);
        } catch (Exception e) {
            System.out.println("Connection error to the server.");
            System.exit(1);
        }
        return null;
    }

    ConfirmReply addUserIfNotConnected(String username) {
        if (getActiveUsers().contains(username)) {
            return new ConfirmReply(false, "User already Connected!");
        } else {
            System.out.println("User logged in: " + username);
            remoteObject.put(username, connect(username));
            return new ConfirmReply(true, "");
        }
    }

    boolean removeUser(String username) {
        return remoteObject.remove(username) != null;
    }

    public List<String> getActiveUsers() {
        Set<String> set = remoteObject.keySet();
        List<String> output =new LinkedList<>();
        output.addAll(set);
        return output;
    }

    /*
     * Sends to a player its chat with a different player
     */
    public void sendChat(String sendTo, Chat chat) throws RemoteException {
        ActionInClient serverToClient = getRemote(sendTo);

        serverToClient.getChat(chat);
    }

    public ConfirmReply offerDraw(String player) {
        try {
            getRemote(player).drawOffer();
            return new ConfirmReply(true);
        } catch (Exception e) {
            return new ConfirmReply(false, e.getMessage());
        }
    }


    ActionInClient getRemote(String player) {
        if (!getActiveUsers().contains(player)) {
            throw new RuntimeException("User " + player + " is not connected");
        }
        ActionInClient actionInServer = remoteObject.get(player);
        if (actionInServer == null) {
            throw new RuntimeException("Could not get " + player + "'s remoteObject");
        }
        return actionInServer;
    }

    ConfirmReply refreshGameInClient(String player, GameUpdate gameUpdate) {
        try {
            getRemote(player).refreshGameInClient(gameUpdate);
            return new ConfirmReply(true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ConfirmReply(false, "Failed to refresh " + player + "'s game!");
        }
    }

    /*
     * Ends the game for the player
     */
    boolean endGame(String username, String print) {
        try {
            getRemote(username).endGame(print);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }


    public synchronized void ping(ConnectionManager manager) {
        for (String x : getActiveUsers()) {
            try {
                getRemote(x).updateActivePlayers(getActiveUsers());
            } catch (Exception e) {
                manager.handleUserWithError(x);
            }
        }
    }


}
