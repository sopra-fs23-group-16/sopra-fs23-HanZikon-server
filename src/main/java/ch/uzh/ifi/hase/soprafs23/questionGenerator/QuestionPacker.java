package ch.uzh.ifi.hase.soprafs23.questionGenerator;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionPacker {
    CSVService service;

    public QuestionPacker(CSVService service) {
        this.service = service;
    }

    public List<QuestionDTO> getQuestionList(GameParamDTO gameParam){
        return service.fetchQuestionSet(Integer.toString(gameParam.getLevel()),gameParam.getQuestionType(),10);
    }

}
