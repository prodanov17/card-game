public class Card {
    private static int nextId = 1; // Static variable to keep track of the next ID to assign
    private int id;
    private char letter;
    private String symbol;
    private int value;

    Card(char letter, String symbol, int value){
        this.id = nextId++;
        this.letter = letter;
        this.symbol = symbol;
        this.value = value;
    }

    public String toString(){
        return this.symbol + this.letter;
    }

    public int getValue(){
        return this.value;
    }

    public int getId(){
        return this.id;
    }
    public char getLetter(){
        return this.letter;
    }
}
