package ch.uzh.ifi.hase.soprafs23.questionGenerator.question;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.QuestionPacker;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.*;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.Question;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.ChoiceQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.DrawingQuestionRepository;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CSVService {

    ChoiceQuestionRepository multipleChoiceRepository;
    DrawingQuestionRepository hanziDrawingRepository;


    @Autowired
    public CSVService(@Qualifier("choiceQuestionRepository") ChoiceQuestionRepository choiceQuestionRepository, @Qualifier("drawingQuestionRepository") DrawingQuestionRepository drawingQuestionRepository) {
        this.multipleChoiceRepository = choiceQuestionRepository;
        this.hanziDrawingRepository = drawingQuestionRepository;
    }

    public void saveChoiceQuestion(List<MultipleChoice> questions) {
        try {
            multipleChoiceRepository.saveAll(questions);
        }
        catch (Exception e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public void saveDrawQuestion(List<HanziDrawing> questions) {
        try {
            hanziDrawingRepository.saveAll(questions);
        }
        catch (Exception e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }


    public List<MultipleChoiceDTO> getMultipleChoices() {

        List<MultipleChoice> lists = multipleChoiceRepository.findAll();
        List<MultipleChoiceDTO> questionsDTO = new ArrayList<>();

        for(MultipleChoice question: lists){
            questionsDTO.add(MultiChoiceDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(question));
        }

        return questionsDTO;
    }

    public List<HanziDrawingDTO> getHanziDrawings() throws FileNotFoundException {

        List<HanziDrawing> data = hanziDrawingRepository.findAll();
        List<HanziDrawingDTO> questionsDTO = new ArrayList<>();

        for(HanziDrawing question: data){
            questionsDTO.add(HanziDrawDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(question));
        }
        return questionsDTO;
    }



    // a recursive method to correspond different modes to certain actions
    public List<QuestionDTO> fetchQuestionSet(String level, String mode, int numQuestion) {
        if(mode.equals("Mixed")){
            List<QuestionDTO> result = new ArrayList<>();
            result.addAll(fetchQuestionSet(level,"HanziDrawing",numQuestion/2));
            result.addAll(fetchQuestionSet(level,"MultipleChoice",numQuestion/2));
            Collections.shuffle(result);
            return result;
        }
        else{
            List<Integer> idSet = findAllIdMatches(level,mode);
            return randomSelect(mode,idSet,numQuestion);
        }
    }

    // find all the questions of the given Level
    private List<Integer> findAllIdMatches(String level,String typeOfQuestion){
        if(typeOfQuestion.equals("HanziDrawing")){
            return getIdFromQuestions(hanziDrawingRepository.findAllByLevel(level));
        }
        else if (typeOfQuestion.equals("MultipleChoice")) {
            List<Question> questionList = multipleChoiceRepository.findAllByLevel(level);
            return getIdFromQuestions(multipleChoiceRepository.findAllByLevel(level));
        }
        return null;
    }

    // get all the IDs from the questions that meet the criteria
    private List<Integer> getIdFromQuestions(List<Question> questionList){
        List<Integer> idList = new ArrayList<>();
        for(Question question:questionList){
            idList.add(question.getId());
        }

        return idList;
    }

    // the random selection process to return the required questionPack
    private List<QuestionDTO> randomSelect(String typeOfQuestion, List<Integer> idSet, int num){
        List<QuestionDTO> result = new ArrayList<>();
        List<Integer> selectedIdSet = new ArrayList<>();

        if(idSet.size() > num){
            Collections.shuffle(idSet);
            selectedIdSet = idSet.subList(0,num);
        }
        else{
            selectedIdSet = idSet;
        }
        for(int i:selectedIdSet){
            if(typeOfQuestion.equals("HanziDrawing")){
                result.add(HanziDrawDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(hanziDrawingRepository.findById(i).orElse(null)));
            }
            else if (typeOfQuestion.equals("MultipleChoice")) {
                result.add(MultiChoiceDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(multipleChoiceRepository.findById(i).orElse(null)));
            }
        }
        return result;
    }
}