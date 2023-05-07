package chess.client.communication;

import chess.client.sharedCode.communication.ActionInClient;
import chess.client.sharedCode.communication.ActionInServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/*
 * A helper class for handling RMI communication.
 */
public class Helper {
    public static int port = 9999;
    public static void exportObject(String username) {
        try {
            // Instantiating the implementation class
            ServerActionInClient obj = new ServerActionInClient();

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            ActionInClient stub = (ActionInClient) UnicastRemoteObject.exportObject(obj, 0);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.getRegistry(port);

            registry.rebind(username, stub);
            // Unbind when program ends
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    close(port, username)//rogkrtgo  rogkrt
                    , "Program existing..."));
        } catch (Exception e) {
            System.err.println("Failed to export object to server; username =  "  + username + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static ActionInServer connect() {
        try {
            Registry var2 = LocateRegistry.getRegistry(port);
            return (ActionInServer) var2.lookup("Server");
        } catch (Exception e) {
            System.out.println("Connection error to the server.");
            System.exit(1);
        }
        return null;
    }
     // Closes connection
    static void close(int port, String bind) {
        try {
            Registry var2 = LocateRegistry.getRegistry(port);
            var2.unbind(bind);
            System.out.println("Removed from registry.");

        } catch (Exception e) {
            System.out.println("Failed to close connection");
        }
    }

}
