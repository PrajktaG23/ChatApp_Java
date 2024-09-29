import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Writer implements Runnable {
    private PrintWriter write;
    private Socket socket;
    private volatile boolean isRunning;
    private String sender;

    public Writer(Socket socket, String sender) throws IOException {
        this.socket = socket;
        this.write = new PrintWriter(socket.getOutputStream(), true);
        this.isRunning = true;
        this.sender = sender;
    }

    // Method to stop writing
    public void stopWriting() {
        isRunning = false;
    }

    @Override
    public void run() {
        System.out.println("Writing started...");
        try {
            while (isRunning && !socket.isClosed()) {
                BufferedReader read1 = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
                String content = read1.readLine();

                // Prefix message with Server: or Client:
                String message = sender + ": " + content;

                write.println(message);
                write.flush();

                if (content.equalsIgnoreCase("exit")) {
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection closed while writing");
        }
    }
}
