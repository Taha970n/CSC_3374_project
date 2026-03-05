import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient {

    private JTextPane chatPane;
    private JTextArea userList;
    private JTextField messageField;
    private PrintWriter out;
    private String username;

    public ChatClient() {

        JFrame frame = new JFrame("Group Chat Client");

        frame.setSize(750, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Chat pane (supports colors)
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane chatScroll = new JScrollPane(chatPane);

        // User list
        userList = new JTextArea();
        userList.setEditable(false);
        userList.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton sendButton = new JButton("Send");

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(chatScroll, BorderLayout.CENTER);
        frame.add(userScroll, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        username = JOptionPane.showInputDialog(frame, "Enter your username:");

        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username cannot be empty.");
            System.exit(0);
        }

        connectToServer();

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    private void connectToServer() {

        try {

            Socket socket = new Socket("127.0.0.1", 1234);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(username);

            new Thread(() -> {

                String message;

                try {

                    while ((message = in.readLine()) != null) {

                        final String msg = message;

                        if (msg.startsWith("USERS:")) {

                            String users = msg.substring(6);

                            SwingUtilities.invokeLater(() -> {

                                userList.setText("");

                                for (String user : users.split(",")) {

                                    if (!user.isEmpty()) {
                                        userList.append(user + "\n");
                                    }
                                }
                            });

                        } else {

                            SwingUtilities.invokeLater(() -> displayMessage(msg));
                        }
                    }

                } catch (IOException e) {

                    SwingUtilities.invokeLater(() ->
                            displayMessage("Disconnected from server"));
                }

            }).start();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null,
                    "Could not connect to server.");
            System.exit(0);
        }
    }

    private void displayMessage(String message) {

        StyledDocument doc = chatPane.getStyledDocument();
        Style style = chatPane.addStyle("Style", null);

        try {

            if (message.contains("joined the chat")) {

                StyleConstants.setForeground(style, new Color(0, 150, 0));
                StyleConstants.setBold(style, true);

            } else if (message.contains("left the chat")) {

                StyleConstants.setForeground(style, Color.RED);
                StyleConstants.setBold(style, true);

            } else {

                StyleConstants.setForeground(style, Color.BLACK);
                StyleConstants.setBold(style, false);
            }

            doc.insertString(doc.getLength(), message + "\n", style);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {

        String message = messageField.getText().trim();

        if (!message.isEmpty()) {

            out.println(message);
            messageField.setText("");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(ChatClient::new);

    }
}