import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable{
    private final int END_GAME_SCORE = 120;
    private final int TOTAL_ROUNDS = 3;
    Server server;
    private Deck deck;
    private Table table;

    private int round = 0;
    private boolean initialDeal;

    private Team teamOne;
    private Team teamTwo;

    private boolean running = false;

    private Player playerTurn;
    private int playerTurnIndex;

    List<Player> players;

    Game(List<User> players, Server server){
        this.server = server;
        this.deck = new Deck();
        this.table = new Table(server);

        this.teamOne = new Team(1);
        this.teamTwo = new Team(2);

        this.playerTurn = null;

        this.players = new ArrayList<Player>();

        // assign teams
        players.stream().limit(2).forEach(p-> new Player(this.teamOne, p));
        players.stream().skip(2).forEach(p-> new Player(this.teamTwo, p));
    }
    void startGame(){
        this.initialDeal = true;
        this.running = true;
        this.deck.shuffleDeck();
        this.dealCards();
        this.dealTableCards();
        this.round = 1;
        this.playerTurnIndex = 0;
        playerTurn = players.get(playerTurnIndex);
    }

    @Override
    public void run(){
        startGame();

        while (isRunning()) {
            server.broadcastMessage("Round " + round);
            server.broadcastMessage("Current table:");
            table.printCards();

            for (Player player : players) {
                server.broadcastMessage("\nPlayer " + player.getName() + "'s turn");
                server.broadcastMessage(player.printCards());

                if(playerTurn == player){
                    String cardInput = player.getUser().getClient().readUserInput();

                    // TODO: handle user input
                    try {
                        int cardInd = Integer.parseInt(cardInput);
                        player.throwCard(player.getCards().get(cardInd), table);
                        // limit to 0-4
                        playerTurn = players.get((playerTurnIndex++)%4);
                    } catch (NumberFormatException e) {
                        server.broadcastMessage("[EXCEPTION] Invalid input. Please enter a valid card input.");
                    } catch (Exception e) {
                        server.broadcastMessage(e.getMessage());
                    }
                }
            }

            if (dealNextRound()) {
                if(deck.endOfDeck()){
                    // TODO: calculate which team has more cards
                    deck = new Deck();

                    if(gameEnded()){
                        Team winner = teamOne.getPoints() > teamTwo.getPoints() ? teamOne : teamTwo;
                        if (teamOne.getNumber() > teamTwo.getNumber()) {
                            teamOne.addPoints(8);
                        } else {
                            if(teamOne.getNumber() != teamTwo.getNumber())
                                teamTwo.addPoints(8);
                        }
                        server.broadcastMessage("The game has ended! The winner is team: " + winner.getNumber());
                        stopGame();
                        return;
                    }

                }
                server.broadcastMessage("Team One Points: " + teamOne.getPoints());
                server.broadcastMessage("Team Two Points: " + teamTwo.getPoints());
                server.broadcastMessage("Dealing next round...");
                dealCards();
                dealTableCards();
                progressRound();
            }
        }
    }

    public boolean gameEnded(){
        return teamOne.getPoints() >= END_GAME_SCORE || teamTwo.getPoints() >= END_GAME_SCORE;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void stopGame(){
        this.running = false;
        this.round = 0;
    }

    public boolean dealNextRound(){
        return players.stream().allMatch(p -> p.getCards().isEmpty());
    }

    void progressRound(){
        this.initialDeal = false;
        this.round++;
        if(this.round > TOTAL_ROUNDS) this.round = 0;
    }

    List<Player> getPlayers() {
        return this.players;
    }

    Table getTable(){
        return this.table;
    }

    void dealCards(){
        for (Player player : players) {
            try {
                player.addCards(this.deck);
            } catch (Exception ex) {
                server.broadcastMessage(ex.getMessage());
            }
        }
    }
    void dealTableCards(){
        try {
            this.table.addCard(this.deck.drawCard());
            this.table.addCard(this.deck.drawCard());
            this.table.addCard(this.deck.drawCard());
            this.table.addCard(this.deck.drawCard());
        } catch (Exception ex) {
            server.broadcastMessage(ex.getMessage());
        }
    }
}
