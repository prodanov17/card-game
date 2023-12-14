import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<Game> games;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptClients() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // TODO: Handle the client connection, create a new thread or task
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345; // Choose a suitable port
        Server server = new Server(port);
        server.acceptClients();
    }
}
