import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private User user;
    private Server server;
    private InputStream inputStream;
    private BufferedReader reader;
    private PrintWriter writer;
    private OutputStream outputStream;

    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Prompt the client to send their name
            sendMessage("Please enter your name:");
            String playerName = (String) reader.readLine();
            user = new User(playerName, this);

            // Notify the server that a new user has connected
            writer.printf("%s successfully connected!", playerName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            readMessages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readUserInput() {
        byte[] buffer = new byte[1024];

        try {
            int bytesRead = inputStream.read(buffer);
            return new String(buffer, 0, bytesRead);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void readMessages() {
        try {
            byte[] buffer = new byte[1024];

            while (true) {
                int bytesRead = inputStream.read(buffer);
                String receivedMessage = new String(buffer, 0, bytesRead);
                server.broadcastMessage("Received from client: " + receivedMessage);

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
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
