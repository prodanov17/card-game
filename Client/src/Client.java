import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private String username;
    private BufferedReader userInputReader;
    private BufferedReader serverInputReader;
    private PrintWriter writer;
    private Socket socket;

    public Client(String username) {
        this.username = username;

        this.userInputReader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String connectTo = "localhost";
//            String connectTo = "85.90.246.130";
            // Connect to the server
            socket = new Socket(connectTo, 21450);
            serverInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Send the username to the server
            writer.println(username);

            // Receive the server's welcome message
            String welcomeMessage = serverInputReader.readLine();
            System.out.println(welcomeMessage);

            // Prompt the user to join or create a game
            handleGameOptions();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGameOptions() {
        try {
            System.out.println("Choose an option:");
            System.out.println("1. Join a game");
            System.out.println("2. Create a game");
            System.out.println("3. Exit");
            String choice = userInputReader.readLine();
            writer.println(choice);
            switch (choice) {
                case "1":
                    System.out.println("Enter game ID: ");
                    String gameId = userInputReader.readLine().trim();
                    writer.println(gameId);
                    String serverResponse = serverInputReader.readLine().trim();
                    System.out.println(serverResponse);
                    if(serverResponse.equals("Invalid game ID. Please try again.")) handleGameOptions();
                    awaitPlayers();
                    break;
                case "2":
                    System.out.println(serverInputReader.readLine().trim());
                    awaitPlayers();
                    break;
                case "3":
                    exit();
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
                    handleGameOptions();
//            while (true) {
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void awaitPlayers(){
        while(true){
            try {
                String response = serverInputReader.readLine();
                if(!response.isEmpty() && Boolean.parseBoolean(response.trim())){
                    break;
                }
                System.out.println(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        startGame();
    }

    private void startGame(){
        while(true){
            try {
                String response = serverInputReader.readLine();
                if(!response.isEmpty() && Boolean.parseBoolean(response.trim())){
                    //if true prompt to play a card
                    String cardIndex = userInputReader.readLine();
                    writer.println(cardIndex);
                    continue;
                }
                if (response.equals("false")) {
                    // Clear the input buffer
                    while (userInputReader.ready()) {
                        userInputReader.readLine();
                    }
                    continue;
                }
                else System.out.println(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void exit() {
        // TODO: Implement logic for exiting the client
        writer.println("3"); // Send the choice to the server
        System.out.println("Exiting...");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        System.out.print("Enter your username: ");
        try {
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String username = userInputReader.readLine();

            Client client = new Client(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
