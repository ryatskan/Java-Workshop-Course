package chess.server;

import chess.client.sharedCode.communication.ActionInServer;
import chess.server.communication.ConnectionManager;
import chess.server.communication.GameController;
import chess.server.communication.UserController;
import chess.server.database.CrudDatabase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.Thread.sleep;

/*
 * The main application. Initiates the Spring Application, and the main components - RMI and JPA.
 */
@SpringBootApplication
public class MainServer {
    public static CrudDatabase crudDatabase;
    public static UserController userController = new UserController();
    public static GameController gameController = new GameController();

    // Initializes a new Server. Runs the Spring Application and export a stub.
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(MainServer.class, args);
        ConnectionManager obj = null;
        try {
            obj = new ConnectionManager(crudDatabase, userController, gameController);

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            ActionInServer stub = (ActionInServer) UnicastRemoteObject.exportObject(obj, 0);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.createRegistry(9999);
            registry.rebind("Server", stub);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        pingUsersPeriodically(obj);
        while (true) {
            Scanner scanner = new Scanner(System.in);
			switch (scanner.nextLine()) {
				case "games" -> System.out.println(gameController.getAllGameNames());
				case "activeUsers" -> System.out.println(userController.getActiveUsers());
				case "savedUsers" -> System.out.println(getAllSavedUsers());
				case "allChats" -> System.out.println(getAllChats());
				default -> System.out.println("Unknown command!");
			}
        }
    }
    /*
     * Ping users to remove those who are not connected.
     */
    private static void pingUsersPeriodically(ConnectionManager manager) {
        Timer executor =
                new Timer();

        TimerTask periodicTask = new TimerTask() {
            @Override
            public void run() {
                userController.ping(manager);
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 1000, 1000 * 5);
    }

    @Bean
    public CommandLineRunner run(CrudDatabase database) {
        return args -> crudDatabase = database;
    }

    private static List<String> getAllChats() {
        List<String> output = new LinkedList<>();
        crudDatabase.findAll().forEach(x -> {
            output.add("username: " + x.getUsername());
            output.addAll(x.getAllChats());
        });
        return output;
    }
	private static Set<String> getAllSavedUsers() {
		Set<String> output = new HashSet<>();
		crudDatabase.findAll().forEach(x-> output.add("username: " + x.getUsername() + ";password: " + x.getPassword()));
		return output;
	}

}
