//This class handles reading messages from the InputStream.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Reader implements Runnable {
    private BufferedReader read;
    private Socket socket;
    private volatile boolean isRunning;

    public Reader(Socket socket) throws IOException {
        this.socket = socket;
        this.read = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        this.isRunning = true;
    }

    // Method to stop reading
    public void stopReading() {
        isRunning = false;
    }

    @Override
    public void run() {
        System.out.println("Start reading...");
        try {
            while (isRunning && !socket.isClosed()) {
                String message = read.readLine();
                if (message == null || message.equalsIgnoreCase("exit")) {
                    System.out.println("Client terminated the chat");
                    socket.close();
                    break;
                }
                // Directly print the received message (which already has the prefix)
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Connection closed while reading");
        }
    }
}
