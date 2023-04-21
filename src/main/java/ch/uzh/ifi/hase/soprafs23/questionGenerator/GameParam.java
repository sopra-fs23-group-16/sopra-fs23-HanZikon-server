package ch.uzh.ifi.hase.soprafs23.questionGenerator;

public class GameParam {
    int numPlayers;

    // game difficulty level from 1-5
    int gameLevel;
    String questionType;


    public GameParam(int numPlayers, int gameLevel, String questionType) {
        this.numPlayers = numPlayers;
        this.gameLevel = gameLevel;
        this.questionType = questionType;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(int gameLevel) {
        this.gameLevel = gameLevel;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}
