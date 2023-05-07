package chess.client.sharedCode.communication;

import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Chat;
import chess.client.sharedCode.helper.Color;
import chess.client.sharedCode.helper.TableEntry;
import chess.client.sharedCode.gamerelated.PieceEncoding;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/*
 * Interface for Remote Object inside Server, that can be called from the client.
 */
public interface ActionInServer extends Remote {

    public boolean isPromotion(String user, String gameName, Square from, Square to) throws RemoteException;
    public void makeMove(String user, String gameName, Square from, Square to, PieceEncoding promotion) throws RemoteException;

    public ConfirmReply connectionRequest(ConnectionMessage Message) throws RemoteException;

    public ConfirmReply createGame(String username, String gameName, Color color, boolean singlePlayer) throws RemoteException;

    public Set<Square> getLegalMoves(String username, String gameName, Square from) throws RemoteException;

    public ConfirmReply leaveGame(String username, String gameName) throws RemoteException;

    public ConfirmReply joinGame(String username, String gameName) throws RemoteException;


    public void sendMessage(String to, String from, String message) throws RemoteException;

    public Chat getChat(String of, String with) throws RemoteException;

    public List<TableEntry> getAllGames() throws RemoteException;

    public void offerDraw(String username, String gameName) throws RemoteException;

    public void acceptDraw(String username, String gameName, boolean accept) throws RemoteException;
}

