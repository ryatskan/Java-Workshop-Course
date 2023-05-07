package chess.client.communication;

import chess.client.sharedCode.communication.ActionInClient;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.helper.Chat;
import javafx.application.Platform;

import java.rmi.RemoteException;
import java.util.List;

import static chess.client.Data.*;

// Implementing the remote interface. The Server uses this class to run methods in the client
public class ServerActionInClient implements ActionInClient {

    /*
     * Receive an update from the client about the game. Refreshes all information in the client game.
     */
    public void refreshGameInClient(GameUpdate gameUpdate) {
        gameController.refreshGame(gameUpdate);
    }

    @Override
    public void updateActivePlayers(List<String> activePlayers) throws RemoteException {
        if (finishLoadingGraphics) superController.updateActivePlayers(activePlayers);
    }

    @Override

    public void getMsg(String s, String s1) throws RemoteException {
        System.out.println();
    }

    public ServerActionInClient() {
    }

    /*
     * Receives a chat from the server. Prints it onto the chat field if
     * the chat is with the username selected in playerFilter.
     */
    @Override
    public void getChat(Chat chat) {
        Platform.runLater(() -> {
            if (chat.with.equals(superController.playerFilter.valueProperty().get())) {
                superController.setChatBox(chat);
            }
        });
    }
    /*
     * When the opponent offers a draw, outputs a dialog, so that this player can choose to accept or deny
     * the offer.
     */
    @Override
    public void drawOffer() {
        playController.drawOffer();

    }
    /*
     * Ends the current game, and print the given string.
     */
    public void endGame(String print) {
        gameController.endGame(print);
    }

    // Only to check if the client is alive
    @Override
    public void ping() throws RemoteException {
    }
}
