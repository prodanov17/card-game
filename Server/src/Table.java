import java.util.ArrayList;

public class Table {
    ArrayList<Card> cards;
    boolean firstRound;
    Server server;
    Table(Server server){
        cards = new ArrayList<Card>();
        this.server = server;
        this.firstRound = true;
    }

    public int addCard(Card card){
        if(takeCards(card)) {
            int value = 0;
            if(this.cards.size() == 1 && card.getLetter() != 'J'){
                server.broadcastMessage("ZNK");
                value = this.calculateValue() + 10;
            }
            else {
                server.broadcastMessage("TAKE");
                value = this.calculateValue();
            }
            this.clearTable();
            this.firstRound = false;
            return value;
        }
        cards.add(card);
        this.firstRound = false;
        return -1;
    }

    public int getNumberOfCards(){
        return cards.size();
    }
    public int calculateValue(){
        int sum = 0;
        for (Card card : cards) {
            sum += card.getValue();
        }
        return sum;
    }

    public String printCards(){
        StringBuilder sb = new StringBuilder();
        sb.append("[GAME] Table cards: ");
        if(cards.isEmpty()) sb.append("-----");
        cards.forEach(sb::append);

        return sb.toString();
    }

    public void clearTable(){
        cards.clear();
    }

    private boolean takeCards(Card card){
        char chosenCard = firstRound ? this.getBottomCard() : this.getTopCard();
        if(card.getLetter() == 'J' && this.cards.isEmpty()) return false;
        return card.getLetter() == chosenCard || card.getLetter() == 'J';
    }

    public char getBottomCard(){
        return this.cards.get(0).getLetter();
    }
    public char getTopCard(){
        if (this.cards.isEmpty()) {
            return '0'; // Handle the case when the collection is empty
        }

        int lastIndex = this.cards.size() - 1;
        return this.cards.get(lastIndex).getLetter();
    }

    public void dealTableCards(Deck deck){
        try {
            this.firstRound = true;
            this.cards.add(deck.drawCard());
            this.cards.add(deck.drawCard());
            this.cards.add(deck.drawCard());
            this.cards.add(deck.drawCard());
        } catch (Exception ex) {
            server.broadcastMessage(ex.getMessage());
        }
    }
}
