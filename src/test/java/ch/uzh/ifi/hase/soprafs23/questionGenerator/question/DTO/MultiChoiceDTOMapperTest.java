package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MultiChoiceDTOMapperTest {

    ArrayList choices = new ArrayList(Arrays.asList("A","B","C","D"));
    @Test
    public void test_from_MultipleChoiceDTO_to_MultipleChoice(){
        MultipleChoiceDTO multipleChoiceDTO = new MultipleChoiceDTO();
        multipleChoiceDTO.setAnswerIndex(1);
        multipleChoiceDTO.setChoices(choices);
        multipleChoiceDTO.setLevel("1");
        multipleChoiceDTO.setOracleURL("http://fake.url");

        MultipleChoice multipleChoice = MultiChoiceDTOMapper.DTOMapper.INSTANCE.convertDTOtoEntity(multipleChoiceDTO);

        assertEquals(1,multipleChoice.getAnswerIndex());
        assertEquals(choices,multipleChoice.getChoices());
        assertEquals("1",multipleChoice.getLevel());
        assertEquals("http://fake.url",multipleChoice.getOracleURL());
    }

    @Test
    public void test_from_MultipleChoice_to_MultipleChoiceDTO(){
        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.setAnswerIndex(1);
        multipleChoice.setChoices(choices);
        multipleChoice.setLevel("1");
        multipleChoice.setOracleURL("http://fake.url");

        MultipleChoiceDTO multipleChoiceDTO = MultiChoiceDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(multipleChoice);

        assertEquals(1,multipleChoiceDTO.getAnswerIndex());
        assertEquals(choices,multipleChoiceDTO.getChoices());
        assertEquals("1",multipleChoiceDTO.getLevel());
        assertEquals("http://fake.url",multipleChoiceDTO.getOracleURL());
        assertEquals("MultipleChoice",multipleChoiceDTO.getQuestionType());
    }

}