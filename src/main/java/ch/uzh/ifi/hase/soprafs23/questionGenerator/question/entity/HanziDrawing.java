package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity;

import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;

import javax.persistence.*;

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

    @CsvBindAndSplitByPosition(position = 1, elementType = Evolution.class, splitOn = ";")
    @Embedded
    @Column
    Evolution evolution;

    @CsvBindByPosition(position = 2)
    @Column
    String pinyin;

    @CsvBindByPosition(position = 3)
    @Column
    String meaning;

    @CsvBindByPosition(position = 3)
    @Column
    String level;

    public HanziDrawing() {}

    public HanziDrawing(int id, String character, Evolution evolution, String pinyin, String meaning, String level) {
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

    public Evolution getEvolution() {
        return evolution;
    }

    public void setEvolution(Evolution evolution) {
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
