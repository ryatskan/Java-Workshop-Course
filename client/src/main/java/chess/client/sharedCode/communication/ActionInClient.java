package chess.client.sharedCode.communication;


import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.helper.Chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/*
 * Interface for Remote Object inside Client, that can be called from the server
 */
public interface ActionInClient extends Remote {
    void getMsg(String message, String from) throws RemoteException;
    void getChat(Chat chat) throws RemoteException;
    void drawOffer() throws RemoteException;
    void endGame(String print) throws RemoteException;
    void refreshGameInClient(GameUpdate gameUpdate) throws RemoteException;
    void updateActivePlayers(List<String> activePlayers) throws RemoteException;
    void ping() throws RemoteException;
}
