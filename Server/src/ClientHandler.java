import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private User user;
    private Server server;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();

            // Prompt the client to send their name
            String playerName = readUserInput().trim();
            user = new User(playerName, this);
            sendMessage("Successfully connected");
            System.out.printf("[LOG] %s successfully connected!", user.getName());
            handleGameOptions();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
//            readMessages();
        } catch (IOException e) {
//            e.printStackTrace();
            handleClientDisconnect();
        }
    }

    public String readUserInput() {
        try {
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1) {
                handleClientDisconnect();
                return null;
            }
            return new String(buffer, 0, bytesRead);
        } catch (IOException e) {
            handleClientDisconnect();
            return null;
        }
    }

    private void handleClientDisconnect() {
        server.removeClient(this);  // Remove the client from the server's list

        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("[DISCONNECT] Client %s has disconnected", this.user.getName());

    }

    private void readMessages() {
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                String receivedMessage = new String(buffer, 0, bytesRead);
                if (bytesRead == -1) {
                    // Client disconnected
                    handleClientDisconnect();
                    break;
                }

                // Broadcast the message to all clients
//                Server.broadcastMessage("Client " + clientSocket.getPort() + ": " + receivedMessage, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void sendMessage(String message) {
        try {
            outputStream.write((message + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGameOptions() {
        String choice = readUserInput().trim();

        switch (choice) {
            case "1":
                // TODO: Implement logic for joining a game
//                sendMessage("Enter game id!"); // sent by client
                String gameId = readUserInput().trim();

                if (!gameId.isEmpty()) {
                    server.joinGame(this, gameId);
                } else {
                    sendMessage("Invalid game ID. Please enter a valid ID.");
                    handleGameOptions();
                }
                break;
            case "2":
                // TODO: Implement logic for creating a game
                String id = server.createGame(this);
                sendMessage(String.format("Game successfully created. Game ID: %s", id));
                break;
            default:
                sendMessage("Invalid choice. Please choose a valid option.");
                handleGameOptions(); // Repeat the prompt
        }
    }

}
