package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;

import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.HanziDrawing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface HanziDrawDTOMapper {
    @Mapper
    public interface DTOMapper {
        DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);
        @Mapping(source = "id", target = "id")
        @Mapping(source = "character", target = "character")
        @Mapping(source = "evolution", target = "evolution")
        @Mapping(source = "pinyin", target = "pinyin")
        @Mapping(source = "meaning", target = "meaning")
        @Mapping(source = "level", target = "level")
        HanziDrawing convertDTOtoEntity(HanziDrawingDTO hanziDrawingDTO);

        @Mapping(source = "id", target = "id")
        @Mapping(source = "character", target = "character")
        @Mapping(source = "evolution", target = "evolution")
        @Mapping(source = "pinyin", target = "pinyin")
        @Mapping(source = "meaning", target = "meaning")
        @Mapping(source = "level", target = "level")
        @Mapping(target = "questionType",ignore = true)
        HanziDrawingDTO convertEntityToDTO(HanziDrawing hanziDrawing);
    }
}
