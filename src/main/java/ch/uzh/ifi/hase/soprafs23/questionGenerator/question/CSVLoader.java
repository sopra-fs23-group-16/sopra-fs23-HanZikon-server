package ch.uzh.ifi.hase.soprafs23.questionGenerator.question;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO.*;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CSVLoader {

    final CSVService service;

    public CSVLoader(CSVService service) throws IOException {
        this.service = service;
        loadMultiChoiceOntoDatabase();
        loadHanziDrawingOntoDatabase();
    }

    private void loadMultiChoiceOntoDatabase() throws IOException {
        String fileName = "multiple_choice.csv";

        List<MultipleChoice> beans = new CsvToBeanBuilder(new FileReader(fileName,StandardCharsets.UTF_8))
                .withType(MultipleChoice.class)
                .build()
                .parse();

        service.saveChoiceQuestion(beans);
    }

    private void loadHanziDrawingOntoDatabase() throws IOException {
        String fileName = "HanziDrawingDataset.csv";

        List<HanziDrawing> beans = new CsvToBeanBuilder(new FileReader(fileName, StandardCharsets.UTF_8))
                .withType(HanziDrawing.class)
                .withSeparator(',')
                .build()
                .parse();

        service.saveDrawQuestion(beans);
    }

}
