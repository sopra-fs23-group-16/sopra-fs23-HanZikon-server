package ch.uzh.ifi.hase.soprafs23.questionGenerator;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.ChoiceQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.DrawingQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionPackerTest {

    @Qualifier("choiceQuestionRepository")
    @Autowired
    ChoiceQuestionRepository choiceQuestionRepository;

    @Autowired
    @Qualifier("drawingQuestionRepository")
    DrawingQuestionRepository drawingQuestionRepository;

    @Autowired
    CSVService service;

    QuestionPacker questionPacker;



    @Test
    void getQuestionList_Integration() {
        this.questionPacker = new QuestionPacker(service);
        ArrayList<QuestionDTO> resultList = new ArrayList<>();

        assertEquals(10,questionPacker.getQuestionList(new GameParamDTO(1,2,"Mixed",10)).size());
        assertEquals(10,questionPacker.getQuestionList(new GameParamDTO(2,2,"HanziDrawing",10)).size());
        assertEquals(10,questionPacker.getQuestionList(new GameParamDTO(3,2,"MultipleChoice",10)).size());

    }
}