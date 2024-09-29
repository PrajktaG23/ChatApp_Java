import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

public class Server extends JFrame {

    private ServerSocket server;
    private Socket socket;
   // private ExecutorService executor;
    private BufferedReader read;
    private PrintWriter write;

    // GUI Components
    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Constructor
    public Server() {
        try {
            // Initialize the server socket
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("Waiting...");
            socket = server.accept();
            System.out.println("Client connected");

            // Initialize reader and writer
            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write = new PrintWriter(socket.getOutputStream());

            // Create GUI components
            createGUI();
            handleEvents();

            // Start reader and writer threads
            startReading();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        // Setting up the GUI
        this.setTitle("Server Messenger[END]");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting fonts
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        // Layout management
        this.setLayout(new BorderLayout());

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // Add components to the frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) { // Enter key is pressed
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    write.println(contentToSend);
                    write.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    // Method to read messages from the client
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String message = read.readLine();
                    if (message.equals("exit")) {
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated Chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Client: " + message + "\n");
                }
            } catch (IOException e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is the server...going to start server");
        new Server();
    }
}
