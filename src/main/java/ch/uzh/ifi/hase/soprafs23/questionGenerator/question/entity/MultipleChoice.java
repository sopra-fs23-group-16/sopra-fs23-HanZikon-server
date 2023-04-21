package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity;

import com.opencsv.bean.CsvBindAndSplitByPosition;
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

    @CsvBindAndSplitByPosition(position = 2, elementType = String.class, splitOn = ";")
    @ElementCollection
    private List<String> choices; //Store four candidate chinese characters in the List

    @CsvBindByPosition(position = 3)
    @Column
    private int answerIndex; // Store the index of the correct answers

    @CsvBindByPosition(position = 4)
    @Column
    private String level; // Level of the game, from 1-10?

    public MultipleChoice(int id, String oracleURL, List<String> choices, int answerIndex, String level) {
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

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
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
