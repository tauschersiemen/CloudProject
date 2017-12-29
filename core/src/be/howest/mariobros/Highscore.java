package be.howest.mariobros;

public class Highscore {
    private String name;
    private String score;
    public Highscore(){

    }

    public void setName(String name){
        this.name = name;
    }

    public void setScore(String score){
        this.score = score;
    }
    public String getName(){
        return this.name;
    }

    public String getScore(){
        return this.score;
    }


    public String toString(){
        return "name:" + this.name +" ,score:" + this.score;
    }
}
