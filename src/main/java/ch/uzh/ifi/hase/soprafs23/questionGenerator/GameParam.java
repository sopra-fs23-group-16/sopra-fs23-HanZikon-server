package ch.uzh.ifi.hase.soprafs23.questionGenerator;

public class GameParam {
    int participantNumber;
    int gameLevel;
    TypeOfQuestion questionType;
    int numOfQuestion;

    public GameParam(int participantNumber, int gameLevel, TypeOfQuestion questionType,int numOfQuestion) {
        this.participantNumber = participantNumber;
        this.gameLevel = gameLevel;
        this.questionType = questionType;
        this.numOfQuestion = numOfQuestion;
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

    public int getNumOfQuestion() {
        return numOfQuestion;
    }

    public void setNumOfQuestion(int numOfQuestion) {
        this.numOfQuestion = numOfQuestion;
    }
}
