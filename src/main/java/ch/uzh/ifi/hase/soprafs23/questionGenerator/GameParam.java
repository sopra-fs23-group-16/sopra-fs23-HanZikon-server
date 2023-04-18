package ch.uzh.ifi.hase.soprafs23.questionGenerator;

public class GameParam {
    int participantNumber;

    // game difficulty level from 1-5
    int gameLevel;
    TypeOfQuestion questionType;


    public GameParam(int participantNumber, int gameLevel, TypeOfQuestion questionType) {
        this.participantNumber = participantNumber;
        this.gameLevel = gameLevel;
        this.questionType = questionType;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public void setParticipantNumber(int participantNumber) {
        this.participantNumber = participantNumber;
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
