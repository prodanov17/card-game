import java.util.ArrayList;
import java.util.List;

public class Game implements Runnable{
    private final int END_GAME_SCORE = 120;
    private Deck deck;
    private Table table;

    private int round = 0;
    private boolean initialDeal;

    private Team teamOne;
    private Team teamTwo;

    private boolean running = false;

    List<Player> players;

    Game(List<User> players){
        this.deck = new Deck();
        this.table = new Table();

        this.teamOne = new Team(1);
        this.teamTwo = new Team(2);

        this.players = new ArrayList<Player>();

        // assign teams
        players.stream().limit(2).forEach(p-> new Player(this.teamOne, p.getName()));
        players.stream().skip(2).forEach(p-> new Player(this.teamTwo, p.getName()));

    }
    void startGame(){
        this.initialDeal = true;
        this.running = true;
        this.deck.shuffleDeck();
        this.dealCards();
        this.dealTableCards();
        this.round = 1;
    }

    @Override
    public void run(){
        startGame();

        while (isRunning()) {
            System.out.println("Round " + round);
            System.out.println("Current table:");
            table.printCards();

            for (Player player : players) {
                System.out.println("\nPlayer " + player.getName() + "'s turn");
                player.printCards();

//                String cardInput = userInput.nextLine();
                // TODO: handle user input
                try {
                    int cardValue = Integer.parseInt(cardInput);
                    player.throwCard(cardValue, table);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid card value.");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (dealNextRound()) {
                if(deck.endOfDeck()){
                    // TODO: calculate which team has more cards
                    deck = new Deck();

                    if(gameEnded()){
                        Team winner = teamOne.getPoints() > teamTwo.getPoints() ? teamOne : teamTwo;
                        System.out.println("The game has ended! The winner is team: " + winner.getNumber());
                        return;
                    }
                }
                System.out.println("Team One Points: " + teamOne.getPoints());
                System.out.println("Team Two Points: " + teamTwo.getPoints());
                System.out.println("Dealing next round...");
                dealCards();
                dealTableCards();
                progressRound();
            }
        }
    }

    public boolean gameEnded(){
        return teamOne.getPoints() >= 120 || teamTwo.getPoints() >= 120;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void stopGame(){
        this.running = false;
        this.round = 0;
    }

    public boolean dealNextRound(){
        return players.stream().allMatch(p -> p.getHand().isEmpty());
    }

    void progressRound(){
        this.round++;
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
                System.out.println(ex.getMessage());
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
            System.out.println(ex.getMessage());
        }
    }
}
