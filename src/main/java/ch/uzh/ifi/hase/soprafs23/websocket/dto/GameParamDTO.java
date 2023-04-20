package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class GameParamDTO {
    private int gameLevel;
    private int numPlayers;
    private String questionType;

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public String getQuestionType() {
        return questionType;
    }
}
