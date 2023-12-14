import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private static List<ClientHandler> clients = new ArrayList<>();

    private List<Game> games;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is running on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                // TODO: Handle the client connection, create a new thread or task

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();

                if(clients.size() % 4 == 0){
                    int limit = games.size() * 4;
                    Game game = new Game(clients.stream().skip(limit).limit(4).map(ClientHandler::getUser).collect(Collectors.toList()), this);
                    games.add(game);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcastMessage(String message){
        clients.forEach(c -> c.sendMessage(message));
    }

    public static void main(String[] args) {
        int port = 12345; // Choose a suitable port
        Server server = new Server(port);
        Thread acceptClients = new Thread(server);
        acceptClients.start();

        while(true){
            server.games.forEach(e-> {
                Thread gameThread = new Thread(e);
                gameThread.start();
            });
        }
    }
}
