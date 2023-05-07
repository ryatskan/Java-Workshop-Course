package chess.server.chesslib.mainlogic;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

public class Stockfish {
    public static final int waitingTime = 5;
    public static String makeStockfishMovement(String fen) throws IOException, InterruptedException {
        return runPythonScript(fen);
    }
    /*
     * Runs the python script "script.py". The script receives a position and determines the best move
     * The best move is the return value.
     */

    private static String runPythonScript(String input) throws IOException, InterruptedException {
        Files.deleteIfExists(Paths.get("src/main/resources/chess/output.txt"));
        FileWriter inputFile = new FileWriter("src/main/resources/chess/input.txt");
        inputFile.write(input);
        inputFile.close();
        String path = "src/main/resources/chess/script.py";
        ProcessBuilder pb = new ProcessBuilder("py", path).inheritIO();
        // initialize a new python process, and wait for it to finish.
        Process p = Runtime.getRuntime().exec("py " + path);
        p.getInputStream();
        p.getOutputStream();
        //System.out.println(p.getErrorStream().read());
        if (!p.waitFor(waitingTime, TimeUnit.SECONDS)) throw new RemoteException();
        // return the output of the script, saved in a file
        return readOutput();
    }

    static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/chess/output.txt")));
    }

    static String readOutput() throws IOException {
        String output = readFile("src/main/resources/chess/output.txt");
        if (output.length() == 0) throw new RuntimeException("Illegal stockfish");
        return output;
    }
}
