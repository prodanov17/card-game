import java.util.ArrayList;
import java.util.List;

class Player {
    private User user;
    List<Card> hand;
    Team team;

    Player(Team team, User user){
        this.user = user;
        this.hand = new ArrayList<Card>();
        this.team = team;
    }


    Player(){};

    public User getUser(){
        return this.user;
    }

    public List<Card> getCards(){
        return this.hand;
    }

    public String getName(){
        return this.user.getName();
    }
    private boolean hasCard(int cardId){
        for (Card card : hand) {
            if (card.getId() == cardId) return true;
        }
        return false;
    }

    public Team getTeam(){
        return this.team;
    }

    public void addCards(Deck deck) throws Exception{
        if(hand.size() >= 4) throw new Exception("[EXCEPTION] Player already has 4 cards");
        for (int i = 0; i < 4; i++) {
            this.hand.add(deck.drawCard());
        }
    }

    public String printCards(){
        StringBuilder sb = new StringBuilder();
        sb.append("[GAME] Player cards: ");
        hand.forEach(sb::append);

        return sb.toString();
    }

    public void throwCard(Card card, Table table){
        int amount = table.getNumberOfCards() + 1;
        int points = table.addCard(card);
        if(points > -1){
            this.team.addPoints(points);
            this.team.addTotalCards(amount);
        }
        this.hand.remove(card);
    }
}
