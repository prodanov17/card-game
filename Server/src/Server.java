import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server implements Runnable{
    private ServerSocket serverSocket;
    private static List<ClientHandler> clients = new ArrayList<>();

    private List<Game> games = new ArrayList<>();

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

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void broadcastMessage(String message){
        clients.forEach(c -> c.sendMessage(message));
    }

    public String createGame(ClientHandler ch){
        Game game = new Game(this, String.format("%s's game", ch.getUser().getName()));
        try {
            game.addPlayer(ch.getUser());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        games.add(game);

        return game.getGameId();
    }


    public void joinGame(ClientHandler ch, String gameId) {
        // Find the game with the specified ID
        Game chosenGame = findGameById(gameId);

        if (chosenGame != null) {
            // Add the client to the chosen game
            try{
                chosenGame.addPlayer(ch.getUser());
            }
            catch(Exception e){
                ch.sendMessage(e.getMessage());
                return;
            }

            // Notify the client that they joined the game
            ch.sendMessage("Joined game: " + chosenGame.getGameName());
            chosenGame.getPlayers().forEach(e-> e.getUser().getClient().sendMessage(String.format("%s has joined this game", ch.getUser().getName())));

            // Start the game thread if it's not already running
            if (!chosenGame.isRunning() && chosenGame.getPlayers().size() == 4) {
                chosenGame.getPlayers().forEach(e->e.getUser().getClient().sendMessage("true"));
                chosenGame.startGame();
                Thread gameThread = new Thread(chosenGame);
                gameThread.start();
            }
        } else {
            ch.sendMessage("Invalid game ID. Please try again.");
        }
    }

    private Game findGameById(String gameId) {
        for (Game game : games) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }
        return null; // Game with the specified ID not found
    }

    public void removeClient(ClientHandler client){
        clients.remove(client);
    }

    public static void main(String[] args) {
        int port = 21450; // Choose a suitable port
        Server server = new Server(port);
        Thread acceptClients = new Thread(server);
        acceptClients.start();
    }
}
