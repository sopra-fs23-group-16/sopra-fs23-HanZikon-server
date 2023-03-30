package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceDTO implements QuestionDTO{

    private int id;
    private String oracleURL;
    private String choices;
    private int answerIndex;

    private String level;

    public MultipleChoiceDTO(int id, String oraclePicture, String choices, int answerIndex, String level) {
        this.id = id;
        this.oracleURL = oraclePicture;
        this.choices = choices;
        this.answerIndex = answerIndex;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOracleURL() {
        return oracleURL;
    }

    public void setOracleURL(String oraclePicture) {
        this.oracleURL = oraclePicture;
    }

    public String getChoices() {
        return choices;
    }

    public void setChoices(String choices) {
        this.choices = choices;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }

    public String getLevel() {
        return level;
    }

    public MultipleChoiceDTO() {
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
