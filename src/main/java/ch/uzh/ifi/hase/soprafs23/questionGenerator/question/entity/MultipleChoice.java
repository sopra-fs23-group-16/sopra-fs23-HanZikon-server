package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity;

import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "multiple_choice_database")
public class MultipleChoice implements Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @CsvBindByPosition(position = 1)
    @Column
    private String oracleURL;

    @CsvBindByPosition(position = 2)
    @Column
    private String choices; //Store four candidate chinese characters in a String, without separation

    @CsvBindByPosition(position = 3)
    @Column
    private int answerIndex; // Store the index of the correct answers

    @CsvBindByPosition(position = 3)
    @Column
    private String level; // Level of the game, from 1-10?

    public MultipleChoice(int id, String oracleURL, String choices, int answerIndex, String level) {
        this.id = id;
        this.oracleURL = oracleURL;
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

    public void setLevel(String level) {
        this.level = level;
    }

    public MultipleChoice() {

    }
}
