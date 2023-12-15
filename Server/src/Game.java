import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable{
    private final int END_GAME_SCORE = 120;
    private final int TOTAL_ROUNDS = 3;
    private Server server;
    private String gameId;
    private String gameName;
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

    Game(Server server, String gameName){
        this.server = server;
        this.deck = new Deck();

        this.teamOne = new Team(1);
        this.teamTwo = new Team(2);
        this.table = new Table(server);

        this.playerTurn = null;

        this.players = new ArrayList<Player>();
        this.gameName = gameName;
        this.gameId = GameIdGenerator.generateUniqueId();
    }

    public void addPlayer(User player) throws Exception{
        if(players.size() > 3) throw new Exception("[EXCEPTION] Game is full. Please join another one!");
        if(players.size() < 2){
            players.add(new Player(this.teamOne, player));
        }
        else {
            players.add(new Player(this.teamTwo, player));
        }
    }

    public String getGameName(){
        return this.gameName;
    }

    public String getGameId(){
        return this.gameId;
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
//        startGame();

        players.forEach(p->p.getUserClient().sendMessage(p.printCards()));
        while (isRunning()) {
            for (Player player : players) {
                server.broadcastMessage(table.printCards());

                if(playerTurn == player){
                    server.broadcastMessage("[INFO] Player " + player.getName() + "'s turn");
                    player.getUserClient().sendMessage(player.printCards());
                    player.getUserClient().sendMessage("[INFO] YOUR TURN!");
                    player.getUserClient().sendMessage("true");
                    String cardInput = player.getUser().getClient().readUserInput().trim();

                    // TODO: handle user input
                    try {
                        int cardInd = Integer.parseInt(cardInput);
                        Card thrownCard = player.getCards().get(cardInd);
                        player.throwCard(thrownCard, table);
                        // limit to 0-4
                        playerTurnIndex++;
                        playerTurn = players.get(playerTurnIndex%4);
                        server.broadcastMessage(String.format("[GAME] %s thrown by %s", thrownCard, player.getUser().getName()));
                    } catch (NumberFormatException e) {
                        player.getUserClient().sendMessage("[EXCEPTION] Invalid input. Please enter a valid card input.");
                        continue;
                    } catch (Exception e) {
                        server.broadcastMessage(e.getMessage());
                        continue;
                    }
                }
                else player.getUserClient().sendMessage("false");
            }

            if (dealNextRound()) {
                boolean dealTable = false;
                if(deck.endOfDeck()){
                    System.out.println("[LOG] End of deck");
                    deck = new Deck();
                    deck.shuffleDeck();
                    if(gameEnded()){
                        Team winner = teamOne.getPoints() > teamTwo.getPoints() ? teamOne : teamTwo;
                        if (teamOne.getNumber() > teamTwo.getNumber()) {
                            teamOne.addPoints(8);
                        } else {
                            if(teamOne.getNumber() != teamTwo.getNumber())
                                teamTwo.addPoints(8);
                        }
                        server.broadcastMessage("[INFO] The game has ended! The winner is team: " + winner.getNumber());
                        stopGame();
                        return;
                    }
                    dealTable = true;
                }
                server.broadcastMessage("[INFO] Team One Points: " + teamOne.getPoints());
                server.broadcastMessage("[INFO] Team Two Points: " + teamTwo.getPoints());
                server.broadcastMessage("[GAME] Dealing next round...");
                dealCards();
                progressRound();
                if(dealTable) dealTableCards();
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
