package chess.client.controllers;

import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.gamerelated.GameUpdate;
import chess.client.sharedCode.gamerelated.Square;
import chess.client.sharedCode.helper.Color;
import chess.client.sharedCode.gamerelated.PieceEncoding;

import java.io.IOException;
import java.rmi.RemoteException;

import static chess.client.Data.*;
import static chess.client.sharedCode.gamerelated.GameStatus.Ongoing;

/*
 * If the client is inside a game, this class keeps track of it, logic side.
 */
public class GameController {
    public boolean insideGame = false;
    public String gameName;
    private Color myColor;
    private boolean isMyTurn;

    public GameController() {
        gameController = this;
    }

    /*
     * Creates a new chess game, with a given name and color.
     * Create a single-player if singlePlayer == T, else create a 2-player game.
     */
    public void newGame(String gameName, Color thisPlayerColor, boolean singlePlayer) {
        if (insideGame) {
            System.out.println("Already inside game!");
            return;
        }
        try {
            ConfirmReply reply = actionInServer.createGame(username, gameName, thisPlayerColor, singlePlayer);
            if (!reply.OK) {
                System.out.println(reply.message);
                return;
            }
            this.gameName = gameName;
            playController.initializeNewGame(thisPlayerColor);
            superController.setContent(false);

            setInsideGame(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInsideGame(boolean insideGame) {
        this.insideGame = insideGame;
    }

    /*
     * Joins an exist game gameName.
     */
    public void joinGame(String gameName) {
        ConfirmReply reply;
        if (insideGame) {
            System.out.println("Already inside game!");
            System.exit(1);
        }

        try {
            reply = actionInServer.joinGame(username, gameName);
        } catch (Exception e) {
            System.out.println("Failed to join game " + gameName + ".");
            throw new RuntimeException(e);
        }
        try {
            if (reply.OK) {
                System.out.println("Joined game " + gameName + ".");
                playController.initializeNewGame(myColor);
                superController.setContent(false);//superController.playScreen);
                insideGame = true;
            } else {
                System.out.println(reply.message);
                return;
            }
        } catch (Exception e) {
            System.out.println("Failed to load the game");
            throw new RuntimeException(e);
        }


    }

    /*
     * Leaves the current game screen. Changes the screen from Play.fxml to HomePage.fxml.
     * Also notify server
     */
    public void leaveGame() {
        // leaving a game after it has ended.
        try {
            // root = FXMLLoader.load(MFXDemoResourcesLoader.class.getResource("/fxml/Super.fxml"));
            superController.setContent(true);
            //superController.toggleGroup.selectToggle(superController.toggleGroup.getToggles().get(0));
            //  superController.setContent(;);
        } catch (Exception e) {
            System.out.println("Failed to change screen from Play.fxml to HomePage.fxml");
            System.exit(1);
        }
        // leaving a game while it is inside one, meaning that we need to notify server
        if (insideGame) {
            try {
                ConfirmReply reply = actionInServer.leaveGame(username, gameName);
                if (!reply.OK) {
                    System.out.println("Failed to leave the game '" + gameName + "'. reason: " + reply.message);
                    System.exit(1);
                }
                setInsideGame(false);
                //  superController.toggleGroup.selectToggle(superController.toggleGroup.getToggles().get(0));
                System.out.println("Successfully left the game '" + gameName + "'.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getCurrentGame() {
        if (!insideGame) return null;
        return gameName;
    }

    /*
     * Activates when a game ends. Simply freezes the board, and waits for the user to leave the game.
     */
    public void endGame(String print) {
        if (!insideGame) {
            System.out.println("Not inside game!");
            return;
        }
        System.out.println("Current game has ended!");
        playController.printHeadline(print);
        insideGame = false;
        gameName = null;
    }

    /*
     * Send a draw offer.
     */
    public void offerDraw() {
        if (!insideGame) {
            System.out.println("Cant offer draw. reason: not in a game");
            return;
        }
        try {
            actionInServer.offerDraw(username, gameName);
            System.out.println("Successfully sent draw offer in game '" + gameName + "'.");
        } catch (Exception e) {
            System.out.println("Failed to offer draw in game '" + gameName + "'.");
        }
    }

    /*
     * Refreshes the chess game, based on info given from server.
     */
    public void refreshGame(GameUpdate gameUpdate) {
        if (!insideGame)
            insideGame = true;

        if (gameUpdate.getGameResult() != Ongoing) {
            endGame(gameUpdate.getGameResult().toString());
        }
        updateTurn(gameUpdate);
        this.gameName = gameUpdate.getGameName();
        this.myColor = gameUpdate.getYourColor(username);
        this.isMyTurn = gameUpdate.getCurrTurn() == myColor;
        playController.refreshGame(gameUpdate);
    }

    void makeMove(String username, String gameName, Square from, Square to) throws RemoteException {
        PieceEncoding promotion = null;
        if (actionInServer.isPromotion(username, gameName, from, to))
            playController.getPromotionAndMove(from, to);
        else actionInServer.makeMove(username, gameName, from, to, promotion);
    }

    void makePromotionMove(Square from, Square to, Character character) {
        try {
            actionInServer.makeMove(username, gameName, from, to, PieceEncoding.charToEncoding(character, myColor));
        } catch (Exception e) {
            System.out.println("Failed to make promotion move");
            System.exit(1);
        }
    }


    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void updateTurn(GameUpdate gameUpdate) {
    }

    public Color getMyColor() {
        return myColor;
    }

    public void setMyColor(Color myColor) {
        this.myColor = myColor;
    }
}
