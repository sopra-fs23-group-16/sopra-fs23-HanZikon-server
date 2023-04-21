package ch.uzh.ifi.hase.soprafs23.questionGenerator;

public class GameParam {
    int numPlayers;

    // game difficulty level from 1-5
    int gameLevel;
    TypeOfQuestion questionType;


    public GameParam(int numPlayers, int gameLevel, TypeOfQuestion questionType) {
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

    public TypeOfQuestion getQuestionType() {
        return questionType;
    }

    public void setQuestionType(TypeOfQuestion questionType) {
        this.questionType = questionType;
    }
}
