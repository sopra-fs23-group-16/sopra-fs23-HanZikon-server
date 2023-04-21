package ch.uzh.ifi.hase.soprafs23.questionGenerator;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GameParamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class QuestionPacker {
    CSVService service;

    public QuestionPacker(CSVService service) {
        this.service = service;
    }

    public List<QuestionDTO> getQuestionList(GameParamDTO gameParam){
        return service.fetchQuestionSet(Integer.toString(gameParam.getGameLevel()),gameParam.getQuestionType(),10);
    }

    // direct interface with frontend to retrieve a package of questions of certain parameter
    @PostMapping("/questionPackRequest")

    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<QuestionDTO> getQuestionList(@RequestBody GameParam gameParam){
        return service.fetchQuestionSet(Integer.toString(gameParam.gameLevel),gameParam.questionType.toString(),10);
    }
}
