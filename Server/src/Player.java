import java.util.ArrayList;

class Player {
    private String name;
    ArrayList<Card> hand;
    Team team;

    Player(Team team, String name){
        this.name = name;
        this.hand = new ArrayList<Card>();
        this.team = team;
    }


    Player(){};

    public String getName(){
        return this.name;
    }
    private boolean hasCard(int cardId){
        for (Card card : hand) {
            if (card.getId() == cardId) return true;
        }
        return false;
    }

    public ArrayList<Card> getHand(){
        return this.hand;
    }

    public Team getTeam(){
        return this.team;
    }

    public void addCards(Deck deck) throws Exception{
        if(hand.size() >= 4) throw new Exception("Player already has 4 cards");
        for (int i = 0; i < 4; i++) {
            this.hand.add(deck.drawCard());
        }
    }

    public void printCards(){
        System.out.println("Player cards: ");
        hand.forEach(Card::print);
    }

    public void throwCard(int cardId, Table table) throws Exception{
        for (Card card : hand) {
            if (card.getId() == cardId) {
                int amount = table.getNumberOfCards() + 1;
                int points = table.addCard(card);
                if(points > -1){
                    this.team.addPoints(points);
                    this.team.addTotalCards(amount);
                }
                this.hand.remove(card);
                return;
            }
        }
        throw new Exception("Card not found");
    }
}
