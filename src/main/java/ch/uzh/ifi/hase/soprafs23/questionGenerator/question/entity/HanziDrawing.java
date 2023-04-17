package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity;

import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "Drawing")
public class HanziDrawing implements Question{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    int id;

    @CsvBindByPosition(position = 0)
    @Column
    String character;

    // evolution store a list of URLs, some fields could be n.a. denoting not applicable
    @CsvBindAndSplitByPosition(position = 1, elementType = String.class, splitOn = ";")
    @ElementCollection
    List<String> evolution;

    @CsvBindByPosition(position = 2)
    @Column
    String pinyin;

    @CsvBindByPosition(position = 3)
    @Column
    String meaning;

    @CsvBindByPosition(position = 4)
    @Column
    String level;

    public HanziDrawing() {}

    public HanziDrawing(int id, String character, List<String> evolution, String pinyin, String meaning, String level) {
        this.id = id;
        this.character = character;
        this.evolution = evolution;
        this.pinyin = pinyin;
        this.meaning = meaning;
        this.level = level;
    }

    @Override
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
}
