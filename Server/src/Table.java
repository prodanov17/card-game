import java.util.ArrayList;

public class Table {
    ArrayList<Card> cards;
    Table(){
        cards = new ArrayList<Card>();
    }

    public int addCard(Card card){
        if(takeCards(card)) {
            System.out.println("TAKE");
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

    public void printCards(){
        System.out.println("Table cards: ");
        if(cards.isEmpty()) System.out.println("-----");
        for (Card card : cards) {
            card.printTable();
        }
    }

    public void clearTable(){
        cards.clear();
    }

    private boolean takeCards(Card card){
        System.out.println(card.getLetter());
        System.out.println(this.getTopCard());
        System.out.println("YE");
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
