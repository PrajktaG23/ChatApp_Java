import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Client extends JFrame {

    private Socket socket;
    private BufferedReader read;
    private PrintWriter write;

    // GUI components
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connection established");

            // Set up the streams for communication
            read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            write = new PrintWriter(socket.getOutputStream());

            // Create GUI
            createGUI();
            handleEvents();

            // Start reading messages from the server
            startReading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Event handling for message input
    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    write.println(contentToSend);
                    write.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                    
                    // Exit condition
                    if (contentToSend.equalsIgnoreCase("exit")) {
                        try {
                            socket.close();
                            messageInput.setEnabled(false); // Disable input on exit
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    // GUI setup
    private void createGUI() {
        this.setTitle("Client Messenger [END]");
        this.setSize(600, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting fonts
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        // Layout setup
        this.setLayout(new BorderLayout());

        // Component alignment and adding components to frame
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        // Adding components to the frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    // Thread for reading messages from the server
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Start reading...");

            try {
                while (true) {
                    String message = read.readLine();
                    if (message.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated Chat");
                        messageInput.setEnabled(false); // Disable input on exit
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: " + message + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };

        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client...going to start client");
        new Client();
    }
}
