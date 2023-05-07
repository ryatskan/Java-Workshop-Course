package chess.client;

import chess.client.communication.ServerActionInClient;
import chess.client.controllers.SuperController;
import chess.client.sharedCode.communication.ActionInServer;
import chess.client.sharedCode.communication.ConfirmReply;
import chess.client.sharedCode.communication.ConnectionMessage;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

import static chess.client.Data.*;
import static chess.client.communication.Helper.connect;
import static chess.client.communication.Helper.exportObject;

/*
 * The main application. Runs the client.
 */
public class Application extends javafx.application.Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Welcome to Roy's Chess Game.");
        ServerActionInClient obj = new ServerActionInClient();
        actionInServer = connect();
        ConfirmReply op;
        do {
            op =  login(actionInServer, false);
        } while (!op.OK);
        initializeGraphics(stage);
        stage.show();
        finishLoadingGraphics = true;
    }

    private void initializeGraphics(Stage stage) throws IOException {
        Data.stage = stage;
        CSSFX.start();
        SuperController superController = new SuperController();
        FXMLLoader loader = new FXMLLoader(loadURL("/fxml/Super.fxml"));
        loader.setControllerFactory(c -> superController);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setTitle("Roy's ChessGame");

    }

    /*
     * Login into the server. Insert a username and a password
     */
    public ConfirmReply login(ActionInServer remoteAction, boolean random) {

        String password;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username:");

        if (!random) {
            username = scanner.nextLine();
        } else username = String.valueOf(new Random().nextInt(10000));
        System.out.println("Enter password:");
        if (!random) {
            password = scanner.nextLine();
        } else password = String.valueOf(new Random().nextInt(10000));

        try {
            exportObject(username);
            ConfirmReply back = remoteAction.connectionRequest(new ConnectionMessage(username, password));
            if (!back.OK) {
                System.out.println("Response from server: " + back.message);
                return back;
            }
            return new ConfirmReply(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection error to the server.11");
            System.exit(1);
            return null;
        }
    }
    public static URL loadURL(String str) {
        return Application.class.getResource(str);
    }
    public static String load(String path) {
        return loadURL(path).toString();
    }
}