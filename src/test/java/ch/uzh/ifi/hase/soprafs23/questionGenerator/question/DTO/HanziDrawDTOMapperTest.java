package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HanziDrawDTOMapperTest {
    ArrayList<String> evolution = new ArrayList<>(Arrays.asList("Element A","Element B","Element C","Element D"));

    @Test
    public void test_from_HanziDrawDTO_to_HanziDraw(){
        HanziDrawingDTO hanziDrawingDTO = new HanziDrawingDTO();
        hanziDrawingDTO.setCharacter("test");

        hanziDrawingDTO.setEvolution(evolution);
        hanziDrawingDTO.setLevel("1");
        hanziDrawingDTO.setMeaning("testMeaning");
        hanziDrawingDTO.setPinyin("testPinyin");

        HanziDrawing hanziDrawing = HanziDrawDTOMapper.DTOMapper.INSTANCE.convertDTOtoEntity(hanziDrawingDTO);

        assertEquals("test",hanziDrawing.getCharacter());
        assertEquals(evolution,hanziDrawing.getEvolution());
        assertEquals("1",hanziDrawing.getLevel());
        assertEquals("testMeaning",hanziDrawing.getMeaning());
        assertEquals("testPinyin",hanziDrawing.getPinyin());
    }

    @Test
    public void test_from_HanziDraw_to_HanziDrawDTO(){
        HanziDrawing hanziDrawing = new HanziDrawing();
        hanziDrawing.setCharacter("test");
        hanziDrawing.setEvolution(evolution);
        hanziDrawing.setLevel("1");
        hanziDrawing.setMeaning("testMeaning");
        hanziDrawing.setPinyin("testPinyin");

        HanziDrawingDTO hanziDrawingDTO = HanziDrawDTOMapper.DTOMapper.INSTANCE.convertEntityToDTO(hanziDrawing);

        assertEquals("test",hanziDrawingDTO.getCharacter());
        assertEquals(evolution,hanziDrawingDTO.getEvolution());
        assertEquals("1",hanziDrawingDTO.getLevel());
        assertEquals("testMeaning",hanziDrawingDTO.getMeaning());
        assertEquals("testPinyin",hanziDrawingDTO.getPinyin());
        assertEquals("HanziDrawing",hanziDrawingDTO.getQuestionType());
    }
}