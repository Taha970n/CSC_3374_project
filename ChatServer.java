import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 1234;

    private static Set<ClientHandler> clients =
            Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {

        System.out.println("Server started on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler client = new ClientHandler(socket);
                clients.add(client);

                new Thread(client).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    public static void broadcast(String message) {

        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    // Update user list
    public static void updateUsers() {

        StringBuilder users = new StringBuilder("USERS:");

        synchronized (clients) {

            for (ClientHandler client : clients) {
                if (client.username != null) {
                    users.append(client.username).append(",");
                }
            }

            for (ClientHandler client : clients) {
                client.sendMessage(users.toString());
            }
        }
    }

    // Client handler
    static class ClientHandler implements Runnable {

        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {

            if (out != null) {
                out.println(message);
            }
        }

        public void run() {

            try {

                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(
                        socket.getOutputStream(), true);

                // First message = username
                username = in.readLine();

                if (username == null || username.trim().isEmpty()) {
                    username = "Anonymous";
                }

                System.out.println(username + " joined");

                broadcast(username + " joined the chat");
                updateUsers();

                String message;

                while ((message = in.readLine()) != null) {

                    broadcast(username + ": " + message);

                }

            } catch (IOException e) {

                System.out.println(username + " disconnected");

            } finally {

                try {
                    socket.close();
                } catch (IOException ignored) {}

                clients.remove(this);

                broadcast(username + " left the chat");
                updateUsers();
            }
        }
    }
}