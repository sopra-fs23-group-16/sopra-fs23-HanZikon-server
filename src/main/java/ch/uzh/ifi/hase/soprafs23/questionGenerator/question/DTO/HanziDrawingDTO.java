package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import java.util.List;

public class HanziDrawingDTO implements QuestionDTO {
    int id;
    String character;
    List<String> evolution;
    String pinyin;
    String meaning;
    String level;
    String questionType;
    public HanziDrawingDTO() {
        this.questionType = "HanziDrawing";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public List<String> getEvolution() {
        return evolution;
    }

    public void setEvolution(List<String> evolution) {
        this.evolution = evolution;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = "HanziDrawing";
    }
}
