import java.util.ArrayList;

public class Table {
    ArrayList<Card> cards;
    Server server;
    Table(Server server){
        cards = new ArrayList<Card>();
        this.server = server;
    }

    public int addCard(Card card){
        if(takeCards(card)) {
            server.broadcastMessage("TAKE");
            int value = this.calculateValue();
            int amount = cards.size();
            this.clearTable();
            return value;
        }
        cards.add(card);
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
        server.broadcastMessage(Character.toString(card.getLetter()));
        server.broadcastMessage(Character.toString(this.getTopCard()));
        return card.getLetter() == this.getTopCard() || card.getLetter() == 'J';
    }

    public Card getBottomCard(){
        return this.cards.get(0);
    }
    public char getTopCard(){
        if (this.cards.isEmpty()) {
            return '0'; // Handle the case when the collection is empty
        }

        int lastIndex = this.cards.size() - 1;
        return this.cards.get(lastIndex).getLetter();
    }
}
