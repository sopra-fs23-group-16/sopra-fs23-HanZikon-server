package ch.uzh.ifi.hase.soprafs23.questionGenerator.question.DTO;
import ch.uzh.ifi.hase.soprafs23.questionGenerator.question.entity.MultipleChoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface MultiChoiceDTOMapper {
    @Mapper
    public interface DTOMapper {

        DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

        @Mapping(source = "id", target = "id")
        @Mapping(source = "oracleURL", target = "oracleURL")
        @Mapping(source = "choices", target = "choices")
        @Mapping(source = "answerIndex", target = "answerIndex")
        @Mapping(source = "level", target = "level")

        MultipleChoice convertDTOtoEntity(MultipleChoiceDTO multipleChoiceDTO);

        @Mapping(source = "id", target = "id")
        @Mapping(source = "oracleURL", target = "oracleURL")
        @Mapping(source = "choices", target = "choices")
        @Mapping(source = "answerIndex", target = "answerIndex")
        @Mapping(source = "level", target = "level")
        @Mapping(target = "questionType", ignore = true)
        MultipleChoiceDTO convertEntityToDTO(MultipleChoice multipleChoice);
    }
}
