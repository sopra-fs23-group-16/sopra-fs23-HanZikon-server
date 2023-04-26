package ch.uzh.ifi.hase.soprafs23.questionGenerator.question;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.Question;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.ChoiceQuestionRepository;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.repository.DrawingQuestionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebAppConfiguration
@SpringBootTest
class CSVServiceTest {

    @Autowired
    CSVService csvService;
    List<MultipleChoice> multipleChoiceList;
    List<HanziDrawing> hanziDrawingList;

    @Qualifier("choiceQuestionRepository")
    @Autowired
    ChoiceQuestionRepository choiceQuestionRepository;

    @Autowired
    @Qualifier("drawingQuestionRepository")
    DrawingQuestionRepository drawingQuestionRepository;

    @InjectMocks
    CSVService spyService;

    @Mock
    ChoiceQuestionRepository mockChoiceRepo;

    @Mock
    DrawingQuestionRepository mockDrawRepo;


    @BeforeEach
    public void setup() {
        drawingQuestionRepository.deleteAll();
        choiceQuestionRepository.deleteAll();

        spyService.setHanziDrawingRepository(mockDrawRepo);
        spyService.setMultipleChoiceRepository(mockChoiceRepo);

    }

    @BeforeEach
    void initialize(){
        ArrayList<String> fillerList = new ArrayList<>(Arrays.asList("filler1","filler2","filler3","filler4"));
        this.multipleChoiceList = new ArrayList<>();
        this.hanziDrawingList = new ArrayList<>();

        MultipleChoice multipleChoice = new MultipleChoice();
        multipleChoice.setAnswerIndex(1);
        multipleChoice.setChoices(fillerList);
        multipleChoice.setLevel("1");
        multipleChoice.setOracleURL("http://fake.url");
        multipleChoice.setId(1);

        this.multipleChoiceList.add(multipleChoice);
        MultipleChoice multipleChoice1 = new MultipleChoice();
        multipleChoice1.setId(2);
        multipleChoice1.setLevel("1");
        this.multipleChoiceList.add(multipleChoice1);
        assertEquals(2,this.multipleChoiceList.size());

        HanziDrawing hanziDrawing = new HanziDrawing();
        hanziDrawing.setCharacter("test");
        hanziDrawing.setEvolution(fillerList);
        hanziDrawing.setLevel("1");
        hanziDrawing.setMeaning("testMeaning");
        hanziDrawing.setPinyin("testPinyin");
        hanziDrawing.setId(1);

        this.hanziDrawingList.add(hanziDrawing);
        HanziDrawing hanziDrawing1 = new HanziDrawing();
        hanziDrawing1.setId(2);
        hanziDrawing1.setLevel("1");
        this.hanziDrawingList.add(hanziDrawing1);
        assertEquals(2,this.hanziDrawingList.size());


    }

    @Test
    void saveChoiceQuestion_with_success() {
        csvService.saveChoiceQuestion(this.multipleChoiceList);
        assertEquals(2,csvService.getMultipleChoices().size());
    }

    @Test
    void saveChoiceQuestion_with_null_raiseException() {
        assertThrows(RuntimeException.class,() -> csvService.saveChoiceQuestion(null));
    }

    @Test
    void saveHanziDrawQuestion_with_success(){
        csvService.saveDrawQuestion(this.hanziDrawingList);
        assertEquals(2,csvService.getHanziDrawings().size());
    }

    @Test
    void saveHanziDrawQuestion_with_null_raiseException(){
        assertThrows(RuntimeException.class,() -> csvService.saveDrawQuestion(null));
    }

    @Test
    void fetchQuestionSet_integration() {
        this.csvService.saveChoiceQuestion(multipleChoiceList);
        this.csvService.saveDrawQuestion(hanziDrawingList);
        
        assertEquals(1,this.csvService.fetchQuestionSet("1","HanziDrawing",1).size());
        assertEquals(2,this.csvService.fetchQuestionSet("1","HanziDrawing",500).size());

        assertEquals(1,this.csvService.fetchQuestionSet("1","MultipleChoice",1).size());
        assertEquals(2,this.csvService.fetchQuestionSet("1","MultipleChoice",500).size());

        assertEquals(2,this.csvService.fetchQuestionSet("1","Mixed",2).size());
    }

    @Test
    void getIdFromQuestions() throws Exception {
        ArrayList<Question> combinedList = new ArrayList<>();
        combinedList.addAll(hanziDrawingList);
        combinedList.addAll(multipleChoiceList);

        assertEquals(Arrays.asList(1,2),Whitebox.invokeMethod(this.spyService,"getIdFromQuestions",hanziDrawingList));
        assertEquals(Arrays.asList(1,2),Whitebox.invokeMethod(this.spyService,"getIdFromQuestions",multipleChoiceList));
        assertEquals(Arrays.asList(1,2,1,2),Whitebox.invokeMethod(this.spyService,"getIdFromQuestions",combinedList));
    }


    @Test
    void randomSelect() throws Exception {

        when(this.spyService.hanziDrawingRepository.findById(any())).thenReturn(Optional.ofNullable(new HanziDrawing()));
        when(this.spyService.multipleChoiceRepository.findById(any())).thenReturn(Optional.ofNullable(new MultipleChoice()));

        ArrayList list = (ArrayList) Whitebox.invokeMethod(this.spyService,"randomSelect","HanziDrawing",Arrays.asList(1,2),3);
        assertEquals(2,list.size());

        list = (ArrayList) Whitebox.invokeMethod(this.spyService,"randomSelect","HanziDrawing",Arrays.asList(1,2),1);
        assertEquals(1,list.size());

        list = (ArrayList) Whitebox.invokeMethod(this.spyService,"randomSelect","MultipleChoice",Arrays.asList(1,2),80);
        assertEquals(2,list.size());

        list = (ArrayList) Whitebox.invokeMethod(this.spyService,"randomSelect","MultipleChoice",Arrays.asList(1,2),1);
        assertEquals(1,list.size());

    }
}