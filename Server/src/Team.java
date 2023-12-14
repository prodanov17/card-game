public class Team {
    private int teamNumber;
    private int totalCards;

    private int points;

    Team(int number){
        this.teamNumber = number;
        this.points = 0;
        this.totalCards = 0;
    }

    public void addPoints(int amount){
        this.points += amount;
    }

    public void addTotalCards(int amount){
        this.totalCards += amount;
    }

    public void resetTotalCards(){
        this.totalCards = 0;
    }

    public int getPoints(){
        return this.points;
    }

    public int getNumber() {
        return teamNumber;
    }
}
