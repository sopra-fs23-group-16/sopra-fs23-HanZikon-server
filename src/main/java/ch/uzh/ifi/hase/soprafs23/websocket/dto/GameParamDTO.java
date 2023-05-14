package ch.uzh.ifi.hase.soprafs23.websocket.dto;

public class GameParamDTO {
    private int level;
    private int numPlayers;
    private String questionType;

    private int numQuestion;

    public GameParamDTO(int level, int numPlayers, String questionType,int numQuestion) {
        this.level = level;
        this.numPlayers = numPlayers;
        this.questionType = questionType;
        this.numQuestion = numQuestion;
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

    public int getNumQuestion() {
        return numQuestion;
    }
}
