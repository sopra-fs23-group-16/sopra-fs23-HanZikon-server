package ch.uzh.ifi.hase.soprafs23.questionGenerator.question;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.*;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Component
public class CSVLoader {

    final CSVService service;

    public CSVLoader(CSVService service) throws FileNotFoundException {
        this.service = service;
        loadMultiChoiceOntoDatabase();
        loadHanziDrawingOntoDatabase();
    }

    public void loadMultiChoiceOntoDatabase() throws FileNotFoundException {
        String fileName = ".\\multiple_choice.csv";

        List<MultipleChoice> beans = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(MultipleChoice.class)
                .build()
                .parse();

        service.saveChoiceQuestion(beans);
    }

    private void loadHanziDrawingOntoDatabase() throws FileNotFoundException {
        String fileName = ".\\drawing.csv";

        List<HanziDrawing> beans = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(HanziDrawing.class)
                .build()
                .parse();

        service.saveDrawQuestion(beans);
    }

}
