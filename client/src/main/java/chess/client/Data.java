package chess.client;

import chess.client.controllers.GameController;
import chess.client.controllers.PlayController;
import chess.client.controllers.SuperController;
import chess.client.sharedCode.communication.ActionInServer;
import javafx.scene.Node;
import javafx.stage.Stage;

/*
 * Keeps track of all the variables needed by all objects
 */
public class Data {
    public static String username;
    public static SuperController superController;
    public static Stage stage;
    public static ActionInServer actionInServer;
    public static GameController gameController = new GameController();
    public static PlayController playController;
    public static Node playScreen;
    public static Node homeScreen;
    public static boolean finishLoadingGraphics = false;

}
