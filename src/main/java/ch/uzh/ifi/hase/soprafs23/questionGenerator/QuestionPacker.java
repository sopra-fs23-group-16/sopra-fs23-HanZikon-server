package ch.uzh.ifi.hase.soprafs23.questionGenerator;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.CSVService;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.QuestionDTO;
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

    @PostMapping("/questionPackRequest")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<QuestionDTO> inquiryUser(@RequestBody GameParam gameParam){
        return service.fetchQuestionSet(Integer.toString(gameParam.gameLevel),gameParam.questionType.toString(),gameParam.numOfQuestion);
    }
}
