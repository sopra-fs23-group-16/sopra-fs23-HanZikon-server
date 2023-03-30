package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

public class HanziDrawingDTO implements QuestionDTO {
    int id;
    String character;
    EvolutionDTO evolution;
    String pinyin;
    String meaning;
    String level;
    public HanziDrawingDTO() {
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

    public EvolutionDTO getEvolution() {
        return evolution;
    }

    public void setEvolution(EvolutionDTO evolution) {
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
}
