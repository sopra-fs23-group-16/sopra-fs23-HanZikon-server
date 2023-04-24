package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class GameParamDTO {
    private int level;
    private int numPlayers;
    private String questionType;

    public GameParamDTO(int level, int numPlayers, String questionType) {
        this.level = level;
        this.numPlayers = numPlayers;
        this.questionType = questionType;
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public int getLevel() {
        return level;
    }

    public String getQuestionType() {
        return questionType;
    }
}
