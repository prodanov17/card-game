import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    char[] hearts = {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'X', 'J', 'Q', 'K'};
    ArrayList<Card> cards;

    int pointerAt;
    int amount = 52; // default 52

    Deck(){
        cards = new ArrayList<Card>();
        this.pointerAt = amount -1;
        for (char letter : hearts) {
            int value = 0;
            if(letter == 'J') value = 1;
            if(letter == 'A') value = 1;
            this.cards.add(new Card(letter, "❤️", value));
            value = 0;
            if(letter == 'A') value = 1;
            if(letter == 'J') value = 1;
            this.cards.add(new Card(letter, "♠️", value));
            value = 0;
            if(letter == 'A') value = 1;
            if(letter == 'J') value = 1;
            if(letter == '2') value = 2;
            this.cards.add(new Card(letter, "♣️", value));
            value = 0;
            if(letter == 'A') value = 1;
            if(letter == 'J') value = 1;
            if(letter == 'X') value = 2;
            this.cards.add(new Card(letter, "♦️", value));
        }
    }

    public ArrayList<Card> getCards(){
        return this.cards;
    }

    //works
    void shuffleDeck(){
        Collections.shuffle(cards);
    }

    //works
    public String getDreamCard(){
        return cards.get(0).toString();
    }

    boolean endOfDeck(){
        return this.pointerAt < 0;
    }
    public Card drawCard() throws Exception {
        if(this.endOfDeck()) throw new Exception("End of deck");
        return cards.get(this.pointerAt--);
    }

    // works
    Card getCard(int id){
        Card result = cards.get(0);
        for (int i = 0; i < amount; i++) {
            if(cards.get(i).getId() == id) return cards.get(i);
        }
        return null;
    }
}
